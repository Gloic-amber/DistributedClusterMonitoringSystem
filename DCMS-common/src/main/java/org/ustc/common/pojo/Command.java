package org.ustc.common.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.HashMap;
import java.util.Objects;

/**
 * ClassName: Command
 * Package: org.ustc.common.pojo
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */
@Data
public class Command {

    private Status type;

    private HashMap<String, String> contents;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return type == command.type && Objects.equals(contents, command.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, contents);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this) + "\n";
    }
}
