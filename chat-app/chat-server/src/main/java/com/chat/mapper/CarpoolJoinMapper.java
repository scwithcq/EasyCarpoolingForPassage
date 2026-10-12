package com.chat.mapper;

import com.chat.entity.CarpoolJoin;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarpoolJoinMapper {

    //查询某人是否已接某单
    @Select("select * from chat_app.carpool_join where order_id = #{orderId} and stu_number = #{stuNumber}")
    CarpoolJoin getByOrderAndStu(@Param("orderId") String orderId, @Param("stuNumber") String stuNumber);

    //新增接单记录
    @Insert("insert into carpool_join (order_id, stu_number, username, phone, create_time) " +
            "values (#{orderId}, #{stuNumber}, #{username}, #{phone}, #{createTime})")
    void insert(CarpoolJoin carpoolJoin);

    //取消接单
    @Delete("delete from chat_app.carpool_join where order_id = #{orderId} and stu_number = #{stuNumber}")
    void deleteByOrderAndStu(@Param("orderId") String orderId, @Param("stuNumber") String stuNumber);

    //查询某拼车的接单列表
    @Select("select * from chat_app.carpool_join where order_id = #{orderId} order by create_time asc")
    List<CarpoolJoin> getJoinList(String orderId);

    //分页查询我接单的拼车（仅正常状态的拼车）
    Page<com.chat.entity.CarpoolOrder> getMyJoin(String stuNumber);
}
