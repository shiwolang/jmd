package zzy.jmd.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author zhouzhongyuan
 * @since 2017/6/28
 */
public abstract class ConfigUtils {
    public static String DOC_ROOT;
    public static String VIEW_ROOT;
    public final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        try {
            File file = ResourceUtils.getFile("classpath:config.json");
            String s = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            Map map = objectMapper.readValue(s, Map.class);
            Object docRoot = map.get("docRoot");
            Object viewRoot = map.get("viewRoot");
            if (docRoot == null) {
                throw new RuntimeException("docRoot error");
            } else {
                File dr = new File(docRoot.toString());
                if (dr.isDirectory() && dr.exists()) {
                    DOC_ROOT = dr.getAbsolutePath();
                } else {
                    throw new RuntimeException("docRoot error");
                }
            }
            if (viewRoot == null) {
                throw new RuntimeException("docRoot error");
            } else {
                File dr = new File(viewRoot.toString());
                if (dr.isDirectory() && dr.exists()) {
                    VIEW_ROOT = dr.getAbsolutePath();
                } else {
                    throw new RuntimeException("docRoot error");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File mdFile(String id) {
        return new File(DOC_ROOT + id + ".md");
    }

    public static File viewFile(String path) throws FileNotFoundException {
        return ResourceUtils.getFile(VIEW_ROOT + path);
    }
}
