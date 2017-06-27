package zzy.jmd.server.httpway;

/**
 * @author zhouzhongyuan
 * @since 2017/3/7
 */
public final class EndResultStatus {
    public final static EndResultStatus C_200 = new EndResultStatus("C_200");//成功
    public final static EndResultStatus W_300 = new EndResultStatus("W_300");//警告
    public final static EndResultStatus E_500 = new EndResultStatus("E_500");//错误
    public final static EndResultStatus W_404 = new EndResultStatus("W_404");//没有找到对应的route
    //==
    private final String code;

    public EndResultStatus(String code) {

        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
