package zzy.jmd.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author zhouzhongyuan
 * @since 2017/6/28
 */
public abstract class ToolUtils {
    public static String DOC_ROOT;
    public final static ObjectMapper objectMapper = new ObjectMapper();

    public static void init(String path) throws IOException {
        File file = new File(path);
        String s = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        Map map = objectMapper.readValue(s, Map.class);
        Object docRoot = map.get("docRoot");
        if (docRoot == null) {
            throw new RuntimeException("docRoot error");
        } else {
            File dr = new File(docRoot.toString());
            if (dr.isDirectory() && dr.exists()) {
                DOC_ROOT = dr.getAbsolutePath() + "/";
            } else {
                throw new RuntimeException("docRoot error");
            }
        }
    }


    public static File mdFile(String id) {
        String s = DOC_ROOT + id + ".md";
        return new File(s);
    }

    public static InputStream viewStream(String path) throws IOException {
        return resourceStream("view" + path);
    }

    public static InputStream resourceStream(String path) throws IOException {
        return ToolUtils.class.getResource("/" + path).openStream();
    }
}
