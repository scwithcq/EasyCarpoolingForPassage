<template>
    <div class="personal_container" v-loading="loading">
        <h2 class="page_title">个人信息</h2>

        <el-card shadow="never" class="info_card">
            <div class="info_head">
                <img v-if="info.userImage" :src="info.userImage" class="avatar" />
                <div v-else class="avatar avatar_placeholder"><i class="el-icon-user-solid"></i></div>
                <div class="head_right">
                    <div class="nickname">{{ info.username }}</div>
                    <div class="stu_number">学号：{{ info.stuNumber }}</div>
                </div>
            </div>

            <el-descriptions :column="2" border class="info_desc">
                <el-descriptions-item label="用户名">{{ info.username }}</el-descriptions-item>
                <el-descriptions-item label="学号">{{ info.stuNumber }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ info.phone }}</el-descriptions-item>
                <el-descriptions-item label="性别">{{ info.sex === 1 ? '男' : '女' }}</el-descriptions-item>
            </el-descriptions>

            <div class="info_footer">
                <el-button type="primary" size="small" icon="el-icon-edit" @click="openEdit">编辑资料</el-button>
                <el-button size="small" icon="el-icon-key" @click="openPassword">修改密码</el-button>
            </div>
        </el-card>

        <!-- 编辑资料 -->
        <el-dialog title="编辑资料" :visible.sync="editVisible" width="520px">
            <el-form :model="editForm" label-width="70px">
                <el-form-item label="头像">
                    <el-upload class="avatar-uploader" action="" :show-file-list="false"
                        :http-request="doUpload">
                        <img v-if="editForm.userImage" :src="editForm.userImage" class="avatar" />
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                    <div class="upload_tip">支持 jpg/png/gif/webp/bmp，大小不超过 5MB</div>
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input v-model="editForm.username" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="editForm.phone" maxlength="11" placeholder="请输入手机号"></el-input>
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="editForm.sex">
                        <el-radio :label="1">男</el-radio>
                        <el-radio :label="0">女</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" :loading="saving" @click="submitEdit">保 存</el-button>
            </div>
        </el-dialog>

        <!-- 修改密码 -->
        <el-dialog title="修改密码" :visible.sync="passwordVisible" width="460px">
            <el-form :model="passwordForm" label-width="80px">
                <el-form-item label="原密码">
                    <el-input v-model="passwordForm.oldPassword" show-password placeholder="请输入原密码"></el-input>
                </el-form-item>
                <el-form-item label="新密码">
                    <el-input v-model="passwordForm.newPassword" show-password placeholder="请输入新密码"></el-input>
                </el-form-item>
                <el-form-item label="确认密码">
                    <el-input v-model="passwordForm.confirmPassword" show-password placeholder="请再次输入新密码"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="passwordVisible = false">取 消</el-button>
                <el-button type="primary" :loading="changing" @click="submitPassword">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getPersonalMessage, updatePersonalMessage, updatePassword, uploadAvatar } from '@/api/employee';
import { getUserInfo, setUserInfo } from '@/utils/auth';

export default {
    name: 'personal_Information',
    created() {
        this.fetchInfo();
    },
    data() {
        return {
            loading: false,
            info: {},

            editVisible: false,
            saving: false,
            editForm: {
                username: '',
                userImage: '',
                phone: '',
                sex: 1
            },

            passwordVisible: false,
            changing: false,
            passwordForm: {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            }
        }
    },
    methods: {
        fetchInfo() {
            this.loading = true;
            getPersonalMessage()
                .then((response) => {
                    this.info = response.data || {};
                })
                .catch(() => {
                    this.info = {};
                })
                .then(() => {
                    this.loading = false;
                });
        },

        openEdit() {
            // 只回填允许修改的字段，避免把密码等字段回传给后端
            this.editForm = {
                username: this.info.username,
                userImage: this.info.userImage,
                phone: this.info.phone,
                sex: this.info.sex
            };
            this.editVisible = true;
        },
        // 头像上传：成功后仅回填图片地址，保存资料时才真正提交
        doUpload(options) {
            const file = options.file;
            const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/bmp'];
            if (allowedTypes.indexOf(file.type) === -1) {
                this.$message.error('仅支持 jpg/png/gif/webp/bmp 格式的图片!');
                return;
            }
            if (file.size / 1024 / 1024 >= 5) {
                this.$message.error('头像大小不能超过 5MB!');
                return;
            }
            const formData = new FormData();
            formData.append('file', file);
            uploadAvatar(formData)
                .then((response) => {
                    this.editForm.userImage = response.data;
                    this.$message.success('头像上传成功!');
                })
                .catch(() => { });
        },
        submitEdit() {
            if (!this.editForm.username || !this.editForm.phone) {
                this.$message.error('用户名和手机号不能为空!');
                return;
            }
            if (!/^\d{11}$/.test(this.editForm.phone)) {
                this.$message.error('请输入11位手机号!');
                return;
            }
            this.saving = true;
            updatePersonalMessage(this.editForm)
                .then(() => {
                    this.$message.success('修改成功!');
                    this.editVisible = false;
                    this.syncLocalUserInfo();
                    this.fetchInfo();
                })
                .catch(() => { })
                .then(() => {
                    this.saving = false;
                });
        },
        // 同步本地登录态并刷新页面，让顶部导航显示最新的用户名与头像
        syncLocalUserInfo() {
            const userInfo = getUserInfo() || {};
            setUserInfo({
                id: userInfo.id,
                stuNumber: this.info.stuNumber,
                username: this.editForm.username,
                userImage: this.editForm.userImage
            });
            this.$router.go(0);
        },

        openPassword() {
            this.passwordForm = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            };
            this.passwordVisible = true;
        },
        submitPassword() {
            const form = this.passwordForm;
            if (!form.oldPassword || !form.newPassword) {
                this.$message.error('原密码和新密码不能为空!');
                return;
            }
            if (form.newPassword !== form.confirmPassword) {
                this.$message.error('两次输入的新密码不一致!');
                return;
            }
            this.changing = true;
            updatePassword({
                oldPassword: form.oldPassword,
                newPassword: form.newPassword
            })
                .then(() => {
                    this.$message.success('密码修改成功，请牢记新密码!');
                    this.passwordVisible = false;
                })
                .catch(() => { })
                .then(() => {
                    this.changing = false;
                });
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

.info_card {
    max-width: 720px;
}

.info_head {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.avatar {
    width: 72px;
    height: 72px;
    border-radius: 50%;
    object-fit: cover;
}

.avatar_placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 34px;
    color: #c0c4cc;
    background: #f5f7fa;
}

.head_right {
    margin-left: 16px;
}

.nickname {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
}

.stu_number {
    margin-top: 6px;
    color: #909399;
    font-size: 13px;
}

.info_footer {
    margin-top: 20px;
}

.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.avatar-uploader .el-upload:hover {
    border-color: #409EFF;
}

.avatar-uploader-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 72px;
    height: 72px;
    font-size: 24px;
    color: #8c939d;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
}

.upload_tip {
    margin-top: 6px;
    color: #909399;
    font-size: 12px;
    line-height: 18px;
}
</style>