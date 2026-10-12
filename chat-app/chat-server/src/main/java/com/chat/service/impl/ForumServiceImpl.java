package com.chat.service.impl;

import com.chat.component.CurrentUserHelper;
import com.chat.constant.MessageConstant;
import com.chat.constant.StatusConstant;
import com.chat.dto.ForumCommentDTO;
import com.chat.dto.ForumPostDTO;
import com.chat.entity.Employee;
import com.chat.entity.ForumCategory;
import com.chat.entity.ForumComment;
import com.chat.entity.ForumLike;
import com.chat.entity.ForumPost;
import com.chat.exception.ForumBusinessException;
import com.chat.mapper.ForumCategoryMapper;
import com.chat.mapper.ForumCommentMapper;
import com.chat.mapper.ForumLikeMapper;
import com.chat.mapper.ForumPostMapper;
import com.chat.result.PageResult;
import com.chat.service.ForumService;
import com.chat.vo.ForumCommentVO;
import com.chat.vo.ForumPostVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ForumServiceImpl implements ForumService {

    /**
     * 点赞对象类型：帖子
     */
    private static final Integer TARGET_TYPE_POST = 1;

    /**
     * 点赞对象类型：评论
     */
    private static final Integer TARGET_TYPE_COMMENT = 2;

    @Autowired
    private ForumCategoryMapper forumCategoryMapper;

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumCommentMapper forumCommentMapper;

    @Autowired
    private ForumLikeMapper forumLikeMapper;

    @Autowired
    private CurrentUserHelper currentUserHelper;

    @Override
    public List<ForumCategory> getCategoryList() {
        return forumCategoryMapper.getCategoryList();
    }

    @Override
    public void addPost(ForumPostDTO forumPostDTO) {
        if (!StringUtils.hasText(forumPostDTO.getTitle()) || !StringUtils.hasText(forumPostDTO.getContent())) {
            throw new ForumBusinessException(MessageConstant.PARAM_ERROR);
        }

        Employee currentUser = currentUserHelper.getCurrentUser();

        ForumPost forumPost = new ForumPost();
        BeanUtils.copyProperties(forumPostDTO, forumPost);
        forumPost.setPostId("f" + UUID.randomUUID());
        // 作者信息一律取自登录态，不信任前端传参
        forumPost.setStuNumber(currentUser.getStuNumber());
        forumPost.setUsername(currentUser.getUsername());
        forumPost.setUserImage(currentUser.getUserImage());
        forumPost.setViewCount(0);
        forumPost.setLikeCount(0);
        forumPost.setCommentCount(0);
        forumPost.setStatus(StatusConstant.ENABLE);
        forumPost.setCreateTime(LocalDateTime.now());
        forumPost.setUpdateTime(LocalDateTime.now());
        forumPostMapper.addPost(forumPost);
    }

    @Override
    public PageResult getPostList(Integer page, Integer pageSize, Long categoryId) {
        PageHelper.startPage(page, pageSize);
        Page<ForumPostVO> pageResult = (Page<ForumPostVO>) forumPostMapper.getPostList(categoryId);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Override
    public ForumPostVO getPostById(String postId) {
        ForumPostVO forumPostVO = forumPostMapper.getPostById(postId);
        if (forumPostVO == null) {
            throw new ForumBusinessException(MessageConstant.POST_NOT_FOUND);
        }
        forumPostMapper.increaseViewCount(postId);
        forumPostVO.setViewCount(forumPostVO.getViewCount() + 1);
        return forumPostVO;
    }

    @Override
    public void deletePost(String postId) {
        ForumPost forumPost = forumPostMapper.getByPostId(postId);
        if (forumPost == null) {
            throw new ForumBusinessException(MessageConstant.POST_NOT_FOUND);
        }
        Employee currentUser = currentUserHelper.getCurrentUser();
        if (!currentUser.getStuNumber().equals(forumPost.getStuNumber())) {
            throw new ForumBusinessException(MessageConstant.NO_PERMISSION);
        }
        forumPostMapper.updateStatus(postId, StatusConstant.DISABLE);
    }

    @Override
    public PageResult getMyPost(Integer page, Integer pageSize) {
        Employee currentUser = currentUserHelper.getCurrentUser();
        PageHelper.startPage(page, pageSize);
        Page<ForumPostVO> pageResult = (Page<ForumPostVO>) forumPostMapper.getMyPost(currentUser.getStuNumber());
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Override
    public void addComment(ForumCommentDTO forumCommentDTO) {
        if (!StringUtils.hasText(forumCommentDTO.getContent())) {
            throw new ForumBusinessException(MessageConstant.PARAM_ERROR);
        }
        ForumPost forumPost = forumPostMapper.getByPostId(forumCommentDTO.getPostId());
        if (forumPost == null) {
            throw new ForumBusinessException(MessageConstant.POST_NOT_FOUND);
        }

        Employee currentUser = currentUserHelper.getCurrentUser();

        ForumComment forumComment = new ForumComment();
        BeanUtils.copyProperties(forumCommentDTO, forumComment);
        forumComment.setCommentId("fc" + UUID.randomUUID());
        forumComment.setStuNumber(currentUser.getStuNumber());
        forumComment.setUsername(currentUser.getUsername());
        forumComment.setUserImage(currentUser.getUserImage());
        forumComment.setLikeCount(0);
        forumComment.setStatus(StatusConstant.ENABLE);
        forumComment.setCreateTime(LocalDateTime.now());
        forumCommentMapper.insertComment(forumComment);

        // 同步维护帖子的评论数
        forumPostMapper.updateCommentCount(forumCommentDTO.getPostId(), 1);
    }

    @Override
    public List<ForumCommentVO> getCommentList(String postId) {
        return forumCommentMapper.getCommentListByPostId(postId);
    }

    @Override
    public void deleteComment(String commentId) {
        ForumComment forumComment = forumCommentMapper.getByCommentId(commentId);
        if (forumComment == null) {
            throw new ForumBusinessException(MessageConstant.COMMENT_NOT_FOUND);
        }
        Employee currentUser = currentUserHelper.getCurrentUser();
        if (!currentUser.getStuNumber().equals(forumComment.getStuNumber())) {
            throw new ForumBusinessException(MessageConstant.NO_PERMISSION);
        }
        forumCommentMapper.updateStatus(commentId, StatusConstant.DISABLE);
        // 仅首次删除时扣减帖子评论数
        if (StatusConstant.ENABLE.equals(forumComment.getStatus())) {
            forumPostMapper.updateCommentCount(forumComment.getPostId(), -1);
        }
    }

    @Override
    public void like(Integer targetType, String targetId) {
        if (targetType == null || !StringUtils.hasText(targetId)
                || (!TARGET_TYPE_POST.equals(targetType) && !TARGET_TYPE_COMMENT.equals(targetType))) {
            throw new ForumBusinessException(MessageConstant.PARAM_ERROR);
        }

        Employee currentUser = currentUserHelper.getCurrentUser();
        String stuNumber = currentUser.getStuNumber();

        ForumLike exist = forumLikeMapper.getByTarget(stuNumber, targetType, targetId);
        if (exist != null) {
            // 已点赞 -> 取消点赞
            forumLikeMapper.deleteByTarget(stuNumber, targetType, targetId);
            updateTargetLikeCount(targetType, targetId, -1);
            log.info("用户{}取消点赞{}：{}", stuNumber, targetType, targetId);
        } else {
            ForumLike forumLike = new ForumLike();
            forumLike.setStuNumber(stuNumber);
            forumLike.setTargetType(targetType);
            forumLike.setTargetId(targetId);
            forumLike.setCreateTime(LocalDateTime.now());
            forumLikeMapper.insert(forumLike);
            updateTargetLikeCount(targetType, targetId, 1);
            log.info("用户{}点赞{}：{}", stuNumber, targetType, targetId);
        }
    }

    /**
     * 按点赞对象类型同步维护对应的点赞数
     */
    private void updateTargetLikeCount(Integer targetType, String targetId, int delta) {
        if (TARGET_TYPE_POST.equals(targetType)) {
            forumPostMapper.updateLikeCount(targetId, delta);
        } else {
            forumCommentMapper.updateLikeCount(targetId, delta);
        }
    }
}