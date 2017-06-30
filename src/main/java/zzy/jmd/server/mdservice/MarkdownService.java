package zzy.jmd.server.mdservice;


import org.apache.commons.io.FileUtils;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import zzy.jmd.server.ToolUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhou
 */
public class MarkdownService {
    private final HtmlRenderer renderer;
    private final Parser parser;

    public MarkdownService() {
        List<Extension> extensions = Arrays.asList(TablesExtension.create(), AutolinkExtension.create(), StrikethroughExtension.create());
        this.parser = Parser.builder().extensions(extensions).build();
        this.renderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    public String markdownToHtml(String id, String md) throws IOException {
        FileUtils.write(ToolUtils.mdFile(id), md, "UTF-8", false);
        return renderer.render(parser.parse(md));
    }

    public String openFile(String id) {
        try {
            File file = ToolUtils.mdFile(id);
            if (!file.exists()) {
                return "";
            }
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
