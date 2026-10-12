package com.chat.controller.admin;

import com.chat.constant.JwtClaimsConstant;
import com.chat.dto.AdminLoginDTO;
import com.chat.entity.Admin;
import com.chat.properties.JwtProperties;
import com.chat.result.Result;
import com.chat.service.AdminService;
import com.chat.utils.JwtUtil;
import com.chat.vo.AdminLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端 - 管理员登录
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@Api(tags = "管理端-管理员接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("管理员登录：{}", adminLoginDTO.getUsername());

        Admin admin = adminService.login(adminLoginDTO);

        // 登录成功后生成 jwt 令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, admin.getId());
        claims.put(JwtClaimsConstant.USERNAME, admin.getUsername());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .token(token)
                .build();

        return Result.success(adminLoginVO);
    }

    @PostMapping("/logout")
    @ApiOperation("管理员退出登录")
    public Result logout() {
        // jwt 为无状态令牌，客户端丢弃令牌即可
        return Result.success();
    }
}