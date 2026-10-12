<template>
    <div class="announcement_container">
        <h2 class="page_title">公告信息</h2>

        <div v-loading="loading" class="announcement_list">
            <div v-for="item in list" :key="item.id" class="announcement_item" @click="openDetail(item)">
                <div class="item_head">
                    <el-tag v-if="item.isTop === 1" size="mini" type="danger">置顶</el-tag>
                    <span class="item_title">{{ item.title }}</span>
                    <span class="item_time">{{ item.createTime }}</span>
                </div>
                <div class="item_summary">{{ item.content }}</div>
            </div>
            <div v-if="!loading && list.length === 0" class="empty_tip">暂无公告</div>
        </div>

        <el-pagination :page-size="pageSize" :pager-count="7" layout="prev, pager, next" :total="total"
            :current-page="currentPage" @current-change="handleCurrentChange" class="pagination_sty">
        </el-pagination>

        <!-- 公告详情 -->
        <el-dialog :visible.sync="detailVisible" width="720px" custom-class="announcement_detail_dialog">
            <div slot="title" class="detail_title">
                <el-tag v-if="detail.isTop === 1" size="mini" type="danger">置顶</el-tag>
                {{ detail.title }}
            </div>
            <div class="detail_time">发布时间：{{ detail.createTime }}</div>
            <img v-if="detail.coverImage" :src="detail.coverImage" class="detail_cover" />
            <div class="detail_content">{{ detail.content }}</div>
        </el-dialog>
    </div>
</template>

<script>
import { getAnnouncementList, getAnnouncementById } from '@/api/announcement';

export default {
    name: 'publish_information',
    created() {
        this.fetchList();
    },
    data() {
        return {
            loading: false,
            total: 0,
            currentPage: 1,
            pageSize: 10,
            list: [],

            detailVisible: false,
            detail: {}
        }
    },
    methods: {
        fetchList() {
            this.loading = true;
            getAnnouncementList(this.currentPage, this.pageSize)
                .then((response) => {
                    this.list = response.data.records;
                    this.total = response.data.total;
                })
                .catch(() => {
                    this.list = [];
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

        openDetail(item) {
            this.detail = item;
            this.detailVisible = true;
            getAnnouncementById(item.id)
                .then((response) => {
                    this.detail = response.data;
                })
                .catch(() => { });
        }
    }
}
</script>

<style scoped>
.page_title {
    margin: 0 0 16px;
    font-size: 20px;
    color: #303133;
}

.announcement_list {
    min-height: 200px;
}

.announcement_item {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    padding: 14px 16px;
    margin-bottom: 12px;
    background: #fff;
    cursor: pointer;
    transition: box-shadow .2s;
}

.announcement_item:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
}

.item_head {
    display: flex;
    align-items: center;
}

.item_title {
    margin-left: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
}

.item_time {
    margin-left: auto;
    color: #909399;
    font-size: 12px;
}

.item_summary {
    margin-top: 8px;
    color: #606266;
    font-size: 13px;
    line-height: 20px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
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

.detail_title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
}

.detail_time {
    margin-bottom: 12px;
    color: #909399;
    font-size: 12px;
}

.detail_cover {
    display: block;
    max-width: 100%;
    margin-bottom: 12px;
    border-radius: 4px;
}

.detail_content {
    color: #606266;
    line-height: 24px;
    white-space: pre-wrap;
    word-break: break-all;
}
</style>