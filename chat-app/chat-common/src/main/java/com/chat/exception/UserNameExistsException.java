package com.chat.exception;

public class UserNameExistsException extends BaseException{

    public UserNameExistsException(){

    };

    public UserNameExistsException(String msg){
        super(msg);
    }

}
