package server;

import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.nio.channels.Channel;

public class SslChannelInitializer extends ChannelInitializer<Channel> {

    private final SslContext context;
    private final boolean startTls;
    public SslChannelInitializer(SslContext context,
                                 boolean client, boolean startTls) {   //1
        this.context = context;
        this.startTls = startTls;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine engine = context.newEngine(ch.alloc());  //2
        engine.setUseClientMode(client); //3
        ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls));  //4
    }
}