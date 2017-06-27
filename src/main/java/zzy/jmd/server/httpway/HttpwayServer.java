package zzy.jmd.server.httpway;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by zhou on 2017/2/8.
 * 简单的一个http的服务器，如果不能承载后期再做优化
 */
public class HttpwayServer {
    private final Timer timer = new HashedWheelTimer();

    private final ThreadPoolExecutor threadPoolExecutor;
    private final Frontway frontway;
    private final Integer port;

    public HttpwayServer(Integer port, ThreadPoolExecutor threadPoolExecutor, Frontway frontway) {
        this.port = port;
        this.threadPoolExecutor = threadPoolExecutor;
        this.frontway = frontway;
    }

    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new HttpServerPipelineFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        bootstrap.bind(new InetSocketAddress(port));
        System.out.println("HttpwayServer start... :" + port);
    }

    private class HttpServerPipelineFactory implements ChannelPipelineFactory {
        public ChannelPipeline getPipeline() throws Exception {
            ChannelPipeline pipeline = Channels.pipeline();
            pipeline.addLast("idlestatehandler", new IdleStateHandler(timer, 0, 0, 60));
            pipeline.addLast("httpRequestDecoder", new HttpServerCodec());
            pipeline.addLast("httpContentCompressor", new HttpContentCompressor());
            pipeline.addLast("httpChunkAggregator", new HttpChunkAggregator(Integer.MAX_VALUE));
            pipeline.addLast("handler", new HttpwayRequestHandler(frontway, threadPoolExecutor));
            return pipeline;
        }
    }
}
