package zzy.jmd.server.httpway;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzhongyuan
 * @since 2017/5/15
 */
public final class FrontwayModule {
    private static Logger LOGGER = LoggerFactory.getLogger(FrontwayModule.class);
    private final String name;
    private final Map<String, Action> actions = new HashMap<>();
    private final Map<Class, ExceptionHandler> exceptionHandlers = new HashMap<>();

    public FrontwayModule(String name) {
        this.name = name;
    }

    public FrontwayModule addAction(String route, Object object, Method method) {
        Action action = new Action(route, object, method);
        actions.put(route, action);
        return this;
    }

    public FrontwayModule addExceptionHandler(Class exceptionType, ExceptionHandler exceptionHandler) {
        exceptionHandlers.put(exceptionType, exceptionHandler);
        return this;
    }

    protected Action findAction(String route) {
        return actions.get(route);
    }

    protected void checkRoutes(FrontwayModule frontwayModule) {
        for (Map.Entry<String, Action> entry : frontwayModule.actions.entrySet()) {
            String key = entry.getKey();
            Validate.isTrue(!(actions.keySet().contains(key)), String.format("route: %s exist", key));
        }
    }

    protected ExceptionHandler findExceptionHandler(Throwable e) throws EndResultException {
        return exceptionHandlers.get(e.getClass());
    }

    public String getName() {
        return name;
    }

    public Map<String, Action> getActions() {
        return actions;
    }
}
