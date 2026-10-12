<template>
  <el-container style="height:100vh;">
    <el-header height="60px">
      <div class="header_top">
        <p>校易通</p>
        <div class="header_right">
          <span class="current_user">{{ currentUsername }}</span>
          <button class="button_quit" @click="RegisterSend">退出登陆</button>
        </div>
      </div>
    </el-header>
    <el-container>
      <el-aside width="150px">
        <ul class="asideList">
          <li>
            <router-link to="/layout/home">系统首页</router-link>
          </li>
          <li>
            <router-link to="/layout/carpooling_information">拼车信息</router-link>
          </li>
          <li>
            <router-link to="/layout/exchange_forum">交流论坛</router-link>
          </li>
          <li>
            <router-link to="/layout/publish_information">公告信息</router-link>
          </li>
          <li>
            <router-link to="/layout/message_feedback">留言反馈</router-link>
          </li>
          <li>
            <router-link to="/layout/chat_room">
              <span class="menu_text">聊天</span>
              <span v-if="unreadCount > 0" class="unread_badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
            </router-link>
          </li>
          <li>
            <router-link to="/layout/personal_Information">个人信息</router-link>
          </li>
        </ul>

        <!-- 添加 -->
        <div class="position_buttom">
          <input_information></input_information>
        </div>
      </el-aside>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import input_information from '@/components/input_information.vue'
import { getUserInfo, clearAuth } from '@/utils/auth'
import { addSocketListener, connectSocket, disconnectSocket } from '@/utils/websocket'
import { getUnreadCount } from '@/api/chat'
export default {
  components: {
    input_information
  },
  name: 'layout',
  created() {
    const userInfo = getUserInfo()
    this.currentUsername = userInfo ? userInfo.username : ''
    this.unwatch = addSocketListener(this.handleSocketEvent)
    if (this.currentUsername) {
      connectSocket(this.currentUsername)
      this.refreshUnread()
    }
  },
  beforeDestroy() {
    if (this.unwatch) {
      this.unwatch()
    }
    disconnectSocket()
  },
  watch: {
    // 切换页面时刷新未读数（离开聊天页时服务端已完成已读标记）
    $route() {
      this.refreshUnread()
    }
  },
  data() {
    return {
      currentUsername: '',
      unreadCount: 0
    }
  },
  methods: {
    RegisterSend() {
      disconnectSocket()
      clearAuth()
      this.$router.replace('/login')
    },
    refreshUnread() {
      getUnreadCount()
        .then((response) => {
          this.unreadCount = response.data || 0
        })
        .catch(() => { })
    },
    handleSocketEvent(payload) {
      if (payload.type === 'message') {
        // 正在聊天页时由 chat_room 负责展示与标记已读，不累加角标
        if (this.$route.path !== '/layout/chat_room') {
          this.unreadCount++
        }
        return
      }
      // chat_room 标记已读后刷新角标
      if (payload.type === 'messagesRead') {
        this.refreshUnread()
      }
    }
  }
}
</script>

<style scoped>
.position_buttom {
  padding-top: 350px;
  border-radius: 10px;
}

.button_quit {
  width: 70px;
  height: 40px;
  margin: 10px;
  border-radius: 5px;
}


.header_top {
  display: flex;
  justify-content: space-between;
}

.header_right {
  display: flex;
  align-items: center;
}

.current_user {
  font-size: 16px;
  color: #333;
}


.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
  font-size: 30px;
}

.el-aside {
  background-color: #303133;
}

.el-main {
  background-color: #E9EEF3;
  color: #333;
}

.asideList,
li {
  width: 100%;
}

.asideList li {
  height: 40px;
  text-align: center;
  line-height: 40px;
}

.asideList li a {
  display: inline-block;
  width: 100%;
  height: 100%;
  color: #ffffff;
  transition: background-color 0.1s;
  position: relative;
}

.unread_badge {
  position: absolute;
  top: 6px;
  right: 16px;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  padding: 0 4px;
  background-color: #F56C6C;
  color: #ffffff;
  font-size: 11px;
  border-radius: 8px;
  text-align: center;
}

.asideList li .router-link-active {
  background-color: #E6A23C;
}

.asideList li a:hover {
  background-color: #909399;
}
</style>