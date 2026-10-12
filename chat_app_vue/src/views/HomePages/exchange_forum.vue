<template>
    <div class="forum_container">
        <el-tabs v-model="activeTab" @tab-click="handleTabChange">
            <el-tab-pane label="全部帖子" name="all"></el-tab-pane>
            <el-tab-pane label="我发布的" name="mine"></el-tab-pane>
        </el-tabs>

        <!-- 工具栏：分类筛选 + 发帖 -->
        <div class="forum_toolbar">
            <el-select v-if="activeTab === 'all'" v-model="categoryId" placeholder="全部分类" clearable size="small"
                class="category_select" @change="handleCategoryChange">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
            </el-select>
            <span v-else class="toolbar_tip">这里是你发布的全部帖子</span>
            <el-button type="primary" size="small" icon="el-icon-edit" @click="openAdd">发布帖子</el-button>
        </div>

        <!-- 帖子列表 -->
        <div v-loading="loading" class="post_list">
            <div v-for="post in postList" :key="post.postId" class="post_item" @click="openDetail(post)">
                <div class="post_head">
                    <span class="post_title">{{ post.title }}</span>
                    <el-tag v-if="post.categoryName" size="mini" type="success">{{ post.categoryName }}</el-tag>
                </div>
                <div class="post_content">{{ post.content }}</div>
                <div class="post_meta">
                    <span class="meta_author">{{ post.username }}</span>
                    <span>{{ post.createTime }}</span>
                    <span><i class="el-icon-view"></i> {{ post.viewCount || 0 }}</span>
                    <span><i class="el-icon-star-off"></i> {{ post.likeCount || 0 }}</span>
                    <span><i class="el-icon-chat-dot-round"></i> {{ post.commentCount || 0 }}</span>
                    <span class="post_ops" @click.stop>
                        <el-button v-if="isMine(post.stuNumber)" type="text" size="mini" class="danger_text"
                            @click="handleDeletePost(post)">删除</el-button>
                    </span>
                </div>
            </div>
            <div v-if="!loading && postList.length === 0" class="empty_tip">暂无帖子，快来发布第一条吧~</div>
        </div>

        <el-pagination :page-size="pageSize" :pager-count="7" layout="prev, pager, next" :total="total"
            :current-page="currentPage" @current-change="handleCurrentChange" class="pagination_sty">
        </el-pagination>

        <!-- 发布帖子 -->
        <el-dialog title="发布帖子" :visible.sync="addVisible" width="600px">
            <el-form :model="addForm" label-width="60px">
                <el-form-item label="标题">
                    <el-input v-model="addForm.title" maxlength="50" show-word-limit placeholder="请输入标题"></el-input>
                </el-form-item>
                <el-form-item label="分类">
                    <el-select v-model="addForm.categoryId" placeholder="请选择分类" style="width: 100%;">
                        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="内容">
                    <el-input type="textarea" :rows="6" v-model="addForm.content" maxlength="1000" show-word-limit
                        placeholder="请输入内容"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="addVisible = false">取 消</el-button>
                <el-button type="primary" :loading="publishing" @click="submitAdd">发 布</el-button>
            </div>
        </el-dialog>

        <!-- 帖子详情 -->
        <el-dialog :visible.sync="detailVisible" width="720px" custom-class="post_detail_dialog">
            <div slot="title" class="detail_title">{{ detail.title }}</div>
            <div class="detail_meta">
                <span class="meta_author">{{ detail.username }}</span>
                <el-tag v-if="detail.categoryName" size="mini" type="success">{{ detail.categoryName }}</el-tag>
                <span>{{ detail.createTime }}</span>
                <span><i class="el-icon-view"></i> {{ detail.viewCount || 0 }}</span>
                <span><i class="el-icon-chat-dot-round"></i> {{ detail.commentCount || 0 }}</span>
            </div>
            <div class="detail_content">{{ detail.content }}</div>

            <div class="detail_like">
                <el-button size="small" :type="isPostLiked ? 'primary' : ''" @click="togglePostLike">
                    <i class="el-icon-star-off"></i>
                    {{ isPostLiked ? '已点赞' : '点赞' }} ({{ detail.likeCount || 0 }})
                </el-button>
            </div>

            <el-divider content-position="left">评论 ({{ commentList.length }})</el-divider>

            <!-- 评论列表 -->
            <div v-loading="commentLoading" class="comment_list">
                <div v-for="item in commentList" :key="item.commentId" class="comment_item">
                    <div class="comment_head">
                        <span class="meta_author">{{ item.username }}</span>
                        <span class="comment_time">{{ item.createTime }}</span>
                    </div>
                    <div class="comment_body">
                        <span v-if="item.replyUsername" class="reply_tag">回复 @{{ item.replyUsername }}：</span>{{ item.content }}
                    </div>
                    <div class="comment_ops">
                        <el-button type="text" size="mini" @click="toggleCommentLike(item)">
                            <i class="el-icon-star-off"></i>
                            {{ isCommentLiked(item.commentId) ? '已赞' : '点赞' }} ({{ item.likeCount || 0 }})
                        </el-button>
                        <el-button type="text" size="mini" @click="startReply(item)">回复</el-button>
                        <el-button v-if="isMine(item.stuNumber)" type="text" size="mini" class="danger_text"
                            @click="handleDeleteComment(item)">删除</el-button>
                    </div>
                </div>
                <div v-if="!commentLoading && commentList.length === 0" class="empty_tip">还没有评论，快来抢沙发~</div>
            </div>

            <!-- 发表评论 -->
            <div class="comment_editor">
                <div v-if="replyTarget" class="reply_hint">
                    正在回复 @{{ replyTarget.username }}
                    <el-button type="text" size="mini" @click="cancelReply">取消</el-button>
                </div>
                <el-input type="textarea" :rows="3" v-model="commentContent" maxlength="300" show-word-limit
                    :placeholder="replyTarget ? '写下你的回复...' : '写下你的评论...'"></el-input>
                <div class="editor_footer">
                    <el-button type="primary" size="small" :loading="commenting" @click="submitComment">发表</el-button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import {
    getCategoryList, getPostList, getMyPost, getPostById, addPost, deletePost,
    getCommentList, addComment, deleteComment, likeTarget
} from '@/api/forum';
import { getUserInfo } from '@/utils/auth';

