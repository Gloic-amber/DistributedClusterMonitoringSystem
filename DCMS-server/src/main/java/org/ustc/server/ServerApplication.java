package org.ustc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ustc.common.pojo.Client;
import org.ustc.server.netty.MyServerHandler;
import org.ustc.server.service.RecordService;

import java.util.List;

/**
 * ClassName: ServerApplication
 * Package: org.ustc.server
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */


@Slf4j
@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

    @Value("${serverPort}")
    private Integer serverPort;

    @Resource
    private RecordService recordService;

    @Resource
    private MyServerHandler myServerHandler;

    @Override
    public void run(String... args) throws Exception {

        // 先查询数据库，获得所有客户端
        List<Client> clientList = recordService.findAllClients();

        // 将客户端载入内存，并设置为下线
        for (Client client: clientList) {
            client.setOnline(false);
            recordService.clients.getClients().put(client.getName(), client);
        }

        log.info("start server at " + serverPort);
        // 创建两个EventLoopGroup对象。实际上，它们就是两个NIO线程组，
        // bossGroup主要用于接收客户端的连接，而workerGroup主要用于SocketChannel的读写处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建ServerBootstrap对象，用于配置Server相关参数，并启动Server
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)

                    // 配置NioServerSocketChannel的选项和处理器
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

                    // 设置连接的处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("framer", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()))
                                    .addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast("register", myServerHandler)
                                    .addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                        }
                    });
            log.info("server listening on port: " + serverPort);

            // 绑定端口，并同步等待成功，即启动服务器
            ChannelFuture channelFuture = bootstrap.bind(serverPort).sync();

            // 同步等待服务器通道关闭，即服务器关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭两个线程组
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
