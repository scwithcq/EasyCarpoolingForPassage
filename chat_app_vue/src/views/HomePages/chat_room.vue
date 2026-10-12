<template>
    <el-row type="flex" :gutter="16" class="chat_container">
        <!-- 左栏：在线用户 -->
        <el-col :span="7">
            <div class="panel online_panel">
                <div class="panel_title">
                    <span>在线用户</span>
                    <span :class="['status_dot', connected ? 'online' : 'offline']"></span>
                    <span class="status_text">{{ connected ? '已连接' : '未连接' }}</span>
                </div>

                <div class="online_list">
                    <el-tag v-for="user in otherOnlineUsers" :key="user" class="online_tag"
                        :type="user === target ? 'primary' : 'info'" :effect="user === target ? 'dark' : 'plain'"
                        @click="startChat(user)">
                        {{ user }}
                    </el-tag>
                    <div v-if="otherOnlineUsers.length === 0" class="empty_tip">暂无其他在线用户</div>
                </div>

                <div class="manual_box">
                    <div class="manual_label">对方用户名</div>
                    <el-input v-model="manualName" size="small" placeholder="输入用户名后回车"
                        @keyup.enter.native="startManualChat"></el-input>
                    <el-button type="primary" size="small" class="manual_btn" @click="startManualChat">开始聊天
                    </el-button>
                </div>
            </div>
        </el-col>

        <!-- 右栏：会话面板 -->
        <el-col :span="17">
            <div class="panel chat_panel">
                <template v-if="target">
                    <div class="panel_title">
                        <span>正在与 <b>{{ target }}</b> 聊天</span>
                        <span :class="['status_dot', connected ? 'online' : 'offline']"></span>
                        <span class="status_text">{{ connected ? '已连接' : '未连接' }}</span>
                    </div>

                    <div ref="msgList" v-loading="historyLoading" class="msg_list">
                        <div v-if="hasMore" class="load_more">
                            <el-button type="text" size="small" @click="loadMore">加载更早的消息</el-button>
                        </div>
                        <div v-if="messages.length === 0 && !historyLoading" class="empty_tip">
                            还没有聊天记录，发送第一条消息吧~
                        </div>
                        <div v-for="item in messages" :key="item.key"
                            :class="['msg_item', item.kind === 'system' ? 'msg_system' : (item.kind === 'self' ? 'msg_self' : 'msg_other')]">
                            <template v-if="item.kind === 'system'">
                                <span class="system_text">{{ item.content }}</span>
                            </template>
                            <template v-else>
                                <div v-if="item.kind === 'other'" class="msg_author">{{ target }}</div>
                                <div class="bubble">{{ item.content }}</div>
                                <div class="msg_time">{{ item.time }}</div>
                            </template>
                        </div>
                    </div>

                    <div class="send_box">
                        <el-input type="textarea" :rows="2" v-model="content" maxlength="300" show-word-limit
                            placeholder="输入消息，回车发送" @keydown.enter.native.prevent="send"></el-input>
                        <div class="send_footer">
                            <el-button type="primary" size="small" @click="send">发送</el-button>
                        </div>
                    </div>
                </template>

                <div v-else class="no_target">
                    <div class="empty_tip">请选择左侧在线用户，或手动输入对方用户名开始聊天</div>
                </div>
            </div>
        </el-col>
    </el-row>
</template>

<script>
import { getHistory } from '@/api/chat';
import { getUserInfo } from '@/utils/auth';
import { addSocketListener, getOnlineUsers, connectSocket, socketReady, sendChatMessage, emitLocalEvent } from '@/utils/websocket';

