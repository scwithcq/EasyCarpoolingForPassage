<template>
    <div class="message_container">
        <!-- 发表留言 -->
        <div class="publish_box">
            <el-input type="textarea" :rows="3" v-model="content" maxlength="300" show-word-limit
                :placeholder="replyTarget ? ('回复 @' + replyTarget.username + '：') : '写下你的留言...'"></el-input>
            <div class="publish_footer">
                <el-button v-if="replyTarget" type="text" size="small" @click="cancelReply">取消回复</el-button>
                <el-button type="primary" size="small" :loading="submitting" @click="submit">发表留言</el-button>
            </div>
        </div>

        <!-- 留言列表 -->
        <div v-loading="loading" class="message_list">
            <div v-for="item in messageList" :key="item.messageId" class="message_item">
                <div class="message_head">
                    <span class="meta_author">{{ item.username }}</span>
                    <span class="message_time">{{ item.createTime }}</span>
                </div>
                <div class="message_body">
                    <span v-if="item.replyUsername" class="reply_tag">回复 @{{ item.replyUsername }}：</span>{{ item.content }}
                </div>
                <div class="message_ops">
                    <el-button type="text" size="mini" @click="startReply(item)">回复</el-button>
                    <el-button v-if="isMine(item.stuNumber)" type="text" size="mini" class="danger_text"
                        @click="handleDelete(item)">删除</el-button>
                </div>
            </div>
            <div v-if="!loading && messageList.length === 0" class="empty_tip">暂无留言，快来发表第一条留言吧~</div>
        </div>

        <el-pagination :page-size="pageSize" :pager-count="7" layout="prev, pager, next" :total="total"
            :current-page="currentPage" @current-change="handleCurrentChange" class="pagination_sty">
        </el-pagination>
    </div>
</template>

<script>
import { getMessageList, addMessage, deleteMessage } from '@/api/message';
import { getUserInfo } from '@/utils/auth';

export default {
    name: 'message_feedback',
    created() {
        this.fetchList();
    },
    data() {
        return {
            loading: false,
            total: 0,
            currentPage: 1,
            pageSize: 10,
            messageList: [],

            content: '',
            submitting: false,
            replyTarget: null
        }
    },
    methods: {
        currentStuNumber() {
            const userInfo = getUserInfo();
            return userInfo ? userInfo.stuNumber : '';
        },
        isMine(stuNumber) {
            return stuNumber === this.currentStuNumber();
        },

        fetchList() {
            this.loading = true;
            getMessageList(this.currentPage, this.pageSize)
                .then((response) => {
                    this.messageList = response.data.records;
                    this.total = response.data.total;
                })
                .catch(() => {
                    this.messageList = [];
                    this.total = 0;
                })
                .then(() => {
                    this.loading = false;
                });
        },
        handleCurrentChange(newPage) {
            this.currentPage = newPage;
            this.fetchList();
        },

        startReply(item) {
            this.replyTarget = item;
        },
        cancelReply() {
            this.replyTarget = null;
        },
        submit() {
            if (!this.content) {
                this.$message.error('留言内容不能为空!');
                return;
            }
            const payload = {
                content: this.content
            };
            if (this.replyTarget) {
                payload.parentId = this.replyTarget.messageId;
                payload.replyUsername = this.replyTarget.username;
            }
            this.submitting = true;
            addMessage(payload)
                .then(() => {
                    this.$message.success('发表成功!');
                    this.content = '';
                    this.replyTarget = null;
                    this.currentPage = 1;
                    this.fetchList();
                })
                .catch(() => { })
                .then(() => {
                    this.submitting = false;
                });
        },

        handleDelete(item) {
            this.$confirm('确定删除该留言吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                deleteMessage(item.messageId).then(() => {
                    this.$message.success('删除成功!');
                    // 删除当前页最后一条时回退一页
                    if (this.messageList.length === 1 && this.currentPage > 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                    this.fetchList();
                }).catch(() => { });
            }).catch(() => { });
        }
    }
}
</script>

<style scoped>
.publish_box {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    padding: 14px 16px;
    margin-bottom: 16px;
}

.publish_footer {
    margin-top: 10px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
}

.publish_footer .el-button+.el-button {
    margin-left: 12px;
}

.message_list {
    min-height: 200px;
}

.message_item {
    border-bottom: 1px solid #f2f6fc;
    padding: 12px 4px;
}

.message_head {
    display: flex;
    align-items: center;
    font-size: 13px;
}

.meta_author {
    color: #409EFF;
    font-weight: 600;
}

.message_time {
    margin-left: 12px;
    color: #c0c4cc;
    font-size: 12px;
}

.message_body {
    margin: 8px 0;
    color: #606266;
    font-size: 14px;
    line-height: 22px;
    word-break: break-all;
}

.reply_tag {
    color: #409EFF;
}

.message_ops {
    display: flex;
    align-items: center;
}

.empty_tip {
    padding: 40px 0;
    text-align: center;
    color: #c0c4cc;
    font-size: 13px;
}

.pagination_sty {
    margin-top: 16px;
    text-align: right;
}

.danger_text {
    color: #F56C6C;
}
</style>
