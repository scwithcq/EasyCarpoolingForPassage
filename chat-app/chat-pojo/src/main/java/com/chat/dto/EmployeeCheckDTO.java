package com.chat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工注册时判断相关数据是否重复")
public class EmployeeCheckDTO implements Serializable {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("学号")
    private String stuNumber;

    @ApiModelProperty("手机号")
    private  String phone;

}
