package com.horin.netty.learning.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class GroupChatServer {

  private final Selector selector;

  private final ServerSocketChannel listenChannel;

  private GroupChatServer() throws IOException {
    Selector selector = Selector.open();
    ServerSocketChannel listenChannel = ServerSocketChannel.open();
    listenChannel.bind(new InetSocketAddress(7777));
    listenChannel.configureBlocking(false);
    listenChannel.register(selector, SelectionKey.OP_ACCEPT);
    this.selector = selector;
    this.listenChannel = listenChannel;
  }

  private void listen() {
    while (true) {
      try {
        if (selector.select(2000) == 0) {
          continue;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
      ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        SocketChannel sc = null;
        try {
          if (key.isAcceptable()) {
            sc = listenChannel.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            System.out.println(sc.getRemoteAddress() + " 上线了");
          } else if (key.isReadable()) {
            sc = (SocketChannel) key.channel();
            sc.read(byteBuffer);
            String msg = new String(byteBuffer.array());
            System.out.println("服务器接收到 " + sc.getRemoteAddress() + " 发送的消息：" + msg);
            byteBuffer.clear();
            forwardMessage(sc.getRemoteAddress() + " 说：" + msg, sc);
          }
        } catch (IOException e) {
          if (sc != null) {
            try {
              System.out.println(sc.getRemoteAddress() + " 下线了");
              key.cancel();
              sc.close();
            } catch (IOException e2) {
              e2.printStackTrace();
            }
          }
        }
        keyIterator.remove();
      }
    }
  }

  private void forwardMessage(String msg, SocketChannel self) throws IOException {
    for (SelectionKey key : selector.keys()) {
      SelectableChannel channel = key.channel();
      if (channel instanceof SocketChannel && (channel != self)) {
        SocketChannel dest = (SocketChannel) channel;
        dest.write(ByteBuffer.wrap(msg.getBytes()));
      }
    }
  }

  public static void main(String[] args) throws Exception {
    GroupChatServer groupChatServer = new GroupChatServer();
    groupChatServer.listen();
  }

}
