import request from '@/utils/request'

//获取当前登录用户的个人信息（身份取自令牌，无需传参）
export const getPersonalMessage = () => {
  return request.get('/getPersonalMessage')
}

//修改个人信息（学号取自令牌，password 不参与修改）
export const updatePersonalMessage = (personalMessage) => {
  return request.post('/upDatePersonalMessage', personalMessage)
}

//修改密码（需登录，凭原密码修改）
export const updatePassword = (passwordMessage) => {
  return request.post('/updatePassword', passwordMessage)
}

//上传头像，返回图片访问地址
export const uploadAvatar = (formData) => {
  return request.post('/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}