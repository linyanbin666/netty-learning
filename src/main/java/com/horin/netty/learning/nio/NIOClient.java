package com.horin.netty.learning.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

  public static void main(String[] args) throws Exception {
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    if (!socketChannel.connect(new InetSocketAddress(7777))) {
      while (!socketChannel.finishConnect()) {
        System.out.println("等待连接完成...");
      }
    }
    ByteBuffer byteBuffer = ByteBuffer.wrap("测试数据".getBytes());
    socketChannel.write(byteBuffer);
    System.in.read();
  }

}
