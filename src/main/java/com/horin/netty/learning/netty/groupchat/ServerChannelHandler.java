package com.horin.netty.learning.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerChannelHandler extends SimpleChannelInboundHandler<String> {

  private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    channelGroup.writeAndFlush("用户：" + ctx.channel().remoteAddress() + " 上线了！");
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    channelGroup.writeAndFlush("用户：" + ctx.channel().remoteAddress() + " 下线了！");
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    System.out.println("用户：" + ctx.channel().remoteAddress() + " 加入了！");
    channelGroup.add(ctx.channel());
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    System.out.println("用户：" + ctx.channel().remoteAddress() + " 离开了！");
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    Channel channel = ctx.channel();
    channelGroup.forEach(c -> {
      if (c == channel) {
        channel.writeAndFlush("自己：" + channel.remoteAddress() + " 发送了：" + msg);
      } else {
        c.writeAndFlush("用户：" + channel.remoteAddress() + " 发送了：" + msg);
      }
    });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }

}
