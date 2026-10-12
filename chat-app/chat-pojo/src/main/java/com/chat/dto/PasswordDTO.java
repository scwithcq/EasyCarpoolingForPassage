package com.chat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 密码相关操作入参：修改密码 / 忘记密码
 */
@Data
@ApiModel(description = "密码操作参数")
public class PasswordDTO implements Serializable {

    @ApiModelProperty("学号（忘记密码时使用）")
    private String stuNumber;

    @ApiModelProperty("手机号（忘记密码时用于校验身份）")
    private String phone;

    @ApiModelProperty("原密码（修改密码时使用）")
    private String oldPassword;

    @ApiModelProperty("新密码")
    private String newPassword;
}
