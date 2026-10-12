import Vue from 'vue'
import VueRouter from 'vue-router'
import { getToken } from '@/utils/auth'

import Login from '@/views/login.vue'
import Home from '@/views/HomePages/home.vue'
import Register from '@/views/register.vue'
import Layout from '@/views/HomePages/layout.vue'
import carpooling_information from '@/views/HomePages/carpooling_information.vue'
import exchange_forum from '@/views/HomePages/exchange_forum.vue'
import publish_information from '@/views/HomePages/publish_information.vue'
import message_feedback from '@/views/HomePages/message_feedback.vue'
import personal_Information from '@/views/HomePages/personal_Information.vue'
import chat_room from '@/views/HomePages/chat_room.vue'



Vue.use(VueRouter)

const router = new VueRouter({
    mode: 'history',
    routes: [
        { path: '/', redirect: 'login' },
        { path: '/login', component: Login },
        { path: '/register', component: Register },
        {
            path: '/layout', component: Layout,
            children: [
                // 首页
                { path: '/layout/home', component: Home },
                // 拼车信息
                { path: '/layout/carpooling_information', component: carpooling_information },
                // 交流论坛
                { path: '/layout/exchange_forum', component: exchange_forum },
                // 公告信息
                { path: '/layout/publish_information', component: publish_information },
                // 留言反馈
                { path: '/layout/message_feedback', component: message_feedback },
                // 个人中心 后续集成到个人信息中心模块里
                { path: '/layout/personal_Information', component: personal_Information },
                // 聊天室
                { path: '/layout/chat_room', component: chat_room },

            ]
        }
    ]
})

// 免登录页面
const whiteList = ['/login', '/register']

// 全局前置守卫：未携带令牌时禁止进入需登录页面
router.beforeEach((to, from, next) => {
    if (whiteList.indexOf(to.path) !== -1) {
        next()
        return
    }
    if (getToken()) {
        next()
        return
    }
    next('/login')
})

export default router