export default {
    name: 'exchange_forum',
    created() {
        this.fetchCategories();
        this.fetchList();
    },
    data() {
        return {
            activeTab: 'all', // all 全部帖子 / mine 我发布的
            loading: false,
            total: 0,
            currentPage: 1,
            pageSize: 10,
            postList: [],
            categories: [],
            categoryId: '',

            addVisible: false,
            publishing: false,
            addForm: {
                title: '',
                categoryId: '',
                content: ''
            },

            detailVisible: false,
            detail: {},
            commentLoading: false,
            commentList: [],
            commentContent: '',
            commenting: false,
            replyTarget: null,
            // 后端未提供「我的点赞」查询接口，点赞状态仅在前端会话内记录
            likedKeys: []
        }
    },
    computed: {
        isPostLiked() {
            return this.detail.postId ? this.likedKeys.indexOf('1_' + this.detail.postId) !== -1 : false;
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
        isCommentLiked(commentId) {
            return this.likedKeys.indexOf('2_' + commentId) !== -1;
        },

        fetchCategories() {
            getCategoryList()
                .then((response) => {
                    this.categories = response.data || [];
                })
                .catch(() => {
                    this.categories = [];
                });
        },
        fetchList() {
            this.loading = true;
            const request = this.activeTab === 'mine'
                ? getMyPost(this.currentPage, this.pageSize)
                : getPostList(this.currentPage, this.pageSize, this.categoryId || undefined);
            request
                .then((response) => {
                    this.postList = response.data.records;
                    this.total = response.data.total;
                })
                .catch(() => {
                    this.postList = [];
                    this.total = 0;
                })
                .then(() => {
                    this.loading = false;
                });
        },
        handleTabChange() {
            this.currentPage = 1;
            this.fetchList();
        },
        handleCategoryChange() {
            this.currentPage = 1;
            this.fetchList();
        },
        handleCurrentChange(newPage) {
            this.currentPage = newPage;
            this.fetchList();
        },

        openAdd() {
            this.addForm = { title: '', categoryId: '', content: '' };
            this.addVisible = true;
        },
        submitAdd() {
            if (!this.addForm.title || !this.addForm.content) {
                this.$message.error('标题和内容不能为空!');
                return;
            }
            this.publishing = true;
            addPost(this.addForm)
                .then(() => {
                    this.$message.success('发布成功!');
                    this.addVisible = false;
                    this.currentPage = 1;
                    this.fetchList();
                })
                .catch(() => { })
                .then(() => {
                    this.publishing = false;
                });
        },

        openDetail(row) {
            this.detail = row;
            this.commentList = [];
            this.commentContent = '';
            this.replyTarget = null;
            this.detailVisible = true;
            getPostById(row.postId)
                .then((response) => {
                    this.detail = response.data;
                })
                .catch(() => { });
            this.fetchComments(row.postId);
        },
        fetchComments(postId) {
            this.commentLoading = true;
            getCommentList(postId)
                .then((response) => {
                    this.commentList = response.data || [];
                })
                .catch(() => {
                    this.commentList = [];
                })
                .then(() => {
                    this.commentLoading = false;
                });
        },

        handleDeletePost(row) {
            this.$confirm('确定删除该帖子吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                deletePost(row.postId).then(() => {
                    this.$message.success('删除成功!');
                    // 删除当前页最后一条时回退一页
                    if (this.postList.length === 1 && this.currentPage > 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                    this.fetchList();
                }).catch(() => { });
            }).catch(() => { });
        },

        // 点赞/取消点赞帖子（后端为切换语义，前端按本地记录判断方向）
        togglePostLike() {
            const postId = this.detail.postId;
            if (!postId) {
                return;
            }
            const key = '1_' + postId;
            const liked = this.likedKeys.indexOf(key) !== -1;
            likeTarget(1, postId)
                .then(() => {
                    if (liked) {
                        this.likedKeys.splice(this.likedKeys.indexOf(key), 1);
                        this.detail.likeCount = Math.max(0, (this.detail.likeCount || 0) - 1);
                    } else {
                        this.likedKeys.push(key);
                        this.detail.likeCount = (this.detail.likeCount || 0) + 1;
                    }
                    this.syncListLikeCount(postId, this.detail.likeCount);
                })
                .catch(() => { });
        },
        // 点赞/取消点赞评论
        toggleCommentLike(comment) {
            const key = '2_' + comment.commentId;
            const liked = this.likedKeys.indexOf(key) !== -1;
            likeTarget(2, comment.commentId)
                .then(() => {
                    if (liked) {
                        this.likedKeys.splice(this.likedKeys.indexOf(key), 1);
                        comment.likeCount = Math.max(0, (comment.likeCount || 0) - 1);
                    } else {
                        this.likedKeys.push(key);
                        comment.likeCount = (comment.likeCount || 0) + 1;
                    }
                })
                .catch(() => { });
        },
        // 同步列表里的点赞数，避免关闭详情后数字不一致
        syncListLikeCount(postId, likeCount) {
            const target = this.postList.find(item => item.postId === postId);
            if (target) {
                target.likeCount = likeCount;
            }
        },

        startReply(comment) {
            this.replyTarget = comment;
        },
        cancelReply() {
            this.replyTarget = null;
        },
        submitComment() {
            if (!this.commentContent) {
                this.$message.error('评论内容不能为空!');
                return;
            }
            const payload = {
                postId: this.detail.postId,
                content: this.commentContent
            };
            if (this.replyTarget) {
                payload.parentId = this.replyTarget.commentId;
                payload.replyUsername = this.replyTarget.username;
            }
            this.commenting = true;
            addComment(payload)
                .then(() => {
                    this.$message.success('发表成功!');
                    this.commentContent = '';
                    this.replyTarget = null;
                    this.fetchComments(this.detail.postId);
                    this.detail.commentCount = (this.detail.commentCount || 0) + 1;
                    this.syncListCommentCount(this.detail.postId, this.detail.commentCount);
                })
                .catch(() => { })
                .then(() => {
                    this.commenting = false;
                });
        },
        syncListCommentCount(postId, commentCount) {
            const target = this.postList.find(item => item.postId === postId);
            if (target) {
                target.commentCount = commentCount;
            }
        },
        handleDeleteComment(comment) {
            this.$confirm('确定删除该评论吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                deleteComment(comment.commentId).then(() => {
                    this.$message.success('删除成功!');
                    this.fetchComments(this.detail.postId);
                    this.detail.commentCount = Math.max(0, (this.detail.commentCount || 0) - 1);
                    this.syncListCommentCount(this.detail.postId, this.detail.commentCount);
                }).catch(() => { });
            }).catch(() => { });
        }
    }
}
</script>

