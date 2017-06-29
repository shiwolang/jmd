package zzy.jmd.server.mdservice;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.Before;
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
    private MarkdownService markdownService;

    @Before
    public void setUp() throws Exception {
        this.markdownService = new MarkdownService();
    }

    @Test
    public void name1() throws Exception {
        List<Extension> extensions = Arrays.asList(TablesExtension.create(), AutolinkExtension.create(), StrikethroughExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(code + table);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        System.out.println(renderer.render(document));
    }

    @Test
    public void testMarkdownToHtml() throws Exception {
        String s = markdownService.markdownToHtml("a/b", "");
        System.out.println(s);
    }

    @Test
    public void testOpenFile() throws Exception {
        String s = markdownService.openFile("a/b");
        System.out.println(s);
    }
}