package com.chat.mapper;

import com.chat.dto.EmployeeCheckDTO;
import com.chat.dto.EmployeePageQueryDTO;
import com.chat.entity.Employee;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

//员工web端  后续在搞管理端
@Mapper
public interface EmployeeMapper {

    //通过学号查询个人信息
    @Select("select * from chat_app.employee where stuNumber = #{stuNumber}")
    Employee getByStuNumber(String stuNumber);

    //注册校验
    @Select("select * from chat_app.employee where stuNumber = #{stuNumber} or username = #{username} or phone = #{phone}")
    List<Employee> getRegisterCheck(EmployeeCheckDTO employeeCheckDTO);

/*
    //校验注册信息是否存在
    @Select("select username,stuNumber,phone from employee where username=#{username} OR stuNumber=#{stuNumber} OR phone=#{phone}")
    EmployeeCheckDTO registerCheck(String username, String stuNumber, String phone);
*/

/*
    //查询注册时用户输入的数据是否已存在
    @Select("SELECT * FROM chat_app.employee AS sub  WHERE sub.username = #{userName} OR sub.stuNumber = #{stuNumber} OR sub.phone = #{phone} ")
    List<EmployeeRegisterDTO> getRegisterExistsMessage(String userName, String stuNumber, String phone);
*/


    //新增用户
    @Insert("insert into employee (stuNumber, userImage, username, password, phone, sex," +
            " create_time, update_time, create_user, update_user,status)" +
            " values(#{stuNumber},#{userImage},#{username},#{password},#{phone},#{sex}," +
            "#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void insertEmployee(Employee employee);

    //更新用户个人信息
    void upDatePersonalMessage(Employee employee);

    //通过用户名查询信息
    @Select("select * from chat_app.employee where username = #{username}")
    Employee getByUsername(String username);

    //管理端：分页查询用户（不返回密码）
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    //管理端：根据id查询用户
    @Select("select * from chat_app.employee where id = #{id}")
    Employee getById(Long id);

    //管理端：启用/封禁用户
    @Update("update chat_app.employee set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    //管理端：删除用户
    @Delete("delete from chat_app.employee where id = #{id}")
    void deleteById(Long id);

    //修改密码（按学号定位，密码为 MD5 密文）
    @Update("update chat_app.employee set password = #{password}, update_time = now() where stuNumber = #{stuNumber}")
    void updatePasswordByStuNumber(@Param("stuNumber") String stuNumber, @Param("password") String password);
}
