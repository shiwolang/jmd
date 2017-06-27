package zzy.jmd.server.mdservice;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhou on 2017/6/24.
 */
public class MarkdownServiceTest {
    String table = "First Header  | Second Header\n" +
            "-------------: | -------------\n" +
            "Content <br /> Cell  | Content Cell\n" +
            "Content Cell  | Content Cell" +
            "";
    String code = "```javascript \n" +
            "var a = 1; \n" +
            "```\n";

    @Test
    public void name1() throws Exception {
        List<Extension> extensions = Arrays.asList(TablesExtension.create(), AutolinkExtension.create(), StrikethroughExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(code + table);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        System.out.println(renderer.render(document));
    }
}