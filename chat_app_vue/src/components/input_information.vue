<template>

  <div>
    <span class="position">
      <el-button @click="drawer = true" type="primary" style="font-size: 30px; color: aqua; ">
        +
      </el-button>

    </span>


    <el-drawer title="发布拼车信息" style="text-align: center;" :visible.sync="drawer" size="50%">

      <h1>表单</h1>

      <!-- <div style="margin: 20px;"></div> -->
      <el-form label-width="80px" :model="addCarpoolMessage" ref="addCarpoolMessage">
        <el-form-item label="出发地" prop="departure">
          <el-input v-model="addCarpoolMessage.departure"></el-input>
        </el-form-item>
        <!--         <el-form-item label="出发时间" prop="departureTime">
          <el-input v-model="addCarpoolMessage.departureTime"></el-input>
        </el-form-item> -->
        <el-form-item label="出发时间" prop="departureTime">
          <el-col :span="11">
            <el-form-item prop="departureTime1">
              <el-date-picker type="date" placeholder="选择日期" value-format="yyyy-MM-dd"
                v-model="addCarpoolMessage.departureTime1" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col class="line" :span="2">-</el-col>
          <el-col :span="11">
            <el-form-item prop="departureTime2">
              <el-time-picker placeholder="选择时间" value-format="HH-mm-ss" v-model="addCarpoolMessage.departureTime2"
                style="width: 100%;"></el-time-picker>
            </el-form-item>
          </el-col>
        </el-form-item>


        <el-form-item label="目的地" prop="destination">
          <el-input v-model="addCarpoolMessage.destination"></el-input>
        </el-form-item>
        <el-form-item label="人数" prop="numberOfPeople">
          <el-input v-model="addCarpoolMessage.numberOfPeople"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addCarpoolMessage.phone"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addCarpoolMessage.remark"></el-input>
        </el-form-item>
        <el-form-item label="学号" prop="stuNumber">
          <el-input v-model="addCarpoolMessage.stuNumber"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addCarpoolMessage.username"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm('addCarpool')">立即创建</el-button>
          <el-button @click="resetForm('addCarpoolMessage')">重置</el-button>
        </el-form-item>
      </el-form>




      <!--       
      <el-table :data="gridData">
        <el-table-column property="date" label="日期" width="150"></el-table-column>
        <el-table-column property="name" label="姓名" width="200"></el-table-column>
        <el-table-column property="address" label="地址"></el-table-column>
      </el-table> -->




    </el-drawer>
  </div>


</template>



<script>
import { addCarpool } from '@/api/order';
import Vue from 'vue';
import moment from 'moment';
Vue.prototype.$moment = moment


export default {
  data() {
    return {
      drawer: false,
      innerDrawer: false,
      //添加订单信息
      addCarpoolMessage: {
        departure: '',
        departureTime: '',
        destination: '',
        numberOfPeople: '',
        phone: '',
        remark: '',
        stuNumber: '',
        username: '',
        departureTime1: '',
        departureTime2: ''
      }

    };
  },
  methods: {
    handleClose(done) {
      this.$confirm('还有未保存的工作哦确定关闭吗？')
        .then(_ => {
          done();
        })
        .catch(_ => { });
    },

    resetForm() {
      this.addCarpoolMessage = {
        departure: '',
        departureTime: '',
        destination: '',
        numberOfPeople: '',
        phone: '',
        remark: '',
        stuNumber: '',
        username: '',
        departureTime1: '',
        departureTime2: ''
      }
    },

    submitForm(formName) {


      this.addCarpoolMessage.departureTime2 = this.addCarpoolMessage.departureTime2.replace(/-/g, ":");
      // 合并日期和时间
      const dateTime = this.addCarpoolMessage.departureTime1 + ' ' + this.addCarpoolMessage.departureTime2;


      this.addCarpoolMessage.departureTime = dateTime;

      // 验证手机号
      if (!this.addCarpoolMessage.phone || !/^1[3-9]\d{9}$/.test(this.addCarpoolMessage.phone)) {
        this.$message.error('手机号格式不正确!');
        return;
      }

      addCarpool(this.addCarpoolMessage)
        .then(response => {
          this.$message.success("上传成功!")
          // this.resetForm()
        })
        .catch(error => {
          this.$message.error("上传失败!")
          // this.resetForm()
        })

    },


    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
}
</script>

<style scoped>
.position {
  padding-left: 40px;
}


el-button {
  border-radius: 3px;
}
</style>