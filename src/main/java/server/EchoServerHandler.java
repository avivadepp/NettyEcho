package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable                                        //该实例可以在channel中共享
public class EchoServerHandler extends
        ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx,
                            Object msg) {
        ByteBuf in = (ByteBuf) msg;
        //使用数组、偏移量、参数作为方法调用的参数
        ByteBuf heapBuf=Unpooled.buffer(100);
        byte[] src=new byte[]{'1','2'};
        heapBuf.writeBytes(src);
        /*if (heapBuf.hasArray()) {                //1
            byte[] array = heapBuf.array();        //2
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();                //3
            int length = heapBuf.readableBytes();//4
            //前面是ascii码表数，超出下标输出0，超出容量后报错
            System.out.println(offset+" "+length+" "+array[0]+" "+array[1]+" "+array[2]);
        }
        if (!in.hasArray()) {            //1
            int length = in.readableBytes();//2
            byte[] array = new byte[length];    //3
            in.getBytes(in.readerIndex(), array);        //4
            System.out.println(array[0]);
        }*/
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer(200);
        ByteBuf headerBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        ByteBuf bodyBuf = ByteBufAllocator.DEFAULT.directBuffer();
        messageBuf.addComponents(heapBuf, in);
        //messageBuf.removeComponent(0); // 移除头
        for (int i = 0; i < messageBuf.numComponents(); i++) {
            System.out.println(messageBuf.component(i).toString());
            //UnpooledDuplicatedByteBuf(ridx: 0, widx: 0, cap: 256, unwrapped: PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 256))
        }
        int length = messageBuf.readableBytes();
        System.out.println("length"+length);           //长度为0，想不通
        byte[] arr = new byte[length];
        messageBuf.getBytes(messageBuf.readerIndex(), arr);
        //System.out.println(arr[0]+" "+arr[1]);
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));        //输出日志到控制台
        ctx.write(in);                            //将接收的消息返回给发送者


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)//冲刷所有待审核消息到远程结点
                .addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();                //打印异常堆栈
        ctx.close();                            //关闭通道
    }
}