package com.chat.service;

import com.chat.dto.EmployeeCheckDTO;
import com.chat.dto.EmployeeDTO;
import com.chat.dto.EmployeeLoginDTO;
import com.chat.dto.PasswordDTO;
import com.chat.entity.Employee;

public interface EmployeeService {


    // 员工登录
    Employee login(EmployeeLoginDTO employeeLoginDTO);


    //员工注册
    void addEmployee(EmployeeDTO employeeDTO);

    String registerCheck(EmployeeCheckDTO employeeCheckDTO);

    void upDatePersonalMessage(EmployeeDTO employeeDTO);

    EmployeeDTO getPersonalMessage(String stuNumber);

    //忘记密码：通过学号 + 手机号校验身份后重置密码
    void forgetPassword(PasswordDTO passwordDTO);

    //修改密码：登录后凭原密码修改
    void updatePassword(PasswordDTO passwordDTO);
}