export default {
    name: 'chat_room',
    created() {
        const userInfo = getUserInfo();
        this.currentUsername = userInfo ? userInfo.username : '';
        this.onlineUsers = getOnlineUsers();
        this.connected = socketReady();
        // 兜底：若父级 layout 尚未建立连接，则主动建立
        if (!this.connected && this.currentUsername) {
            connectSocket(this.currentUsername);
        }
        this.unwatch = addSocketListener(this.handleSocketEvent);
    },
    beforeDestroy() {
        if (this.unwatch) {
            this.unwatch();
        }
    },
    data() {
        return {
            currentUsername: '',
            connected: false,
            onlineUsers: [],

            target: '',
            manualName: '',

            messages: [],
            historyLoading: false,
            historyPage: 1,
            hasMore: false,
            pageSize: 20,

            content: ''
        }
    },
    computed: {
        // 在线用户中排除自己（不能与自己聊天）
        otherOnlineUsers() {
            return this.onlineUsers.filter((user) => user !== this.currentUsername);
        }
    },
    methods: {
        // 与指定用户开启会话
        startChat(username) {
            if (!username || username === this.currentUsername) {
                this.$message.info('不能和自己聊天');
                return;
            }
            if (username === this.target) {
                return;
            }
            this.target = username;
            this.manualName = '';
            this.messages = [];
            this.loadHistory(username);
        },
        startManualChat() {
            const name = (this.manualName || '').trim();
            if (!name) {
                this.$message.error('请输入对方用户名');
                return;
            }
            this.startChat(name);
        },

        // 先查总数定位到最后一页（最新消息），再取内容展示
        loadHistory(target) {
            this.historyLoading = true;
            getHistory(target, 1, this.pageSize)
                .then((response) => {
                    const total = response.data.total || 0;
                    const lastPage = Math.max(1, Math.ceil(total / this.pageSize));
                    return getHistory(target, lastPage, this.pageSize).then((res) => {
                        this.historyPage = lastPage;
                        this.hasMore = lastPage > 1;
                        this.messages = (res.data.records || []).map(this.mapHistoryItem);
                        this.scrollToBottom();
                    });
                })
                .then(() => {
                    // 历史加载完成，服务端已将该会话标记为已读，通知 layout 刷新未读角标
                    emitLocalEvent({ type: 'messagesRead' });
                })
                .catch(() => {
                    this.messages = [];
                    this.hasMore = false;
                })
                .then(() => {
                    this.historyLoading = false;
                });
        },
        // 加载更早的消息并保持滚动位置稳定
        loadMore() {
            if (this.historyPage <= 1 || this.historyLoading) {
                return;
            }
            const container = this.$refs.msgList;
            const prevHeight = container ? container.scrollHeight : 0;
            const prevTop = container ? container.scrollTop : 0;
            this.historyLoading = true;
            getHistory(this.target, this.historyPage - 1, this.pageSize)
                .then((response) => {
                    this.historyPage = this.historyPage - 1;
                    this.hasMore = this.historyPage > 1;
                    const older = (response.data.records || []).map(this.mapHistoryItem);
                    this.messages = older.concat(this.messages);
                    this.$nextTick(() => {
                        if (container) {
                            container.scrollTop = container.scrollHeight - prevHeight + prevTop;
                        }
                    });
                })
                .catch(() => { })
                .then(() => {
                    this.historyLoading = false;
                });
        },
        mapHistoryItem(item) {
            return {
                key: 'h' + item.id,
                kind: item.fromName === this.currentUsername ? 'self' : 'other',
                content: item.content,
                time: item.createTime
            };
        },

        send() {
            if (!this.target) {
                this.$message.error('请先选择聊天对象');
                return;
            }
            const text = (this.content || '').trim();
            if (!text) {
                this.$message.error('消息内容不能为空');
                return;
            }
            if (!socketReady()) {
                this.$message.error('连接已断开，正在重连，请稍后再试');
                return;
            }
            // 服务端只把消息推给接收方，本地需自行回显
            if (sendChatMessage(this.target, text)) {
                this.messages.push({
                    key: 'l' + Date.now(),
                    kind: 'self',
                    content: text,
                    time: this.formatNow()
                });
                this.content = '';
                this.scrollToBottom();
            } else {
                this.$message.error('发送失败，请检查网络连接');
            }
        },

        handleSocketEvent(payload) {
            if (payload.type === 'open') {
                this.connected = true;
                return;
            }
            if (payload.type === 'close') {
                this.connected = false;
                return;
            }
            if (payload.type === 'onlineUsers') {
                this.onlineUsers = payload.users || [];
                return;
            }
            if (payload.type === 'system') {
                if (this.target) {
                    this.pushSystemMessage(payload.text);
                }
                return;
            }
            if (payload.type === 'message') {
                // 仅展示当前会话对象发来的消息，其他人发来的由 layout 负责未读角标
                if (this.target && payload.fromName === this.target) {
                    this.messages.push({
                        key: 'r' + Date.now(),
                        kind: 'other',
                        content: payload.content,
                        time: this.formatNow()
                    });
                    this.scrollToBottom();
                    // 后端无独立已读接口，拉取一条历史即会将该会话标记为已读
                    getHistory(this.target, 1, 1)
                        .then(() => {
                            emitLocalEvent({ type: 'messagesRead' });
                        })
                        .catch(() => { });
                }
            }
        },
        pushSystemMessage(text) {
            this.messages.push({
                key: 's' + Date.now(),
                kind: 'system',
                content: text
            });
            this.scrollToBottom();
        },

        scrollToBottom() {
            this.$nextTick(() => {
                const container = this.$refs.msgList;
                if (container) {
                    container.scrollTop = container.scrollHeight;
                }
            });
        },
        pad(n) {
            return n < 10 ? '0' + n : '' + n;
        },
        formatNow() {
            const d = new Date();
            return d.getFullYear() + '-' + this.pad(d.getMonth() + 1) + '-' + this.pad(d.getDate())
                + ' ' + this.pad(d.getHours()) + ':' + this.pad(d.getMinutes()) + ':' + this.pad(d.getSeconds());
        }
    }
}
</script>

