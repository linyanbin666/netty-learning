package com.horin.netty.learning.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class GroupChatClient {

  private final Selector selector;

  private final SocketChannel socketChannel;

  private GroupChatClient() throws IOException {
    Selector selector = Selector.open();
    SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(7777));
    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ);
    this.selector = selector;
    this.socketChannel = socketChannel;
  }

  private void readInfo() throws IOException {
    if (selector.select() < 0) {
      return;
    }
    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    while (keyIterator.hasNext()) {
      SelectionKey key = keyIterator.next();
      if (key.isReadable()) {
        SocketChannel sc = (SocketChannel) key.channel();
        sc.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()));
        byteBuffer.clear();
      }
      keyIterator.remove();
    }
  }

  private void sendMessage(String msg) throws IOException {
    socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
  }

  public static void main(String[] args) throws Exception {
    GroupChatClient groupChatClient = new GroupChatClient();
    new Thread(() -> {
      try {
        while (true) {
          groupChatClient.readInfo();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      String msg = scanner.nextLine();
      groupChatClient.sendMessage(msg);
    }
  }

}