<style scoped>
.forum_toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
}

.category_select {
    width: 180px;
}

.toolbar_tip {
    color: #909399;
    font-size: 13px;
}

.post_list {
    min-height: 200px;
}

.post_item {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    padding: 14px 16px;
    margin-bottom: 12px;
    cursor: pointer;
    transition: box-shadow .2s;
}

.post_item:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
}

.post_head {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.post_title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
}

.post_content {
    margin: 8px 0;
    color: #606266;
    font-size: 13px;
    line-height: 20px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
}

.post_meta,
.detail_meta {
    display: flex;
    align-items: center;
    color: #909399;
    font-size: 12px;
}

.post_meta span,
.detail_meta span {
    margin-right: 16px;
}

.meta_author {
    color: #409EFF;
    font-weight: 600;
}

.post_ops {
    margin-left: auto;
}

.detail_title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
}

.detail_content {
    margin: 12px 0;
    color: #606266;
    line-height: 24px;
    white-space: pre-wrap;
    word-break: break-all;
}

.detail_like {
    margin-top: 12px;
}

.comment_list {
    min-height: 80px;
    max-height: 320px;
    overflow-y: auto;
}

.comment_item {
    padding: 10px 0;
    border-bottom: 1px solid #f2f6fc;
}

.comment_head {
    display: flex;
    align-items: center;
    font-size: 13px;
}

.comment_time {
    margin-left: 12px;
    color: #c0c4cc;
    font-size: 12px;
}

.comment_body {
    margin: 6px 0;
    color: #606266;
    font-size: 13px;
    line-height: 20px;
    word-break: break-all;
}

.reply_tag {
    color: #409EFF;
}

.comment_ops {
    display: flex;
    align-items: center;
}

.comment_editor {
    margin-top: 16px;
}

.reply_hint {
    margin-bottom: 6px;
    font-size: 13px;
    color: #909399;
}

.editor_footer {
    margin-top: 8px;
    text-align: right;
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
