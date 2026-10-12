import request from '@/utils/request'

//发布拼车
export const addCarpool = (addCarpoolMessage) => {
  return request.post('/order/addCarpool', addCarpoolMessage)
}

//分页查询所有拼车信息
export const getCarpool = (page, pageSize) => {
  return request.get('/order/getCarpool', {
    params: {
      page,
      pageSize
    }
  })
}

//查询拼车详情
export const getCarpoolById = (id) => {
  return request.get('/order/getCarpoolById', {
    params: { id }
  })
}

//分页查询我发布的拼车
export const getMyCarpool = (page, pageSize) => {
  return request.get('/order/getMyCarpool', {
    params: {
      page,
      pageSize
    }
  })
}

//修改我发布的拼车
export const updateCarpool = (carpoolMessage) => {
  return request.put('/order/updateCarpool', carpoolMessage)
}

//删除我发布的拼车
export const deleteCarpool = (id) => {
  return request.delete('/order/deleteCarpool', {
    params: { id }
  })
}

//接单
export const joinCarpool = (id) => {
  return request.post('/order/joinCarpool', null, {
    params: { id }
  })
}

//取消接单
export const cancelJoin = (id) => {
  return request.post('/order/cancelJoin', null, {
    params: { id }
  })
}

//查看某拼车的接单列表
export const getJoinList = (id) => {
  return request.get('/order/getJoinList', {
    params: { id }
  })
}

//分页查询我接单的拼车
export const getMyJoin = (page, pageSize) => {
  return request.get('/order/getMyJoin', {
    params: {
      page,
      pageSize
    }
  })
}
