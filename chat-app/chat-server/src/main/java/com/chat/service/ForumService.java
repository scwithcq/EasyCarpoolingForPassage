package com.chat.service;

import com.chat.dto.ForumCommentDTO;
import com.chat.dto.ForumPostDTO;
import com.chat.entity.ForumCategory;
import com.chat.result.PageResult;
import com.chat.vo.ForumCommentVO;
import com.chat.vo.ForumPostVO;

import java.util.List;

public interface ForumService {

    //查询帖子分类列表
    List<ForumCategory> getCategoryList();

    //发布帖子
    void addPost(ForumPostDTO forumPostDTO);

    //分页查询帖子列表（可按分类筛选）
    PageResult getPostList(Integer page, Integer pageSize, Long categoryId);

    //查询帖子详情（同时浏览量 +1）
    ForumPostVO getPostById(String postId);

    //删除自己发布的帖子
    void deletePost(String postId);

    //分页查询我发布的帖子
    PageResult getMyPost(Integer page, Integer pageSize);

    //发表评论或回复
    void addComment(ForumCommentDTO forumCommentDTO);

    //查询某帖子的全部评论
    List<ForumCommentVO> getCommentList(String postId);

    //删除自己的评论
    void deleteComment(String commentId);

    /**
     * 点赞或取消点赞（再次调用即取消）
     *
     * @param targetType 点赞对象类型：1-帖子 2-评论
     * @param targetId   帖子编号或评论编号
     */
    void like(Integer targetType, String targetId);
}