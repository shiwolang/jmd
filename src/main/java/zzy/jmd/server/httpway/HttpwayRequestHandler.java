package zzy.jmd.server.httpway;

import org.apache.commons.io.IOUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import zzy.jmd.server.ToolUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhouzhongyuan
 * @since 0.5
 */
public class HttpwayRequestHandler extends SimpleChannelUpstreamHandler {
    private Logger LOGGER = LoggerFactory.getLogger(HttpwayRequestHandler.class);
    private final Frontway frontway;
    private final ThreadPoolExecutor threadPoolExecutor;
    public final static Map<String, String> mines = new HashMap<>();

    static {
        try {
            String s = IOUtils.toString(ToolUtils.resourceStream("mime.types"), Charset.forName("UTF-8"));
            String[] ms = StringUtils.delimitedListToStringArray(s, ";");
            for (String m : ms) {
                if (!StringUtils.hasText(m)) {
                    continue;
                }
                String[] split = StringUtils.split(m, " ");
                if (split.length != 2) {
                    continue;
                }
                String mine = StringUtils.trimWhitespace(split[0]);
                String types = StringUtils.trimWhitespace(split[1]);
                String[] typesarr = StringUtils.delimitedListToStringArray(types, " ");
                for (String type : typesarr) {
                    if (!StringUtils.hasText(type)) {
                        continue;
                    }
                    mines.put(StringUtils.trimAllWhitespace(type), mine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpwayRequestHandler(Frontway frontway, ThreadPoolExecutor standardThreadExecutor) {
        this.frontway = frontway;
        this.threadPoolExecutor = standardThreadExecutor;

    }

    /**
     * ----------
     * IdleStateEvent 超时检测
     */
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) e;
            IdleState state = stateEvent.getState();
            if (state.equals(IdleState.ALL_IDLE)) {
                LOGGER.debug("IdleState.ALL_IDLE:{}", ctx.getChannel().getRemoteAddress());
                ctx.getChannel().write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.warn("HttpwayRequestHandler error : {}", e.getCause());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        final Channel channel = ctx.getChannel();
        if (!(message instanceof DefaultHttpRequest)) {
            LOGGER.warn("messageReceived a not DefaultHttpRequest,{}", message.getClass());
            channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        final DefaultHttpRequest httpRequest = (DefaultHttpRequest) message;
        final boolean keepAlive = HttpHeaders.isKeepAlive(httpRequest);

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultHttpResponse defaultHttpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                defaultHttpResponse.addHeader("Access-Control-Allow-Origin", "*");
                defaultHttpResponse.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
                defaultHttpResponse.addHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");

                HttpMethod method = httpRequest.getMethod();
                try {
                    if (method.equals(HttpMethod.GET)) {
                        String uri = httpRequest.getUri();
                        URL url = new URL("http://127.0.0.1" + uri);
                        String path = url.getPath();
                        if (Objects.equals(path, "/")) {
                            path = "/index.html";
                        }
                        String filenameExtension = org.springframework.util.StringUtils.getFilenameExtension(path);
                        String mine = mines.get(filenameExtension);
                        if (mine == null) {
                            path = "/index.html";
                            filenameExtension = org.springframework.util.StringUtils.getFilenameExtension(path);
                            mine = mines.get(filenameExtension);
                        }
                        defaultHttpResponse.addHeader("Content-Type", mine + "; charset=utf-8");

                        try {
                            String context = IOUtils.toString(ToolUtils.viewStream(path), Charset.forName("UTF-8"));
                            byte[] contextBytes = context.getBytes();
                            HttpHeaders.setContentLength(defaultHttpResponse, contextBytes.length);
                            defaultHttpResponse.setContent(ChannelBuffers.wrappedBuffer(contextBytes));
                        } catch (Exception e) {
                            HttpHeaders.setContentLength(defaultHttpResponse, 0);
                            defaultHttpResponse.setContent(ChannelBuffers.EMPTY_BUFFER);
                        }
                    } else if (method.equals(HttpMethod.OPTIONS)) {
                        HttpHeaders.setContentLength(defaultHttpResponse, 0);
                        defaultHttpResponse.setContent(ChannelBuffers.EMPTY_BUFFER);
                    } else if (method.equals(HttpMethod.POST)) {
                        ChannelBuffer content = httpRequest.getContent();
                        int readableBytes = content.readableBytes();
                        byte[] dst = new byte[readableBytes];
                        content.getBytes(0, dst);
                        RequestEntity requestEntity = new RequestEntity(httpRequest.getUri()
                            , dst
                        );
                        //ip获取
                        try {
                            String ipString = httpRequest.getHeader("X-Real-IP");
                            InetSocketAddress remoteAddress0;
                            if (ipString == null || "".equals(ipString) || "0:0:0:0:0:0:0:1".equals(ipString)
                                || "localhost".equals(ipString)) {
                                SocketAddress remoteAddress = channel.getRemoteAddress();
                                if (remoteAddress instanceof InetSocketAddress) {
                                    remoteAddress0 = (InetSocketAddress) remoteAddress;
                                } else {
                                    remoteAddress0 = new InetSocketAddress("127.0.0.1", 0);
                                }
                            } else {
                                remoteAddress0 = new InetSocketAddress(ipString, 0);
                            }
                            requestEntity.setRemoteAddress(remoteAddress0);
                        } catch (Exception e1) {
                            LOGGER.warn("requestEntity.setRemoteAddress error:{}", e1.getMessage());
                        }
                        defaultHttpResponse.addHeader("Content-Type", "application/json; charset=utf-8");
                        //处理请求
                        byte[] frontwayResponse = frontway.processRequest(requestEntity);
                        HttpHeaders.setContentLength(defaultHttpResponse, frontwayResponse.length);
                        defaultHttpResponse.setContent(ChannelBuffers.wrappedBuffer(frontwayResponse));
                    }
                } catch (Exception e) {
                    LOGGER.warn("frontway.processRequest error:{}", e);
                    byte[] contentBytes = ("\"" + e.getMessage() + "\"").getBytes();
                    HttpHeaders.setContentLength(defaultHttpResponse, contentBytes.length);
                    defaultHttpResponse.setContent(ChannelBuffers.wrappedBuffer(contentBytes));
                } finally {
                    HttpHeaders.setKeepAlive(defaultHttpResponse, keepAlive);
                    ChannelFuture writeFuture;
                    writeFuture = channel.write(defaultHttpResponse);
                    if (!keepAlive) {
                        writeFuture.addListener(ChannelFutureListener.CLOSE);
                    }
                }
            }
        });
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("channelClosed:{}", ctx.getChannel().getRemoteAddress());
    }
}
