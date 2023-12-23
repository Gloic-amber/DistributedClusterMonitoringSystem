package org.ustc.common.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.ToString;

/**
 * ClassName: Client
 * Package: org.ustc.common.pojo
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */

@Data
public class Client {
    // 是否在线
    private boolean isOnline;

    // 客户端ip地址
    private String ip;

    // 客户端名称
    private String name;

    // 操作系统
    private String os;

    // cpu数量
    private int cpuNum;

    @Override
    public String toString() {
        return JSON.toJSONString(this) + "\n";
    }
}
