/**
 * 聊天 WebSocket 客户端（全局单例）
 *
 * 后端端点：ws://localhost:8080/ws/chat/{username}
 * 上行报文：{ receiveName: 对方用户名, message: 内容 }
 * 下行报文：{ systemMessage: boolean, fromName?: string, message: string | string[] }
 *   - systemMessage=true 且 message 为数组  -> 在线用户列表广播
 *   - systemMessage=true 且 message 为字符串 -> 系统提示（如对方不在线）
 *   - systemMessage=false                   -> 私聊消息，fromName 为发送人
 */
const WS_URL = 'ws://localhost:8080/ws/chat/'

// 断线重连间隔（毫秒）
const RECONNECT_DELAY = 3000

let socket = null
let currentUsername = ''
let manualClosed = false
let reconnectTimer = null
let onlineUsers = []

// 消息监听器集合，页面/布局各自订阅
const listeners = new Set()

function notify(payload) {
  listeners.forEach((fn) => {
    try {
      fn(payload)
    } catch (e) {
      // 单个监听器异常不影响其它监听器
    }
  })
}

function open() {
  const ws = new WebSocket(WS_URL + encodeURIComponent(currentUsername))
  socket = ws
  ws.onopen = () => notify({ type: 'open' })
  ws.onmessage = (event) => {
    let payload
    try {
      payload = JSON.parse(event.data)
    } catch (e) {
      return
    }
    if (payload.systemMessage) {
      if (Array.isArray(payload.message)) {
        onlineUsers = payload.message
        notify({ type: 'onlineUsers', users: onlineUsers })
      } else {
        notify({ type: 'system', text: payload.message })
      }
      return
    }
    notify({ type: 'message', fromName: payload.fromName, content: payload.message })
  }
  ws.onerror = () => notify({ type: 'error' })
  ws.onclose = () => {
    // 已被新连接替换的旧 socket 直接忽略，避免重连定时器重复创建连接
    if (ws !== socket) {
      return
    }
    notify({ type: 'close' })
    if (!manualClosed) {
      reconnectTimer = setTimeout(open, RECONNECT_DELAY)
    }
  }
}

//建立连接（同一用户名重复调用不会重复连接）
export function connectSocket(username) {
  if (!username) {
    return
  }
  if (socket && currentUsername === username
    && (socket.readyState === WebSocket.CONNECTING || socket.readyState === WebSocket.OPEN)) {
    return
  }
  disconnectSocket()
  currentUsername = username
  manualClosed = false
  open()
}

//主动断开（退出登录时调用，不再自动重连）
export function disconnectSocket() {
  manualClosed = true
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  const ws = socket
  socket = null
  if (ws) {
    try {
      ws.close()
    } catch (e) {
      // 忽略关闭异常
    }
  }
  currentUsername = ''
  onlineUsers = []
}

//发送私聊消息，连接不可用时返回 false
export function sendChatMessage(receiveName, message) {
  if (!socket || socket.readyState !== WebSocket.OPEN) {
    return false
  }
  socket.send(JSON.stringify({ receiveName, message }))
  return true
}

//连接是否可用
export function socketReady() {
  return !!socket && socket.readyState === WebSocket.OPEN
}

//最近一次广播的在线用户列表
export function getOnlineUsers() {
  return onlineUsers
}

//订阅消息事件，返回取消订阅函数
export function addSocketListener(fn) {
  listeners.add(fn)
  return () => listeners.delete(fn)
}

//由页面主动广播一个本地事件（不经过服务端），用于页面间同步状态
export function emitLocalEvent(payload) {
  notify(payload)
}