package org.ustc.server.netty;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Command;
import org.ustc.common.pojo.Report;
import org.ustc.common.pojo.Status;
import org.ustc.server.service.RecordService;

/**
 * ClassName: MyServerHandler
 * Package: org.ustc.server.netty
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */

@Slf4j
@Component
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private RecordService recordService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Command command = JSON.parseObject(msg.toString(), Command.class);
        Client client = JSON.parseObject(command.getContents().get("client"), Client.class);
        switch (command.getType()) {
            case CLIENT_ONLINE: {
                System.out.println(command);
                log.info(client.getName() + " online");
                recordService.clients.getClients().put(client.getName(), client);
                break;
            }
            case CLIENT_REPORT: {
                Report report = JSON.parseObject(command.getContents().get("report"), Report.class);
                System.out.println(report);
                recordService.saveRecord(report);
                break;
            }
        }
        if (!recordService.clients.getClients().containsKey(client.getName())
                || !recordService.clients.getClients().get(client.getName()).isOnline()) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String address = ctx.channel().remoteAddress().toString();
        address = address.substring(1);
        log.info(address + " 主机offline");
        for (String key: recordService.clients.getClients().keySet()) {
            Client client = recordService.clients.getClients().get(key);
            if (client.getIp().equals(address)) {
                recordService.clients.getClients().get(key).setOnline(false);
            }
        }
        cause.printStackTrace();
        ctx.close();
    }
}
