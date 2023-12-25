package org.ustc.server.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Command;
import org.ustc.common.pojo.Report;
import org.ustc.common.pojo.Status;
import org.ustc.server.service.RecordService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
@ChannelHandler.Sharable // 由于MyServerHandler被spring管理，只有一个实例，因此添加Sharable注解
@Component
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    // 创建一个Map来保存客户端的ChannelHandlerContext对象
    private Map<String, ChannelHandlerContext> clients = new ConcurrentHashMap<>();

    @Resource
    private RecordService recordService;

    public void updateFrequency(String clientName, String frequency) {
        ChannelHandlerContext ctx = clients.get(clientName);
        if (ctx != null) {
            ByteBuf message = Unpooled.copiedBuffer(frequency, CharsetUtil.UTF_8);
            ChannelFuture future = ctx.writeAndFlush(message);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        System.out.println("Message sent successfully");
                    } else {
                        System.out.println("Failed to send message");
                        future.cause().printStackTrace();
                    }
                }
            });
        }
    }

    // 处理收到的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Command command = JSON.parseObject(msg.toString(), Command.class);
        Client client = JSON.parseObject(command.getContents().get("client"), Client.class);
        switch (command.getType()) {
            case CLIENT_ONLINE: {
                // 将client保存到数据库中
                System.out.println(command);
                log.info(client.getName() + " online");
                recordService.saveClient(client);
                // 保存client的ChannelHandlerContext对象
                clients.put(client.getName(), ctx);
                break;
            }
            case CLIENT_REPORT: {
                Report report = JSON.parseObject(command.getContents().get("report"), Report.class);
                System.out.println(report);
                recordService.saveRecord(report);
                break;
            }
            case CLIENT_OFFLINE:{
                // 移除client的ChannelHandlerContext对象
                clients.remove(client.getName());
                // 修改为下线
                recordService.clients.getClients().get(client.getName()).setOnline(false);
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
//        cause.printStackTrace();
        ctx.close();
    }
}
