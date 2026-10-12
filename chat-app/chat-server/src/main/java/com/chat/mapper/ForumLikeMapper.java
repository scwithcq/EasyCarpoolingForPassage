package com.chat.mapper;

import com.chat.entity.ForumLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ForumLikeMapper {

    /**
     * 查询某用户对某个对象的点赞记录，不存在返回 null
     *
     * @param targetType 点赞对象类型：1-帖子 2-评论
     */
    @Select("select * from chat_app.forum_like where stu_number = #{stuNumber} " +
            "and target_type = #{targetType} and target_id = #{targetId}")
    ForumLike getByTarget(@Param("stuNumber") String stuNumber,
                          @Param("targetType") Integer targetType,
                          @Param("targetId") String targetId);

    //新增点赞记录
    void insert(ForumLike forumLike);

    //取消点赞
    @Delete("delete from chat_app.forum_like where stu_number = #{stuNumber} " +
            "and target_type = #{targetType} and target_id = #{targetId}")
    void deleteByTarget(@Param("stuNumber") String stuNumber,
                        @Param("targetType") Integer targetType,
                        @Param("targetId") String targetId);

    //统计某个对象的点赞数
    @Select("select count(*) from chat_app.forum_like where target_type = #{targetType} and target_id = #{targetId}")
    Integer countByTarget(@Param("targetType") Integer targetType, @Param("targetId") String targetId);
}