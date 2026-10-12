package com.chat.mapper;

import com.chat.dto.CarpoolPageQueryDTO;
import com.chat.entity.CarpoolOrder;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CarpoolOrderMapper {

    //添加拼车订单信息
    @Insert("insert into order_carpool (order_id, departure, destination, departure_time, " +
            "create_time, update_time, number_of_people, remark, status, stuNumber, username, phone) " +
            "VALUES (#{orderId},#{departure},#{destination},#{departureTime},#{createTime},#{updateTime},#{numberOfPeople}," +
            "#{remark},#{status},#{stuNumber},#{username},#{phone})")
    void addCarpool(CarpoolOrder carpoolOrder);


    //查询拼车订单信息（仅正常状态）
    @Select("select * from order_carpool where status = 1 order by create_time desc")
    Page<CarpoolOrder> getCarpool();

    //用户端：分页查询我发布的拼车
    Page<CarpoolOrder> getMyCarpool(@Param("stuNumber") String stuNumber);

    //用户端：修改拼车信息
    void updateCarpool(CarpoolOrder carpoolOrder);

    //管理端：分页查询拼车订单
    Page<CarpoolOrder> pageQuery(CarpoolPageQueryDTO carpoolPageQueryDTO);

    //根据id查询拼车订单
    @Select("select * from chat_app.order_carpool where id = #{id}")
    CarpoolOrder getById(Long id);

    //修改拼车订单状态
    @Update("update chat_app.order_carpool set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    //更新已接单人数
    @Update("update chat_app.order_carpool set joined_number = #{joinedNumber}, update_time = now() where id = #{id}")
    void updateJoinedNumber(@Param("id") Long id, @Param("joinedNumber") Integer joinedNumber);

    //删除拼车订单
    @Delete("delete from chat_app.order_carpool where id = #{id}")
    void deleteById(Long id);
}