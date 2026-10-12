<template>

    <div>
        <el-tabs v-model="activeTab" @tab-click="handleTabChange">
            <el-tab-pane label="全部拼车" name="all"></el-tab-pane>
            <el-tab-pane label="我发布的" name="mine"></el-tab-pane>
            <el-tab-pane label="我接单的" name="joined"></el-tab-pane>
        </el-tabs>

        <el-table :data="tableData" border style="width: 100%" v-loading="loading">
            <el-table-column label="序号" width="60">
                <template slot-scope="scope">
                    {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
                </template>
            </el-table-column>
            <el-table-column prop="username" label="发布者" width="110">
            </el-table-column>
            <el-table-column prop="stuNumber" label="学号" width="120">
            </el-table-column>
            <el-table-column prop="departure" label="出发地" width="110">
            </el-table-column>
            <el-table-column prop="destination" label="目的地" width="110">
            </el-table-column>
            <el-table-column prop="departureTime" label="出发时间" width="150">
            </el-table-column>
            <el-table-column label="人数" width="90">
                <template slot-scope="scope">
                    {{ (scope.row.joinedNumber || 0) + ' / ' + (scope.row.numberOfPeople || 0) }}
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="150">
            </el-table-column>
            <el-table-column prop="remark" label="备注" width="120">
            </el-table-column>
            <el-table-column prop="phone" label="联系号码" width="130">
            </el-table-column>

            <el-table-column fixed="right" label="操作" width="230">
                <template slot-scope="scope">
                    <el-button @click="openDetail(scope.row)" type="text" size="small">查看</el-button>

                    <template v-if="isOwner(scope.row)">
                        <el-button @click="openEdit(scope.row)" type="text" size="small">编辑</el-button>
                        <el-button @click="handleDelete(scope.row)" type="text" size="small"
                            class="danger_text">删除</el-button>
                    </template>

                    <template v-else>
                        <el-button v-if="isJoined(scope.row)" @click="handleCancelJoin(scope.row)" type="text"
                            size="small">取消接单</el-button>
                        <el-button v-else @click="handleJoin(scope.row)" type="text" size="small"
                            :disabled="isFull(scope.row)">{{ isFull(scope.row) ? '人数已满' : '接单' }}</el-button>
                    </template>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination :page-size="pageSize" :pager-count="7" layout="prev, pager, next" :total="total"
            :current-page="currentPage" @current-change="handleCurrentChange" class="pagination_sty">
        </el-pagination>

        <!-- 拼车详情 -->
        <el-dialog title="拼车详情" :visible.sync="detailVisible" width="680px">
            <el-descriptions :column="2" border>
                <el-descriptions-item label="发布者">{{ detail.username }}</el-descriptions-item>
                <el-descriptions-item label="学号">{{ detail.stuNumber }}</el-descriptions-item>
                <el-descriptions-item label="出发地">{{ detail.departure }}</el-descriptions-item>
                <el-descriptions-item label="目的地">{{ detail.destination }}</el-descriptions-item>
                <el-descriptions-item label="出发时间">{{ detail.departureTime }}</el-descriptions-item>
                <el-descriptions-item label="联系电话">{{ detail.phone }}</el-descriptions-item>
                <el-descriptions-item label="人数">
                    {{ (detail.joinedNumber || 0) + ' / ' + (detail.numberOfPeople || 0) }}
                </el-descriptions-item>
                <el-descriptions-item label="发布时间">{{ detail.createTime }}</el-descriptions-item>
                <el-descriptions-item label="备注" :span="2">{{ detail.remark }}</el-descriptions-item>
            </el-descriptions>

            <h4 class="dialog_sub_title">接单成员</h4>
            <el-table :data="joinList" border size="mini">
                <el-table-column label="序号" type="index" width="60"></el-table-column>
                <el-table-column prop="username" label="用户名"></el-table-column>
                <el-table-column prop="stuNumber" label="学号"></el-table-column>
                <el-table-column prop="phone" label="联系电话"></el-table-column>
                <el-table-column prop="createTime" label="接单时间"></el-table-column>
            </el-table>
        </el-dialog>

        <!-- 编辑拼车 -->
        <el-dialog title="编辑拼车信息" :visible.sync="editVisible" width="520px">
            <el-form :model="editForm" label-width="80px">
                <el-form-item label="出发地">
                    <el-input v-model="editForm.departure"></el-input>
                </el-form-item>
                <el-form-item label="目的地">
                    <el-input v-model="editForm.destination"></el-input>
                </el-form-item>
                <el-form-item label="出发时间">
                    <el-date-picker type="datetime" placeholder="选择出发时间" value-format="yyyy-MM-dd HH:mm:ss"
                        v-model="editForm.departureTime" style="width: 100%;"></el-date-picker>
                </el-form-item>
                <el-form-item label="人数">
                    <el-input-number v-model="editForm.numberOfPeople" :min="1" :max="20"></el-input-number>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="editForm.remark"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" :loading="saving" @click="submitEdit">保 存</el-button>
            </div>
        </el-dialog>

    </div>

</template>

<script>
import {
    getCarpool, getCarpoolById, getMyCarpool, getMyJoin,
    updateCarpool, deleteCarpool, joinCarpool, cancelJoin, getJoinList
} from '@/api/order';
import { getUserInfo } from '@/utils/auth';

export default {
    created() {
        this.fetchList();
        this.fetchJoinedOrderIds();
    },
    data() {
        return {
            activeTab: 'all', // all 全部拼车 / mine 我发布的 / joined 我接单的
            loading: false,
            total: 0, // 数据总数
            currentPage: 1, // 当前页码
            pageSize: 10, // 每页数据条数
            tableData: [],
            joinedOrderIds: [], // 我已接单的拼车 orderId 集合

            detailVisible: false,
            detail: {},
            joinList: [],

            editVisible: false,
            saving: false,
            editForm: {
                id: null,
                departure: '',
                destination: '',
                departureTime: '',
                numberOfPeople: 1,
                remark: ''
            }
        }
    },
    methods: {
        // 当前登录用户学号，用于判断拼车是否本人发布
        currentStuNumber() {
            const userInfo = getUserInfo();
            return userInfo ? userInfo.stuNumber : '';
        },
        isOwner(row) {
            return row.stuNumber === this.currentStuNumber();
        },
        isFull(row) {
            return (row.joinedNumber || 0) >= (row.numberOfPeople || 0);
        },
        isJoined(row) {
            return this.joinedOrderIds.indexOf(row.orderId) !== -1;
        },

        // 按当前 tab 拉取对应列表
        fetchList() {
            this.loading = true;
            let request;
            if (this.activeTab === 'mine') {
                request = getMyCarpool(this.currentPage, this.pageSize);
            } else if (this.activeTab === 'joined') {
                request = getMyJoin(this.currentPage, this.pageSize);
            } else {
                request = getCarpool(this.currentPage, this.pageSize);
            }
            request
                .then((response) => {
                    this.tableData = response.data.records;
                    this.total = response.data.total;
                })
                .catch(() => {
                    this.tableData = [];
                    this.total = 0;
                })
                .then(() => {
                    this.loading = false;
                });
        },
        // 记录我已接单的拼车，用于切换「接单/取消接单」按钮
        fetchJoinedOrderIds() {
            getMyJoin(1, 1000)
                .then((response) => {
                    this.joinedOrderIds = response.data.records.map(item => item.orderId);
                })
                .catch(() => {
                    this.joinedOrderIds = [];
                });
        },

        handleTabChange() {
            this.currentPage = 1;
            this.fetchList();
        },
        handleCurrentChange(newPage) {
            this.currentPage = newPage;
            this.fetchList();
        },
        // 列表变更后刷新列表与我接单状态
        refreshAll() {
            this.fetchList();
            this.fetchJoinedOrderIds();
        },

        openDetail(row) {
            this.detail = row;
            this.joinList = [];
            this.detailVisible = true;
            getCarpoolById(row.id).then((response) => {
                this.detail = response.data;
            }).catch(() => { });
            getJoinList(row.id).then((response) => {
                this.joinList = response.data;
            }).catch(() => { });
        },

        openEdit(row) {
            this.editForm = {
                id: row.id,
                departure: row.departure,
                destination: row.destination,
                departureTime: this.normalizeTime(row.departureTime),
                numberOfPeople: row.numberOfPeople,
                remark: row.remark
            };
            this.editVisible = true;
        },
        // 后端返回的出发时间不带秒（yyyy-MM-dd HH:mm），提交时后端要求带秒，这里统一补齐
        normalizeTime(time) {
            if (!time) {
                return '';
            }
            return time.length === 16 ? time + ':00' : time;
        },
        submitEdit() {
            if (!this.editForm.departure || !this.editForm.destination || !this.editForm.departureTime) {
                this.$message.error('出发地、目的地、出发时间不能为空!');
                return;
            }
            this.saving = true;
            updateCarpool(this.editForm)
                .then(() => {
                    this.$message.success('修改成功!');
                    this.editVisible = false;
                    this.refreshAll();
                })
                .catch(() => { })
                .then(() => {
                    this.saving = false;
                });
        },

        handleDelete(row) {
            this.$confirm('确定删除该拼车信息吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                deleteCarpool(row.id).then(() => {
                    this.$message.success('删除成功!');
                    // 删除当前页最后一条时回退一页
                    if (this.tableData.length === 1 && this.currentPage > 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                    this.refreshAll();
                }).catch(() => { });
            }).catch(() => { });
        },

        handleJoin(row) {
            joinCarpool(row.id).then(() => {
                this.$message.success('接单成功!');
                this.refreshAll();
            }).catch(() => { });
        },
        handleCancelJoin(row) {
            this.$confirm('确定取消接单吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                cancelJoin(row.id).then(() => {
                    this.$message.success('已取消接单!');
                    this.refreshAll();
                }).catch(() => { });
            }).catch(() => { });
        }
    }
}
</script>

<style>
.pagination-container {
    margin-top: 20px;
    text-align: right;
}

.pagination_sty {
    position: relative;
    margin: 10px 0 0 600px;
}

.danger_text {
    color: #F56C6C;
}

.dialog_sub_title {
    margin: 16px 0 8px;
}
</style>
