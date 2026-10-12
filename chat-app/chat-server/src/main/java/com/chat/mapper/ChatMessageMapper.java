package com.chat.mapper;

import com.chat.entity.ChatMessage;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ChatMessageMapper {

    //保存消息
    @Insert("insert into chat_app.chat_message (from_name, to_name, content, is_read, create_time) " +
            "values (#{fromName}, #{toName}, #{content}, #{isRead}, #{createTime})")
    void insert(ChatMessage chatMessage);

    //分页查询某会话的历史消息（两个方向）
    Page<ChatMessage> getHistory(@Param("userA") String userA, @Param("userB") String userB);

    //把对方发给我的未读消息标记为已读
    @Update("update chat_app.chat_message set is_read = 1 where to_name = #{toName} and from_name = #{fromName} and is_read = 0")
    void markRead(@Param("fromName") String fromName, @Param("toName") String toName);

    //查询我的未读消息数
    @Select("select count(*) from chat_app.chat_message where to_name = #{toName} and is_read = 0")
    int countUnread(@Param("toName") String toName);
}
