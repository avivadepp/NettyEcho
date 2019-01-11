package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }
    public static void main(String[] args) throws Exception {
        /*if (args.length != 1) {
            System.err.println(
                    "Usage: " + EchoServer.class.getSimpleName() +
                            " <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);        //设置端口值*/
        int port=8050;
        new EchoServer(port).start();                //调用start方法
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup(); //创建EventLoopGroup,主要职责是注册channel和执行一些runnable任务
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    //创建 ServerBootstrap，负责引导服务器和客户端,监听所有连接
                    .channel(NioServerSocketChannel.class)        //指定使用 NIO 的传输 Channel
                    .localAddress(new InetSocketAddress(port))    //设置 socket 地址使用所选的端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //添加 EchoServerHandler 到 Channel 的 ChannelPipeline
                        //一个新的连接出现，一个新的channel将会被创建,所有channel由一个线程处理
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new EchoServerHandler());
                        }
                    });

            ChannelFuture f = b.bind().sync();
            //绑定服务器;sync等待服务器关闭
            //为什么没有调用connect？
            f.addListener(new ChannelFutureListener() {    //3
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {                //4
                        System.out.println("Write successful");
                    } else {
                        System.err.println("Write error");    //5
                        future.cause().printStackTrace();
                    }
                }
            });
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();            //关闭 channel 和 块，直到它被关闭
        } finally {
            group.shutdownGracefully().sync();            //关闭 EventLoopGroup，释放所有资源
        }
    }

}
