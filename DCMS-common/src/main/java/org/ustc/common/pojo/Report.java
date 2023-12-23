package org.ustc.common.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * ClassName: Report
 * Package: org.ustc.common.pojo
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */


@Data
public class Report {

    private String name;

    private String os;

    private Date timeStamp;

    private double load;

    private int cpuNum;

    private String ip;

    @Override
    public String toString() {
        return JSON.toJSONString(this) + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Double.compare(load, report.load) == 0 && cpuNum == report.cpuNum && Objects.equals(name, report.name) && Objects.equals(os, report.os) && Objects.equals(timeStamp, report.timeStamp) && Objects.equals(ip, report.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, os, timeStamp, load, cpuNum, ip);
    }
}
