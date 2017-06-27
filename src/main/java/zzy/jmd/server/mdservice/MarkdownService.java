package zzy.jmd.server.mdservice;


import java.util.UUID;

/**
 * Created by zhou on 2017/6/24.
 */
public class MarkdownService {

    public String id(String name) {
        UUID uuid = UUID.randomUUID();

        return uuid.toString();
    }

//    public String markdownToHtml(String md) {
//        return pegDownProcessor.markdownToHtml(md);
//    }
}
