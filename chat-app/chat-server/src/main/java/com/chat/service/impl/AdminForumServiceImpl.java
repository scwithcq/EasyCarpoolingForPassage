package com.chat.service.impl;

import com.chat.constant.MessageConstant;
import com.chat.constant.StatusConstant;
import com.chat.dto.ForumCommentPageQueryDTO;
import com.chat.dto.ForumPostPageQueryDTO;
import com.chat.entity.ForumCategory;
import com.chat.entity.ForumComment;
import com.chat.entity.ForumPost;
import com.chat.exception.ForumBusinessException;
import com.chat.mapper.ForumCategoryMapper;
import com.chat.mapper.ForumCommentMapper;
import com.chat.mapper.ForumPostMapper;
import com.chat.result.PageResult;
import com.chat.service.AdminForumService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AdminForumServiceImpl implements AdminForumService {

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumCommentMapper forumCommentMapper;

    @Autowired
    private ForumCategoryMapper forumCategoryMapper;

    @Override
    public PageResult pageQueryPost(ForumPostPageQueryDTO forumPostPageQueryDTO) {
        PageHelper.startPage(forumPostPageQueryDTO.getPage(), forumPostPageQueryDTO.getPageSize());
        Page<ForumPost> page = forumPostMapper.pageQuery(forumPostPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deletePost(String postId) {
        ForumPost forumPost = forumPostMapper.getByPostId(postId);
        if (forumPost == null) {
            throw new ForumBusinessException(MessageConstant.POST_NOT_FOUND);
        }
        forumPostMapper.updateStatus(postId, StatusConstant.DISABLE);
    }

    @Override
    public void updatePostStatus(String postId, Integer status) {
        ForumPost forumPost = forumPostMapper.getByPostId(postId);
        if (forumPost == null) {
            throw new ForumBusinessException(MessageConstant.POST_NOT_FOUND);
        }
        forumPostMapper.updateStatus(postId, status);
    }

    @Override
    public PageResult pageQueryComment(ForumCommentPageQueryDTO forumCommentPageQueryDTO) {
        PageHelper.startPage(forumCommentPageQueryDTO.getPage(), forumCommentPageQueryDTO.getPageSize());
        Page<ForumComment> page = forumCommentMapper.pageQuery(forumCommentPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteComment(String commentId) {
        ForumComment forumComment = forumCommentMapper.getByCommentId(commentId);
        if (forumComment == null) {
            throw new ForumBusinessException(MessageConstant.COMMENT_NOT_FOUND);
        }
        forumCommentMapper.updateStatus(commentId, StatusConstant.DISABLE);
        // 同步维护帖子的评论数（仅首次删除时扣减）
        if (StatusConstant.ENABLE.equals(forumComment.getStatus())) {
            forumPostMapper.updateCommentCount(forumComment.getPostId(), -1);
        }
    }

    @Override
    public void updateCommentStatus(String commentId, Integer status) {
        ForumComment forumComment = forumCommentMapper.getByCommentId(commentId);
        if (forumComment == null) {
            throw new ForumBusinessException(MessageConstant.COMMENT_NOT_FOUND);
        }
        if (status.equals(forumComment.getStatus())) {
            forumCommentMapper.updateStatus(commentId, status);
            return;
        }
        forumCommentMapper.updateStatus(commentId, status);
        // 恢复评论则评论数 +1，删除评论则评论数 -1
        forumPostMapper.updateCommentCount(forumComment.getPostId(),
                StatusConstant.ENABLE.equals(status) ? 1 : -1);
    }

    @Override
    public List<ForumCategory> getCategoryList() {
        return forumCategoryMapper.getAllList();
    }

    @Override
    public void addCategory(ForumCategory forumCategory) {
        if (forumCategory.getStatus() == null) {
            forumCategory.setStatus(StatusConstant.ENABLE);
        }
        forumCategoryMapper.insert(forumCategory);
    }

    @Override
    public void updateCategory(ForumCategory forumCategory) {
        ForumCategory category = forumCategoryMapper.getById(forumCategory.getId());
        if (category == null) {
            throw new ForumBusinessException(MessageConstant.CATEGORY_NOT_FOUND);
        }
        forumCategoryMapper.update(forumCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        ForumCategory category = forumCategoryMapper.getById(id);
        if (category == null) {
            throw new ForumBusinessException(MessageConstant.CATEGORY_NOT_FOUND);
        }
        forumCategoryMapper.deleteById(id);
    }
}