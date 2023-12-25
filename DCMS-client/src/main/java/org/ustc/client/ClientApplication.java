package org.ustc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.ustc.client.netty.ClientContext;
import org.ustc.client.netty.MyClientHandler;
import org.ustc.client.netty.TimerManager;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Command;
import org.ustc.common.pojo.Report;
import org.ustc.common.pojo.Status;
import org.ustc.common.utils.ClientUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ClassName: ClientApplication
 * Package: org.ustc.client
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */


@Slf4j
@Data
@SpringBootApplication
@ConfigurationProperties(prefix = "client")
public class ClientApplication implements CommandLineRunner {

    private int serverPort;

    private String serverIP;

    private Client client = new Client();

    @Resource
    private MyClientHandler myClientHandler;

    @Override
    public void run(String... args) throws Exception {
        // 设置客户端属性
        client.setName(ClientUtil.getRandomName());
        client.setOnline(true);
        client.setCpuNum(ClientUtil.getCpuNumber());
        client.setOs(ClientUtil.getOS());

        // 连接服务端
        log.info("Connect to: " + serverIP + ":" + serverPort);
        Socket reportSocket = new Socket(serverIP, serverPort);

        String address = reportSocket.getLocalAddress().toString();
        address = address.substring(1) + ':' + reportSocket.getLocalPort();
        client.setIp(address);
        log.info("local host: " + address);

        final BufferedWriter reportWriter = new BufferedWriter(new OutputStreamWriter(reportSocket.getOutputStream()));

        // 创建ClientContext对象，并保存client和reportWriter的引用
        ClientContext clientContext = new ClientContext();
        clientContext.setClient(client);
        clientContext.setReportWriter(reportWriter);

        // 将ClientContext对象传递给MyClientHandler
        myClientHandler.setClientContext(clientContext);

        // 设置CLIENT_ONLINE的command
        Command onlineCommand = new Command();
        onlineCommand.setType(Status.CLIENT_ONLINE);
        HashMap<String, String> onlineContents = new HashMap<>();
        onlineContents.put("client", client.toString());
        onlineCommand.setContents(onlineContents);

        // 输出command内容到服务端
        reportWriter.write(onlineCommand.toString());
        reportWriter.flush();

        // 设置每秒的任务
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
        TimerManager.getInstance().schedule(timerTask, 0, 5000);

        // 设置在关闭程序时向服务器发送下线的信息，然后关闭输出流
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    // 设置CLIENT_OFFLINE的command
                    Command offlineCommand = new Command();
                    offlineCommand.setType(Status.CLIENT_OFFLINE);
                    HashMap<String, String> offlineContents = new HashMap<>();
                    offlineContents.put("client", client.toString());
                    offlineCommand.setContents(offlineContents);
                    // 输出command内容到服务端
                    reportWriter.write(offlineCommand.toString());
                    reportWriter.flush();
                    // 关闭输出流
                    reportWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 添加handler
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("framer", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()))
                                    .addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast("handler", myClientHandler)
                                    .addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(serverIP, serverPort).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
