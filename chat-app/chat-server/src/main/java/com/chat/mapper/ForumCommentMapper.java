package com.chat.mapper;

import com.chat.dto.ForumCommentPageQueryDTO;
import com.chat.entity.ForumComment;
import com.chat.vo.ForumCommentVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ForumCommentMapper {

    //新增评论（parentId 为空表示顶级评论，否则为回复）
    @Insert("insert into chat_app.forum_comment (comment_id, post_id, content, stu_number, username, " +
            "user_image, parent_id, reply_username, like_count, status, create_time) " +
            "values (#{commentId}, #{postId}, #{content}, #{stuNumber}, #{username}, " +
            "#{userImage}, #{parentId}, #{replyUsername}, #{likeCount}, #{status}, #{createTime})")
    void insertComment(ForumComment forumComment);

    //查询某帖子下的全部评论，按时间正序
    List<ForumCommentVO> getCommentListByPostId(@Param("postId") String postId);

    //管理端：分页查询评论
    Page<ForumComment> pageQuery(ForumCommentPageQueryDTO forumCommentPageQueryDTO);

    //根据评论编号查询评论
    @Select("select * from chat_app.forum_comment where comment_id = #{commentId}")
    ForumComment getByCommentId(String commentId);

    //修改评论状态（1正常 0删除）
    @Update("update chat_app.forum_comment set status = #{status} where comment_id = #{commentId}")
    void updateStatus(@Param("commentId") String commentId, @Param("status") Integer status);

    //增减评论点赞数
    @Update("update chat_app.forum_comment set like_count = like_count + #{delta} where comment_id = #{commentId}")
    void updateLikeCount(@Param("commentId") String commentId, @Param("delta") Integer delta);
}