package com.chat.exception;

public class PhoneExistsException extends BaseException{

    public PhoneExistsException(){

    };

    public PhoneExistsException(String msg){
        super(msg);
    }

}
