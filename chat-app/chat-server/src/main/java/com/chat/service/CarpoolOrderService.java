package com.chat.service;

import com.chat.dto.CarpoolOrderDTO;
import com.chat.entity.CarpoolOrder;
import com.chat.result.PageResult;

public interface CarpoolOrderService {

    //发布拼车
    void addCarpool(CarpoolOrderDTO carpoolOrderDTO);

    //分页查询拼车列表
    PageResult getCarpool(Integer page, Integer pageSize);

    //查询拼车详情
    CarpoolOrder getCarpoolById(Long id);

    //分页查询我发布的拼车
    PageResult getMyCarpool(Integer page, Integer pageSize);

    //修改我发布的拼车
    void updateCarpool(CarpoolOrderDTO carpoolOrderDTO);

    //删除我发布的拼车
    void deleteCarpool(Long id);

    //接单：接他人的拼车
    void joinCarpool(Long id);

    //取消接单
    void cancelJoin(Long id);

    //查看某拼车的接单列表
    java.util.List<com.chat.entity.CarpoolJoin> getJoinList(Long id);

    //分页查询我接单的拼车
    PageResult getMyJoin(Integer page, Integer pageSize);
}