<style scoped>
.chat_container {
    height: calc(100vh - 92px);
}

.panel {
    background-color: #ffffff;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.panel_title {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #ebeef5;
    font-size: 14px;
    color: #303133;
}

.status_dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-left: 12px;
}

.status_dot.online {
    background-color: #67C23A;
}

.status_dot.offline {
    background-color: #F56C6C;
}

.status_text {
    margin-left: 6px;
    font-size: 12px;
    color: #909399;
}

.online_panel {
    padding-bottom: 12px;
}

.online_list {
    flex: 1;
    overflow-y: auto;
    padding: 12px 16px 0;
}

.online_tag {
    margin: 0 8px 8px 0;
    cursor: pointer;
}

.online_panel .empty_tip {
    padding: 20px 0;
    text-align: center;
    color: #c0c4cc;
    font-size: 13px;
}

.manual_box {
    padding: 12px 16px 0;
    border-top: 1px solid #f2f6fc;
}

.manual_label {
    font-size: 12px;
    color: #909399;
    margin-bottom: 6px;
}

.manual_btn {
    margin-top: 8px;
    width: 100%;
}

.msg_list {
    flex: 1;
    overflow-y: auto;
    padding: 12px 16px;
    background-color: #f7f9fc;
}

.load_more {
    text-align: center;
    margin-bottom: 8px;
}

.msg_item {
    margin-bottom: 12px;
}

.msg_system {
    text-align: center;
}

.system_text {
    display: inline-block;
    padding: 2px 10px;
    background-color: #f0f0f0;
    color: #909399;
    font-size: 12px;
    border-radius: 10px;
}

.msg_other .bubble {
    background-color: #ffffff;
    color: #303133;
    margin-right: 60px;
}

.msg_self {
    text-align: right;
}

.msg_self .bubble {
    background-color: #409EFF;
    color: #ffffff;
    margin-left: 60px;
}

.bubble {
    display: inline-block;
    max-width: 100%;
    padding: 8px 12px;
    border-radius: 6px;
    font-size: 14px;
    line-height: 20px;
    text-align: left;
    word-break: break-all;
    white-space: pre-wrap;
}

.msg_author {
    font-size: 12px;
    color: #909399;
    margin-bottom: 4px;
}

.msg_time {
    font-size: 11px;
    color: #c0c4cc;
    margin-top: 4px;
}

.send_box {
    border-top: 1px solid #ebeef5;
    padding: 10px 16px 12px;
}

.send_footer {
    margin-top: 8px;
    display: flex;
    justify-content: flex-end;
}

.no_target {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>