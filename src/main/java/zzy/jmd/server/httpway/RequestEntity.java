package zzy.jmd.server.httpway;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @author zhouzhongyuan
 * @since 2017/5/15
 */
public final class RequestEntity {
    private static Logger LOGGER = LoggerFactory.getLogger(RequestEntity.class);

    private String route;
    private byte[] paramsBody;
    private InetSocketAddress remoteAddress;

    public RequestEntity(String route, byte[] paramsBody) {
        this.route = route;
        this.paramsBody = paramsBody;
    }

    public String getRoute() {
        return route;
    }


    public byte[] getParamsBody() {
        return paramsBody;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public RequestEntity setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    public RequestEntity setParamsBody(byte[] paramsBody) {
        this.paramsBody = paramsBody;
        return this;
    }

    public RequestEntity setRoute(String route) {
        this.route = route;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("route", route)
                .append("remoteAddress", remoteAddress)
                .append("paramsBody", new String(paramsBody, Charset.forName("UTF-8")))
                .toString();
    }
}
