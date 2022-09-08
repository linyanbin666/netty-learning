package com.horin.netty.learning.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Scanner;

public class GroupChatClient {

  private final String host;
  private final int port;

  private GroupChatClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  private void start() throws Exception {
    NioEventLoopGroup group = new NioEventLoopGroup();
    ChannelFuture channelFuture = new Bootstrap()
        .group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast("stringDecoder", new StringDecoder())
                .addLast("stringEncoder", new StringEncoder())
                .addLast(new ClientChannelHandler());
          }
        })
        .connect(host, port)
        .sync();
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      channelFuture.channel().writeAndFlush(line);
    }
  }

  public static void main(String[] args) throws Exception {
    new GroupChatClient("127.0.0.1", 7777).start();
  }

}
