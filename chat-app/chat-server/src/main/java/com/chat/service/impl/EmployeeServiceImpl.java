package com.chat.service.impl;

import com.chat.component.CurrentUserHelper;
import com.chat.constant.MessageConstant;
import com.chat.constant.StatusConstant;
import com.chat.dto.EmployeeDTO;
import com.chat.dto.EmployeeLoginDTO;
import com.chat.dto.EmployeeCheckDTO;
import com.chat.dto.PasswordDTO;
import com.chat.entity.Employee;
import com.chat.exception.*;
import com.chat.mapper.EmployeeMapper;
import com.chat.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class EmployeeServiceImpl  implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private CurrentUserHelper currentUserHelper;

    // 员工登录
    public Employee login(EmployeeLoginDTO employeeLoginDTO){
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();
        String stuNumber = employeeLoginDTO.getStuNumber();
        System.out.println("------------------------");
        System.out.println("username：" + username);
        System.out.println("stuNumber：" + stuNumber);
        System.out.println("password：" + password);
        System.out.println("------------------------");
        Employee employee = new Employee();

        if (username!=null){
            employee = employeeMapper.getByUsername(username);
            checkUsernameOrStuNumberExist(password, employee);
        }else if (stuNumber!=null){
            employee = employeeMapper.getByStuNumber(stuNumber);
            checkUsernameOrStuNumberExist(password, employee);

        }
        return employee;

    }

    public Employee checkUsernameOrStuNumberExist(String password, Employee employee){

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端用户输入的密码进行MD5加密 并与数据库加密密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //账号状态校验
        if (StatusConstant.DISABLE.equals(employee.getStatus())) {
            //账号被锁定（封禁）
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;

    }

    //注册
    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        //信息不重复，可注册成功
        employee.setStatus(StatusConstant.ENABLE);//ENABLE=1 DISABLE=0
        // 头像允许为空，但历史库中 userImage 为 not null，这里统一兜底为空串，避免注册失败
        if (!StringUtils.hasText(employee.getUserImage())) {
            employee.setUserImage("");
        }
        //注册时对密码进行MD5加密后入库
        employee.setPassword(DigestUtils.md5DigestAsHex(employee.getPassword().getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //设置创始人与修改人ID

        /*employee.setCreateUser(BaseContext.getCurrentId());*/
        /*employee.setUpdateUser(BaseContext.getCurrentId());*/

        //将这所有的数据存储到数据库中
        employeeMapper.insertEmployee(employee);
    }

    //注册校验
    @Override
    public String registerCheck(EmployeeCheckDTO employeeCheckDTO) {
        String stuNumber = employeeCheckDTO.getStuNumber();
        String username = employeeCheckDTO.getUsername();
        String phone = employeeCheckDTO.getPhone();
        List<Employee> employees = new ArrayList<>();
        employees = employeeMapper.getRegisterCheck(employeeCheckDTO);
        System.out.println(employees);
        if (employees.isEmpty()) {
            return null;
        } else {
            for (Employee employee : employees) {
                if (employee.getStuNumber().equals(stuNumber)) {
                    return "学号已存在!";
                } else if (employee.getUsername().equals(username)) {
                    return "用户名已存在!";
                } else if (employee.getPhone().equals(phone)) {
                    return "手机号已存在!";
                }
            }
        }
        return null;
    }

    //更新用户信息
    @Override
    public void upDatePersonalMessage(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        Employee empByStuNumber = employeeMapper.getByStuNumber(employeeDTO.getStuNumber());
        if (empByStuNumber.equals(employee)) {
            System.out.println("不需要进行操作");
        }else {
            employeeMapper.upDatePersonalMessage(employee);
        }

    }

    @Override
    public EmployeeDTO getPersonalMessage(String stuNumber) {
        Employee employee = employeeMapper.getByStuNumber(stuNumber);
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee,employeeDTO);
        // 密码哈希属于敏感字段，不下发给前端
        employeeDTO.setPassword(null);
        return employeeDTO;
    }

    //忘记密码：学号 + 手机号校验通过后重置密码
    @Override
    public void forgetPassword(PasswordDTO passwordDTO) {
        if (!StringUtils.hasText(passwordDTO.getStuNumber())
                || !StringUtils.hasText(passwordDTO.getPhone())
                || !StringUtils.hasText(passwordDTO.getNewPassword())) {
            throw new PasswordEditFailedException(MessageConstant.PARAM_ERROR);
        }
        Employee employee = employeeMapper.getByStuNumber(passwordDTO.getStuNumber());
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (!passwordDTO.getPhone().equals(employee.getPhone())) {
            throw new PasswordEditFailedException(MessageConstant.PHONE_NOT_MATCH);
        }
        employeeMapper.updatePasswordByStuNumber(employee.getStuNumber(),
                DigestUtils.md5DigestAsHex(passwordDTO.getNewPassword().getBytes()));
    }

    //修改密码：校验原密码后更新
    @Override
    public void updatePassword(PasswordDTO passwordDTO) {
        if (!StringUtils.hasText(passwordDTO.getOldPassword())
                || !StringUtils.hasText(passwordDTO.getNewPassword())) {
            throw new PasswordEditFailedException(MessageConstant.PARAM_ERROR);
        }
        Employee currentUser = currentUserHelper.getCurrentUser();
        String oldPassword = DigestUtils.md5DigestAsHex(passwordDTO.getOldPassword().getBytes());
        if (!oldPassword.equals(currentUser.getPassword())) {
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_ERROR);
        }
        employeeMapper.updatePasswordByStuNumber(currentUser.getStuNumber(),
                DigestUtils.md5DigestAsHex(passwordDTO.getNewPassword().getBytes()));
    }


}
