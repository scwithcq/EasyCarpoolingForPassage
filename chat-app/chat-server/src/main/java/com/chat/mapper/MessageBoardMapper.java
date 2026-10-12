package com.chat.mapper;

import com.chat.dto.MessagePageQueryDTO;
import com.chat.entity.MessageBoard;
import com.chat.vo.MessageBoardVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MessageBoardMapper {

    //新增留言（parentId 为空表示顶级留言，否则为回复）
    @Insert("insert into chat_app.message_board (message_id, content, stu_number, username, user_image, " +
            "parent_id, reply_username, status, create_time) " +
            "values (#{messageId}, #{content}, #{stuNumber}, #{username}, #{userImage}, " +
            "#{parentId}, #{replyUsername}, #{status}, #{createTime})")
    void insertMessage(MessageBoard messageBoard);

    //分页查询留言列表（仅正常状态）
    Page<MessageBoardVO> getMessageList();

    //管理端：分页查询留言
    Page<MessageBoard> pageQuery(MessagePageQueryDTO messagePageQueryDTO);

    //根据留言编号查询留言
    @Select("select * from chat_app.message_board where message_id = #{messageId}")
    MessageBoard getByMessageId(String messageId);

    //修改留言状态（1正常 0删除）
    @Update("update chat_app.message_board set status = #{status} where message_id = #{messageId}")
    void updateStatus(@Param("messageId") String messageId, @Param("status") Integer status);
}