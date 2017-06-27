package zzy.jmd.server.httpway;



/**
 * @author zhouzhongyuan
 * @since 2017/5/15
 */
public interface ExceptionHandler {
    EndResultException handler(Throwable e);
}
