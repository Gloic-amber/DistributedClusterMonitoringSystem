package org.ustc.common.pojo;

import lombok.Data;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: Clients
 * Package: org.ustc.common.pojo
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */

@Data
public class Clients {

    private volatile ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clients clients1 = (Clients) o;
        return Objects.equals(clients, clients1.clients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clients);
    }
}
