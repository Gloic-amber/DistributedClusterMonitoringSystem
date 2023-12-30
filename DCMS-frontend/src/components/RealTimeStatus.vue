<template>
  <div>
  <el-table
    :data="tableData"
    style="width: 100%">
    <el-table-column
      prop="ip"
      label="IP地址与端口号"
      width="180">
    </el-table-column>
    <el-table-column
      prop="name"
      label="客户端名称"
      width="180">
    </el-table-column>
    <el-table-column
      prop="cpuNum"
      label="CPU内核数"
      width="180">
    </el-table-column>
    <el-table-column
      prop="os"
      label="操作系统"
      width="180">
    </el-table-column>
    <el-table-column
      prop="online"
      label="状态"
      width="180"
      filter-placement="bottom-end">
      <template slot-scope="scope">
        <el-tag
          :type="scope.row.online? 'success' : 'danger'"
          disable-transitions>{{scope.row.online? '在线':'离线'}}</el-tag>
      </template>
    </el-table-column>
    <el-table-column align="right">
      <template slot="header" >
        <el-button @click="handlePrint()" icon="el-icon-printer" type="info" circle></el-button>
      </template>
      <template slot-scope="scope">
        <el-button @click="handleClick(scope.row)" icon="el-icon-zoom-in" type="warning" circle></el-button>
        <el-button @click="handleEdit(scope.row)" icon="el-icon-edit" type="primary" circle></el-button>
        <el-button @click="openmodifyDialogVisible(scope.row)" icon="el-icon-edit-outline" type="info" circle></el-button> <!-- 新增的修改信息按钮 -->
        <el-button @click="handleDelete(scope.row)" icon="el-icon-delete" type="danger" circle></el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 修改信息的弹窗 -->
  <el-dialog title="修改信息" :visible.sync="modifyDialogVisible" width="30%">
    <el-form :model="modifyForm">
      <el-form-item label="IP地址">
        <el-input v-model="modifyForm.ip"></el-input>
      </el-form-item>
      <el-form-item label="CPU内核数">
        <el-input v-model="modifyForm.cpuNum"></el-input>
      </el-form-item>
      <el-form-item label="操作系统">
        <el-input v-model="modifyForm.os"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="modifyDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="updateClient()">确 定</el-button>
    </span>
  </el-dialog>
  </div>
</template>

<script>
export default {
  name: "RealTimeStatus",
  methods: {
    handleClick(row) {
      if (row.online) {
        this.$router.push("/realTimeGraph");
      }
      else {
        this.$router.push("/historyGraph");
      }
    },
    handleEdit(row) {
      this.$http.post("http://localhost:8011/editClient", row);
      location.reload();
    },
    handlePrint() {
      window.print();
    },
    handleDelete(row) {
      this.$http.post("http://localhost:8011/deleteClient", row);
      location.reload();
    },
    openmodifyDialogVisible(row) { // 打开修改信息弹窗
      this.modifyForm = { ...row }; // 将当前行的数据复制到修改信息的表单中
      this.modifyDialogVisible = true; // 显示修改信息弹窗
    },
    updateClient() { // 修改信息的方法
      this.$http.post("http://localhost:8011/updateClient", this.modifyForm); // 将修改后的数据发送到后端
      this.modifyDialogVisible = false; // 关闭修改信息弹窗
      location.reload(); // 刷新页面
    },
  },
  data() {
    return {
      tableData: [],
      modifyDialogVisible: false, // 控制修改信息弹窗的显示与隐藏
      modifyForm: { // 存储修改信息的表单数据
        ip: '',
        cpuNum: '',
        os: '',
      },

    }
  },
  created() {
    this.$http.get("http://localhost:8011/clients").then(res=>{
      console.log(res.data);
      this.tableData = res.data;
    })
  }
}
</script>

<style>

</style>
