import request from '@/utils/request'

//分页查询已发布公告
export const getAnnouncementList = (page, pageSize) => {
  return request.get('/announcement/getList', {
    params: {
      page,
      pageSize
    }
  })
}

//查询公告详情
export const getAnnouncementById = (id) => {
  return request.get('/announcement/getById', {
    params: { id }
  })
}