package org.ustc.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ustc.client.ClientApplication;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Command;
import org.ustc.common.pojo.Report;
import org.ustc.common.pojo.Status;
import org.ustc.common.utils.ClientUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

/**
 * ClassName: MyClientHandler
 * Package: org.ustc.client.netty
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/25
 * @Version 1.0
 */

@Slf4j
@ChannelHandler.Sharable
@Component
public class MyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ClientContext clientContext;

    public void setClientContext(ClientContext clientContext) {
        this.clientContext = clientContext;
    }


    // TODO: bug，客户端无法收到服务端发送的修改频率的消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        // 处理接收到的消息
        log.info("Received message from server: " + msg);
        long period = Long.parseLong(msg.toString(CharsetUtil.UTF_8));
        Client client = clientContext.getClient();
        BufferedWriter reportWriter = clientContext.getReportWriter();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Report report = new Report();
                report.setName(client.getName());
                report.setCpuNum(client.getCpuNum());
                report.setLoad(ClientUtil.getLoad());
                report.setOs(client.getOs());
                report.setIp(client.getIp());
                report.setTimeStamp(new Date());

                Command reportCommand = new Command();
                reportCommand.setType(Status.CLIENT_REPORT);
                HashMap<String, String> reportContents = new HashMap<>();
                reportContents.put("report", report.toString());
                reportContents.put("client", client.toString());
                reportCommand.setContents(reportContents);

                try {
                    reportWriter.write(reportCommand.toString());
                    reportWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        TimerManager.getInstance().schedule(timerTask, 0, period);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
