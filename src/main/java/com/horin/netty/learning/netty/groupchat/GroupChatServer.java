package com.horin.netty.learning.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

public class GroupChatServer {

  private final int port;

  private GroupChatServer(int port) {
    this.port = port;
  }

  private void start() throws Exception {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    ChannelFuture channelFuture = new ServerBootstrap().group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast("stringDecoder", new StringDecoder())
                .addLast("stringEncoder", new StringEncoder())
                .addLast(new ServerChannelHandler())
                .addLast(new IdleStateHandler(3, 6, 9, TimeUnit.SECONDS))
            .addLast(new HeartbeatChannelHandler())
            ;
          }
        })
        .bind(port)
        .sync();
    System.out.println("服务器启动了");
    channelFuture
        .channel()
        .closeFuture()
        .sync();
  }

  public static void main(String[] args) throws Exception {
    new GroupChatServer(7777).start();
  }

}
