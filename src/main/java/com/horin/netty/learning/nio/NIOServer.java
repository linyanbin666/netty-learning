package com.horin.netty.learning.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

  public static void main(String[] args) throws Exception {
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.bind(new InetSocketAddress(7777));
    Selector selector = Selector.open();
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    while (true) {
      if (selector.select(1000) == 0) {
        System.out.println("等待1s后无连接...");
        continue;
      }
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        if (key.isAcceptable()) {
          SocketChannel socketChannel = serverSocketChannel.accept();
          socketChannel.configureBlocking(false);
          socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        } else if (key.isReadable()) {
          SocketChannel channel = (SocketChannel) key.channel();
          ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
          channel.read(byteBuffer);
          System.out.println("获取客户端数据：" + new String(byteBuffer.array()));
        }
        keyIterator.remove();
      }
    }
  }

}
