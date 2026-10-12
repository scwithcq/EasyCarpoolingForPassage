<template>
    <div class="form">
        <el-form :label-position="labelPosition" label-width="80px" :model="formLabelAlign">
            <el-form-item label="学号">
                <el-input v-model="formLabelAlign.stuNumber" placeholder="请输入学号"></el-input>
            </el-form-item>
            <el-form-item label="用户名">
                <el-input v-model="formLabelAlign.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item label="密码" >
                <el-input v-model="formLabelAlign.password" placeholder="请输入密码" show-password></el-input>
            </el-form-item>
            <el-form-item label="手机号">
                <el-input v-model="formLabelAlign.phone" placeholder="请输入手机号"></el-input>
            </el-form-item>
            <el-form-item label="性别">
                <el-radio-group v-model="formLabelAlign.sex">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="0">女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="上传文件">
              <el-upload
                class="avatar-uploader"
                action="http://localhost:8080/user/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
                <img v-if="formLabelAlign.userImage" :src="formLabelAlign.userImage" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
            <el-form-item class="from-button">
                <el-button type="primary" @click="submitForm">提交</el-button>
                <el-button @click="resetForm">重置</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import { registerCheck,registerMessage } from '@/api/login'
export default {
  name: 'register',
    data() {
      return {
        labelPosition: 'left',
        formLabelAlign: {
          stuNumber: '',
          username: '',
          password: '',
          phone: '',
          sex: 1,
          userImage: ''
        },
      };
    },
    methods: {
        submitForm () {
          // 验证学号
            if (!this.formLabelAlign.stuNumber || !/^\d{12}$/.test(this.formLabelAlign.stuNumber)) {
                this.$message.error('学号错误!');
                return;
            }
          
            // 验证用户名
            if (!this.formLabelAlign.username || this.formLabelAlign.username.length > 6) {
                this.$message.error('用户名错误!');
                return;
            }
          
            // 验证密码
            if (!this.formLabelAlign.password) {
                this.$message.error('密码不能为空!');
                return;
            }
          
            // 验证手机号
            if (!this.formLabelAlign.phone || !/^1[3-9]\d{9}$/.test(this.formLabelAlign.phone)) {
                this.$message.error('手机号格式不正确!');
                return;
            }
          
            // 验证性别
            // if (!this.formLabelAlign.sex) {
            //     this.$message.error('请选择性别!');
            //     return;
            // }
          
            // 验证文件是否上传
            if (!this.formLabelAlign.userImage) {
                this.$message.error('请上传头像!');
                return;
            }
            registerCheck(this.formLabelAlign.stuNumber,this.formLabelAlign.username,this.formLabelAlign.phone)
                .then(() => {
                    registerMessage(this.formLabelAlign)
                      .then(response => {
                        this.$message.success("注册成功!")
                        this.resetForm()
                        this.$router.push('/login')
                      })
                      .catch(error => {
                        this.resetForm()
                      })
                })
                .catch(() => {
                  // 校验不通过的原因已由响应拦截器统一提示
                })
        },
        resetForm () {
            this.formLabelAlign = {
              stuNumber: '',
              username: '',
              password: '',
              phone: '',
              sex: 1,
              userImage: ''
            };
        },
        handleAvatarSuccess(res, file) {
          // console.log(file)
          // console.log(res)
          // this.formLabelAlign.userImage = URL.createObjectURL(file.raw);
          // console.log(this.formLabelAlign.userImage)
          this.formLabelAlign.userImage = res.data
          console.log(this.formLabelAlign.userImage)
        },
        beforeAvatarUpload(file) {
          const isJPG = file.type === 'image/jpeg';
          const isLt2M = file.size / 1024 / 1024 < 2;
        
          if (!isJPG) {
            this.$message.error('上传头像图片只能是 JPG 格式!');
          }
          if (!isLt2M) {
            this.$message.error('上传头像图片大小不能超过 2MB!');
          }
          return isJPG && isLt2M;
        }
    }
}
</script>

<style scoped>
    .form {
        width: 500px;
        height: 600px;
        margin: 100px auto;
    }
    .form-button{
        display: flex;
        justify-content: space-between;
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
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>