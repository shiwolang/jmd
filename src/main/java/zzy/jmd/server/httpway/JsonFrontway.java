package zzy.jmd.server.httpway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhouzhongyuan
 * @since 2017/5/15
 */
public final class JsonFrontway extends Frontway {
    private static Logger LOGGER = LoggerFactory.getLogger(JsonFrontway.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Pattern routePattern;
    private final String replacement;

    public JsonFrontway(String routePattern, String replacement) {
        this.replacement = replacement;
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JodaModule());
        this.routePattern = Pattern.compile(routePattern);
    }

    @Override
    protected String parseRoute(String fullRoute) {
        Matcher matcher = routePattern.matcher(fullRoute);
        if (matcher.find()) {
            return matcher.replaceAll(replacement);
        }
        return fullRoute;
    }

    @Override
    protected Object[] deserialize(byte[] content, Class[] valueTypes) {
        String json = new String(content, Charset.forName("UTF-8"));
        if (valueTypes.length == 0 || content.length < 1) {
            return new Object[0];
        }
        try {
            if (valueTypes.length == 1) {
                Object o = objectMapper.readValue(content, valueTypes[0]);
                return new Object[]{o};
            }
            if (json.startsWith("[") && valueTypes.length > 1) {
                JsonNode jsonNode = objectMapper.readTree(json);
                Object[] objects = new Object[valueTypes.length];
                for (int i = 0; i < valueTypes.length; i++) {
                    Class valueType = valueTypes[i];
                    JsonNode node = jsonNode.get(i);
                    if (node != null) {
                        objects[i] = objectMapper.treeToValue(node, valueType);
                    } else {
                        objects[i] = null;
                    }
                }

                return objects;
            }
        } catch (IOException e) {
            LOGGER.warn("msg:{}, deserialize error content is {},valueTypes:{}, raw:{}", e.getMessage(), json, Arrays.toString(valueTypes), e);
        }
        throw new IllegalArgumentException(String.format("deserialize error content is %s", json));
    }

    @Override
    protected byte[] serialize(EndResult endResult) {
        try {
            return objectMapper.writeValueAsBytes(endResult);
        } catch (JsonProcessingException e) {
            LOGGER.warn("serialize error endResult is {}, raw:{}", endResult, e);
        }
        return new byte[0];
    }

    public void registerCodecModule(Module module) {
        objectMapper.registerModule(module);
    }
}
