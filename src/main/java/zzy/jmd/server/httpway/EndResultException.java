package zzy.jmd.server.httpway;

/**
 * @author zhouzhongyuan
 * @since  2017/3/7
 */
public class EndResultException extends RuntimeException {
    private EndResultStatus status;

    public EndResultException(String message) {
        this(EndResultStatus.W_300, message);
    }

    public EndResultException(EndResultStatus status, String message) {
        super(message);
        this.status = status;
    }

    public EndResultStatus getStatus() {
        return status;
    }
}
