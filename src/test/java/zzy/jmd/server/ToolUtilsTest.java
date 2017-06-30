package zzy.jmd.server;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author zhouzhongyuan
 * @since 2017/6/30
 */
public class ToolUtilsTest {

    @Test
    public void testResourceStream() throws Exception {
        InputStream inputStream = ToolUtils.resourceStream("mime.types");
        System.out.println(IOUtils.toString(inputStream, Charset.forName("UTF-8")));
    }

    @Test
    public void test1() throws Exception {
        String filenameExtension = org.springframework.util.StringUtils.getFilenameExtension("/todo-work/2017-06-30.md");
        System.out.println(filenameExtension);

    }
}