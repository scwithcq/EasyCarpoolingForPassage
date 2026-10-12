package com.chat.controller.user;


import com.chat.dto.CarpoolOrderDTO;
import com.chat.entity.CarpoolOrder;
import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.CarpoolOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "用户拼车订单接口")
public class CarpoolOrderController {
    @Autowired
    private CarpoolOrderService carpoolOrderService;

    @PostMapping("/addCarpool")
    @ApiOperation("添加拼车订单")
    public Result addCarpool(@RequestBody CarpoolOrderDTO carpoolOrderDTO) {
        log.info("添加拼车订单：{}",carpoolOrderDTO);
        carpoolOrderService.addCarpool(carpoolOrderDTO);
        return Result.success();
    }

    @GetMapping("/getCarpool")
    @ApiOperation("查询拼车订单")
    public Result<PageResult> getCarpool(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询第{}页，每页{}数据",page,pageSize);
        PageResult pageResult = carpoolOrderService.getCarpool(page,pageSize);
        return Result.success(pageResult);
    }

    @GetMapping("/getCarpoolById")
    @ApiOperation("查询拼车订单详情")
    public Result<CarpoolOrder> getCarpoolById(Long id) {
        log.info("查询拼车订单详情：{}", id);
        return Result.success(carpoolOrderService.getCarpoolById(id));
    }

    @GetMapping("/getMyCarpool")
    @ApiOperation("分页查询我发布的拼车")
    public Result<PageResult> getMyCarpool(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询我发布的拼车：第{}页，每页{}条", page, pageSize);
        return Result.success(carpoolOrderService.getMyCarpool(page, pageSize));
    }

    @PutMapping("/updateCarpool")
    @ApiOperation("修改我发布的拼车")
    public Result updateCarpool(@RequestBody CarpoolOrderDTO carpoolOrderDTO) {
        log.info("修改拼车订单：{}", carpoolOrderDTO);
        carpoolOrderService.updateCarpool(carpoolOrderDTO);
        return Result.success();
    }

    @DeleteMapping("/deleteCarpool")
    @ApiOperation("删除我发布的拼车")
    public Result deleteCarpool(Long id) {
        log.info("删除拼车订单：{}", id);
        carpoolOrderService.deleteCarpool(id);
        return Result.success();
    }

    @PostMapping("/joinCarpool")
    @ApiOperation("接单")
    public Result joinCarpool(Long id) {
        log.info("接单：{}", id);
        carpoolOrderService.joinCarpool(id);
        return Result.success();
    }

    @PostMapping("/cancelJoin")
    @ApiOperation("取消接单")
    public Result cancelJoin(Long id) {
        log.info("取消接单：{}", id);
        carpoolOrderService.cancelJoin(id);
        return Result.success();
    }

    @GetMapping("/getJoinList")
    @ApiOperation("查看某拼车的接单列表")
    public Result getJoinList(Long id) {
        log.info("查看接单列表：{}", id);
        return Result.success(carpoolOrderService.getJoinList(id));
    }

    @GetMapping("/getMyJoin")
    @ApiOperation("分页查询我接单的拼车")
    public Result<PageResult> getMyJoin(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询我接单的拼车：第{}页，每页{}条", page, pageSize);
        return Result.success(carpoolOrderService.getMyJoin(page, pageSize));
    }
}