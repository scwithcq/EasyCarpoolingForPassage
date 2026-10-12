package com.chat.controller.user;

import com.chat.component.CurrentUserHelper;
import com.chat.component.FileUploadValidator;
import com.chat.constant.JwtClaimsConstant;
import com.chat.dto.EmployeeDTO;
import com.chat.dto.EmployeeLoginDTO;
import com.chat.dto.EmployeeCheckDTO;
import com.chat.dto.PasswordDTO;
import com.chat.entity.Employee;
import com.chat.properties.JwtProperties;
import com.chat.result.Result;
import com.chat.service.EmployeeService;
import com.chat.utils.AliOssUtil;
import com.chat.utils.JwtUtil;
import com.chat.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 用户端
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private CurrentUserHelper currentUserHelper;

    //用户端登录
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("用户登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, employee.getId());
        claims.put(JwtClaimsConstant.USERNAME, employee.getUsername());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .stuNumber(employee.getStuNumber())
                .username(employee.getUsername())
                .userImage(employee.getUserImage())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }


    //员工端注册
    //新增员工
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增用户：{}",employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

/*    //注册校验
    @PostMapping("/register/check")
    @ApiOperation("注册校验")
    public  Result registerCheck(@RequestBody EmployeeRegisterDTO employeeRegisterDTO){
        log.info("注册校验：{}",employeeRegisterDTO);
        employeeService.registerCheck(employeeRegisterDTO);
        return Result.success();

    }*/

    //注册校验
    @PostMapping("/registerCheck")
    @ApiOperation("注册校验")
    public Result registerCheck(@RequestBody EmployeeCheckDTO employeeCheckDTO) {
        log.info("注册校验：{}",employeeCheckDTO);
        String result = employeeService.registerCheck(employeeCheckDTO);
        log.info("结果：{}",result);
        if (result == null) {
            return Result.success();
        }else {
            return Result.error(result);
        }

    }


    //上传头像
    @PostMapping("/upload")
    @ApiOperation("上传头像")
    public Result upload(MultipartFile file){
        log.info("上传头像：{}",file);
        // 校验文件类型与大小
        String extension = FileUploadValidator.validateAndGetExtension(file);
        try {
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            String imageUrl = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(imageUrl);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.error("文件上传失败！");
    }

//    //忘记密码
//    @PostMapping

    //修改个人信息
    @PostMapping("/upDatePersonalMessage")
    @ApiOperation("修改个人相关信息")
    public Result upDatePersonalMessage(@RequestBody EmployeeDTO employeeDTO){
        // 学号取自登录态，防止越权修改他人信息
        Employee currentUser = currentUserHelper.getCurrentUser();
        employeeDTO.setStuNumber(currentUser.getStuNumber());
        log.info("将学号{}的用户信息更新为：{}",currentUser.getStuNumber(),employeeDTO);
        employeeService.upDatePersonalMessage(employeeDTO);
        return Result.success(employeeDTO);
    }

    //获取用户个人信息
    @GetMapping("getPersonalMessage")
    @ApiOperation("获取用户个人信息")
    public Result getPersonalMessage(){
        // 学号取自登录态，无需前端传参
        Employee currentUser = currentUserHelper.getCurrentUser();
        log.info("获取用户个人信息，学号：{}",currentUser.getStuNumber());
        EmployeeDTO employeeDTO = employeeService.getPersonalMessage(currentUser.getStuNumber());
        return Result.success(employeeDTO);
    }

    //忘记密码（无需登录：学号 + 手机号校验身份）
    @PostMapping("/forgetPassword")
    @ApiOperation("忘记密码")
    public Result forgetPassword(@RequestBody PasswordDTO passwordDTO){
        log.info("忘记密码，学号：{}", passwordDTO.getStuNumber());
        employeeService.forgetPassword(passwordDTO);
        return Result.success();
    }

    //修改密码（需登录：凭原密码修改）
    @PostMapping("/updatePassword")
    @ApiOperation("修改密码")
    public Result updatePassword(@RequestBody PasswordDTO passwordDTO){
        log.info("修改密码");
        employeeService.updatePassword(passwordDTO);
        return Result.success();
    }


}
