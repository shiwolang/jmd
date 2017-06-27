package zzy.jmd.server.httpway;


import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 请求后的最终结果的信息组合
 * 弱化spring mvc web应用的概念，转为http web的前端接口概念
 *
 */
public class EndResult {
    private String msg;// 提示信息，这个信息是可以直接用于显示的信息
    private EndResultStatus status;// 状态信息
    private Object data;// 信息主体

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public EndResultStatus getStatus() {
        return status;
    }

    public void setStatus(EndResultStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("msg", msg)
                .append("status", status)
                .append("data", data)
                .toString();
    }


}