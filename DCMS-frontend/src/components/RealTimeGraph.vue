<template>
  <div>
    <div v-for="value in divList" :id="value" style="width: 800px;height:400px;"></div>
  </div>
</template>

<script>
import echarts from "echarts";

export default {
  name: "RealTimeGraph",
  data() {
    return {
      divList: []
    }
  },
  mounted() {
    //格式化Date类
    Date.prototype.Format = function (fmt) {
      var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
      };
      if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
      for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
      return fmt;
    }

    this.timeout = setInterval( () => {
      let nameList = {};
      this.$http.get("http://localhost:8011/realTimeClientsReport").then(res=> {
        for (let i = 0; i < res.data.length; ++i) {
          let name = res.data[i].name
          if (!nameList.hasOwnProperty(name)) {
            nameList[name] = {
              "load": [],
              "timeStamp": []
            };
          }
          nameList[name]["load"].push(res.data[i].load);
          let time = (new Date(res.data[i].timeStamp)).Format("yyyy-MM-dd hh:mm:ss");
          nameList[name]["timeStamp"].push(time);
        }
        console.log(nameList);
        for (let key in nameList) {
          this.divList.push(key);
        }
        this.$nextTick(function () {
          for (let key in nameList)
            this.drawLine(key, nameList[key]["timeStamp"], nameList[key]["load"]);
        })
      });
    }, 1000);
  },
  methods: {
    drawLine(name, timeStamp, load) {
      var myChart = echarts.init(document.getElementById(name));
      var option = {
        title: {
          text: name + " 系统实时负载"
        },
        xAxis: {
          data: timeStamp
        },
        yAxis: {
        },
        series: [{
          data: load,
          type: 'line'
        }]
      };
      myChart.setOption(option);
    },
  }
}
</script>

<style scoped>

</style>
