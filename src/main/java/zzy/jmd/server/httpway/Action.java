package zzy.jmd.server.httpway;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhouzhongyuan
 * @since 2017/5/15
 */
final class Action {
    private String route;
    private Object object;
    private Method method;

    public Action(String route, Object object, Method method) {
        Validate.notNull(route, "Action route is null");
        Validate.notNull(object, "Action object is null");
        Validate.notNull(method, "Action method is null");
        this.route = route;
        this.object = object;
        this.method = method;
    }

    public Object invoke(Object[] params0) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(object, params0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        return new EqualsBuilder()
                .append(route, action.route)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(route)
                .toHashCode();
    }

    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("method", method)
                .append("route", route)
                .toString();
    }
}
