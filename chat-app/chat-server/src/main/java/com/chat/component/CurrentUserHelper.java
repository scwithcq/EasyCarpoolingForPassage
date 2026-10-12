package com.chat.component;

import com.chat.constant.MessageConstant;
import com.chat.context.BaseContext;
import com.chat.entity.Employee;
import com.chat.exception.AccountNotFoundException;
import com.chat.exception.UserNotLoginException;
import com.chat.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 当前登录用户工具：由 jwt 解析出的 id 反查用户信息
 */
@Component
public class CurrentUserHelper {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 获取当前登录用户，未登录或用户不存在时抛出对应业务异常
     */
    public Employee getCurrentUser() {
        Long id = BaseContext.getCurrentId();
        if (id == null) {
            throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        }
        Employee employee = employeeMapper.getById(id);
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return employee;
    }
}