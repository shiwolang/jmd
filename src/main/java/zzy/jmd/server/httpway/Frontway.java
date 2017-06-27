package zzy.jmd.server.httpway;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouzhongyuan
 * @since 2017/5/15
 */
public abstract class Frontway {
    private static Logger LOGGER = LoggerFactory.getLogger(Frontway.class);
    private final List<FrontwayModule> frontwayModuleList = new ArrayList<>();

    public Frontway registerModule(FrontwayModule frontwayModule) {
        Validate.notNull(frontwayModule);
        for (FrontwayModule module : frontwayModuleList) {
            frontwayModule.checkRoutes(module);
        }
        LOGGER.debug("registerModule:{}, Actions:{}", frontwayModule.getName(),frontwayModule.getActions());
        frontwayModuleList.add(frontwayModule);
        return this;
    }

    public byte[] processRequest(RequestEntity requestEntity) {
        String fullRoute = requestEntity.getRoute();
        String parseRoute = parseRoute(fullRoute);
        LOGGER.debug("processRequest,parseRoute:{}, RequestEntity:{}", parseRoute, requestEntity);
        EndResult endResult = new EndResult();
        endResult.setStatus(EndResultStatus.W_404);
        endResult.setMsg("route not found");
        for (FrontwayModule frontwayModule : frontwayModuleList) {
            Action action = frontwayModule.findAction(parseRoute);
            if (action == null) {
                continue;
            }
            try {
                Class<?>[] parameterTypes = action.getParameterTypes();
                ArrayList<Class> paramNoReq = new ArrayList<>();
                for (Class<?> parameterType : parameterTypes) {
                    if (parameterType.equals(RequestEntity.class)) {
                        continue;
                    }
                    paramNoReq.add(parameterType);
                }
                Object[] paramsValues = deserialize(requestEntity.getParamsBody(), paramNoReq.toArray(new Class<?>[paramNoReq.size()]));
                Object[] params = new Object[parameterTypes.length];
                int j = 0;
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    if (parameterType.equals(RequestEntity.class)) {
                        params[i] = requestEntity;
                    } else {
                        params[i] = paramsValues[j];
                        j++;
                    }
                }

                try {
                    Object invoke = action.invoke(params);
                    endResult.setData(invoke);
                    endResult.setMsg(null);
                    endResult.setStatus(EndResultStatus.C_200);
                } catch (Throwable e) {
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        ExceptionHandler exceptionHandler = frontwayModule.findExceptionHandler(cause);
                        if (exceptionHandler != null) {
                            EndResultException handler = exceptionHandler.handler(cause);
                            if (handler != null) {
                                throw handler;
                            }
                        }
                        throw cause;
                    }
                    throw e;
                }
            } catch (EndResultException e) {
                endResult.setStatus(e.getStatus());
                endResult.setMsg(e.getMessage());
            } catch (Throwable e) {
                LOGGER.warn("processRequest error,requestEntity:{},raw:{}", requestEntity, e);
                endResult.setStatus(EndResultStatus.E_500);
                endResult.setMsg("系统异常，请联系客服");
            }
            break;
        }

        return serialize(endResult);
    }

    protected abstract String parseRoute(String fullRoute);

    protected abstract Object[] deserialize(byte[] content, Class[] valueType);

    protected abstract byte[] serialize(EndResult endResult);
}
