package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    private String stuNumber;

    private String userImage;

    private String username;

    //    private String name;
    private String password;

    private String phone;

    private Long sex;



//    private String idNumber;

}