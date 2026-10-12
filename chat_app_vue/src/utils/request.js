import axios from "axios"
import { Message } from "element-ui"
import router from "@/router"
import { getToken, clearAuth } from "@/utils/auth"

// 用户端接口统一带 /user 前缀，与后端拦截器路径保持一致
const instance = axios.create({
  baseURL: 'http://localhost:8080/user',
  timeout: 10000
});

// 请求拦截器：统一注入 jwt 令牌（后端从 authentication 头解析用户身份）
instance.interceptors.request.use(function (config) {
  const token = getToken();
  if (token) {
    config.headers['authentication'] = token;
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});

// 响应拦截器：后端统一返回 Result{code,msg,data}，code=1 为成功
instance.interceptors.response.use(function (response) {
  const res = response.data;
  if (res.code === 1) {
    return res;
  }
  Message.error(res.msg || '请求失败');
  // 令牌缺失或过期：清理登录态并回到登录页
  if (res.msg === '用户未登录') {
    clearAuth();
    router.replace('/login');
  }
  return Promise.reject(new Error(res.msg || '请求失败'));
}, function (error) {
  Message.error('网络异常，请稍后重试');
  return Promise.reject(error);
});

export default instance
