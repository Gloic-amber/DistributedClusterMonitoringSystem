<template>
  <div id="app">
    <el-row style="background-color: #409E44; color: #333; line-height: 60px;">
      <el-col :span="20">
        <i style="margin-left: 20px;color: #aaaaaa; font-size: 25px" class="el-icon-s-opportunity"></i>
        <span style="font-size: 25px; color: #ffffff">分布式机群监管系统DCMS</span>
      </el-col>
      <el-col :span="4" align="right">
        <i class="el-icon-setting" style="margin-right: 10px; color: #eeeeee; font-size: 20px;vertical-align: middle;"></i>
        <el-avatar style="vertical-align: middle; margin-right: 8px;"  src="./static/timeproxy.png"></el-avatar>
        <span style="vertical-align: middle; color: #eeeeee; margin-right: 20px">小店新开</span>
      </el-col>
    </el-row>

    <el-row>
      <el-container style="height: 100%; border: 1px solid #eee">
        <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
          <el-menu :default-openeds="['3']" @select="handleSelect">
            <el-menu-item index="/information">
              <i class="el-icon-help"></i>说明
            </el-menu-item>
            <el-menu-item index="/settings">
              <i class="el-icon-setting"></i>配置添加
            </el-menu-item>
            <el-submenu index="3">
              <template slot="title"><i class="el-icon-upload"></i>客户端节点</template>
              <el-menu-item-group>
                <el-menu-item index="realTimeStatus">节点信息</el-menu-item>
                <el-menu-item index="realTimeGraph">实时负载</el-menu-item>
                <el-menu-item index="historyGraph">历史负载</el-menu-item>
              </el-menu-item-group>
            </el-submenu>
          </el-menu>
        </el-aside>

        <el-container>
          <el-main>
            <router-view/>
          </el-main>
        </el-container>
      </el-container>
    </el-row>

  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      activeIndex: this.$route.path
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      this.$router.push(key);
    }
  }
}
</script>

<style>

</style>
