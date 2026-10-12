package com.chat.service;

import com.chat.dto.ForumCommentPageQueryDTO;
import com.chat.dto.ForumPostPageQueryDTO;
import com.chat.entity.ForumCategory;
import com.chat.result.PageResult;

import java.util.List;

public interface AdminForumService {

    //分页查询帖子
    PageResult pageQueryPost(ForumPostPageQueryDTO forumPostPageQueryDTO);

    //删除帖子（逻辑删除）
    void deletePost(String postId);

    //修改帖子状态
    void updatePostStatus(String postId, Integer status);

    //分页查询评论
    PageResult pageQueryComment(ForumCommentPageQueryDTO forumCommentPageQueryDTO);

    //删除评论（逻辑删除，同时维护帖子评论数）
    void deleteComment(String commentId);

    //修改评论状态
    void updateCommentStatus(String commentId, Integer status);

    //查询全部分类
    List<ForumCategory> getCategoryList();

    //新增分类
    void addCategory(ForumCategory forumCategory);

    //修改分类
    void updateCategory(ForumCategory forumCategory);

    //删除分类
    void deleteCategory(Long id);
}