package org.ustc.client.netty;

import lombok.Data;
import org.ustc.common.pojo.Client;

import java.io.BufferedWriter;

/**
 * ClassName: ClientContext
 * Package: org.ustc.client.netty
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/25
 * @Version 1.0
 */

@Data
public class ClientContext {

    private Client client;

    private BufferedWriter reportWriter;
}
