<template>
    <div class="login-page">
        <div class="bg"></div>
        <div class="form">
            <h1 style="text-align: center;">登录界面</h1>
            <div>
                <div class="form-information">
                    <el-input class="username" v-model="username" placeholder="请输入用户名" autofocus></el-input>
                    <el-input class="password" placeholder="请输入密码" v-model="password" show-password></el-input>
                </div>
                <el-row class="form-button">
                    <el-button class="login" type="primary" round @click="LoginSend">登录</el-button>
                    <el-button class="register" type="primary" round @click="RegisterSend">注册</el-button>
                </el-row>
                <div class="remember-me">
                    <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                    <a class="forgot-password" @click.prevent="openForget">忘记密码?</a>
                </div>
            </div>
        </div>

        <!-- 忘记密码：学号 + 手机号校验身份后重置 -->
        <el-dialog title="重置密码" :visible.sync="forgetVisible" width="420px">
            <el-form :model="forgetForm" label-width="80px">
                <el-form-item label="学号">
                    <el-input v-model="forgetForm.stuNumber" maxlength="12" placeholder="请输入12位学号"></el-input>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="forgetForm.phone" maxlength="11" placeholder="请输入注册时的手机号"></el-input>
                </el-form-item>
                <el-form-item label="新密码">
                    <el-input v-model="forgetForm.newPassword" show-password placeholder="请输入新密码"></el-input>
                </el-form-item>
                <el-form-item label="确认密码">
                    <el-input v-model="forgetForm.confirmPassword" show-password placeholder="请再次输入新密码"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="forgetVisible = false">取 消</el-button>
                <el-button type="primary" :loading="resetting" @click="submitForget">重 置</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
import { loginMessage, forgetPassword } from '@/api/login'
import { setToken, setUserInfo } from '@/utils/auth'
export default {
    name: 'login',
    data() {
        return {
            username: localStorage.getItem('username') || '',
            password: localStorage.getItem('password') || '',
            rememberMe: localStorage.getItem('username') ? true : false,

            forgetVisible: false,
            resetting: false,
            forgetForm: {
                stuNumber: '',
                phone: '',
                newPassword: '',
                confirmPassword: ''
            }
        }
    },
    methods: {
        async LoginSend() {
            if (this.username === '' || this.password === '') {
                this.$message.error("用户名或密码不能为空!");
                return;
            }
            try {
                const response = await loginMessage(this.username, this.password);
                // 保存登录态：后续接口靠该令牌通过后端鉴权
                setToken(response.data.token);
                setUserInfo({
                    id: response.data.id,
                    stuNumber: response.data.stuNumber,
                    username: response.data.username,
                    userImage: response.data.userImage
                });
                if (this.rememberMe) {
                    localStorage.setItem('username', this.username);
                    localStorage.setItem('password', this.password); // 存储密码
                } else {
                    localStorage.removeItem('username');
                    localStorage.removeItem('password');
                }
                this.$message.success('登录成功!');
                this.$router.push('/layout/home');
            } catch (error) {
                // 失败原因已由响应拦截器统一提示
            }
        },
        RegisterSend() {
            this.$router.push('/register');
        },
        openForget() {
            this.forgetForm = {
                stuNumber: '',
                phone: '',
                newPassword: '',
                confirmPassword: ''
            };
            this.forgetVisible = true;
        },
        submitForget() {
            const form = this.forgetForm;
            if (!form.stuNumber || !form.phone || !form.newPassword) {
                this.$message.error('学号、手机号和新密码不能为空!');
                return;
            }
            if (!/^\d{12}$/.test(form.stuNumber)) {
                this.$message.error('请输入12位学号!');
                return;
            }
            if (!/^\d{11}$/.test(form.phone)) {
                this.$message.error('请输入11位手机号!');
                return;
            }
            if (form.newPassword !== form.confirmPassword) {
                this.$message.error('两次输入的新密码不一致!');
                return;
            }
            this.resetting = true;
            forgetPassword({
                stuNumber: form.stuNumber,
                phone: form.phone,
                newPassword: form.newPassword
            })
                .then(() => {
                    this.$message.success('密码重置成功，请使用新密码登录!');
                    this.forgetVisible = false;
                })
                .catch(() => { })
                .then(() => {
                    this.resetting = false;
                });
        }
    }
}
</script>

<style scoped>
.login-page {
    height: 100vh;
    display: flex;
    /* justify-content: space-between; */

    align-items: center;
}

.bg {
    background: url('../images/login/logo.png') no-repeat center top / 400px auto,
        url('../images/login/bgimg.jpg') no-repeat center / cover;
    border-radius: 0 20px 20px 0;
    width: 50%;
    height: 100%;
}

.form {
    width: 300px;
    padding: 20px 8%;
    margin-left: 5%;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    background: #fff;
}

.form-information .username,
.form-information .password {
    margin-bottom: 20px;
}

.form-button {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
}

.remember-me {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
    font-size: 14px;
}



.forgot-password {
    text-align: right;
    font-size: 14px;
    color: #409EFF;
    cursor: pointer;
}
</style>