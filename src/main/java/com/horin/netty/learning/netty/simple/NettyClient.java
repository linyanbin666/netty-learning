package com.horin.netty.learning.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;

public class NettyClient {

  public static void main(String[] args) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    Runtime.getRuntime().addShutdownHook(new Thread(group::shutdownGracefully));
    ChannelFuture channelFuture = new Bootstrap().group(group)
        .channel(NioSocketChannel.class)
        .handler(new NettyClientChannelHandler())
        .connect(new InetSocketAddress(7777))
        .sync();
    channelFuture.channel().closeFuture().sync();
  }

}
