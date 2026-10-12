import request from '@/utils/request'
//登录接口
export const loginMessage = (username,password) => {
    return request.post('/login',{
        username,
        password
    })
}
//注册校验
export const registerCheck = (stuNumber,username,phone) => {
    return request.post('/registerCheck',{
        stuNumber,
        username,
        phone
    })
}
//注册接口
export const registerMessage = (formLabelAlign) => {
  return request.post('/register',formLabelAlign)
}
//忘记密码（登录前使用：学号 + 手机号校验身份后重置密码）
export const forgetPassword = (passwordMessage) => {
  return request.post('/forgetPassword',passwordMessage)
}