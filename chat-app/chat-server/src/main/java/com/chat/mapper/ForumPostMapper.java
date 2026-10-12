package com.chat.mapper;

import com.chat.dto.ForumPostPageQueryDTO;
import com.chat.entity.ForumPost;
import com.chat.vo.ForumPostVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ForumPostMapper {

    //新增帖子
    void addPost(ForumPost forumPost);

    //分页查询帖子列表（可按分类筛选），返回带分类名的 VO
    List<ForumPostVO> getPostList(@Param("categoryId") Long categoryId);

    //查询帖子详情（含分类名，仅返回正常状态）
    ForumPostVO getPostById(@Param("postId") String postId);

    //查询我发布的帖子
    List<ForumPostVO> getMyPost(@Param("stuNumber") String stuNumber);

    //管理端：分页查询帖子（含已删除，支持条件筛选）
    Page<ForumPost> pageQuery(ForumPostPageQueryDTO forumPostPageQueryDTO);

    //根据帖子编号查询帖子
    @Select("select * from chat_app.forum_post where post_id = #{postId}")
    ForumPost getByPostId(String postId);

    //修改帖子状态（1正常 0删除）
    @Update("update chat_app.forum_post set status = #{status} where post_id = #{postId}")
    void updateStatus(@Param("postId") String postId, @Param("status") Integer status);

    //增减帖子评论数
    @Update("update chat_app.forum_post set comment_count = comment_count + #{delta} where post_id = #{postId}")
    void updateCommentCount(@Param("postId") String postId, @Param("delta") Integer delta);

    //增减帖子点赞数
    @Update("update chat_app.forum_post set like_count = like_count + #{delta} where post_id = #{postId}")
    void updateLikeCount(@Param("postId") String postId, @Param("delta") Integer delta);

    //帖子浏览量 +1
    @Update("update chat_app.forum_post set view_count = view_count + 1 where post_id = #{postId}")
    void increaseViewCount(String postId);
}