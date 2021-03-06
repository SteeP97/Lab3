package HashTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class lab2 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        download(new URI("https://www.mirea.ru/"), new HashSet<>());

    }

    public static void download(URI link, HashSet<URI> visited) {
        if (visited.contains(link) || visited.size() >= 16)
            return;
        visited.add(link);
        System.out.println(link);
        try {
            URL url = link.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String html;
            try (InputStream is = conn.getInputStream()) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while (true) {
                    int c = is.read();
                    if (c < 0) break;
                    bos.write(c);
                }
                html = (bos.toString("CP1251"));
            }
            conn.disconnect();
            Pattern p = Pattern.compile("href\\s*=\\s*([^\\s>]+|'[^']*'|\"[^\"]*\")");
            Matcher matcher = p.matcher(html);
            while (matcher.find()) {
                String href = matcher.group(1);
                if (href.startsWith("'") || href.startsWith("\"")) {
                    href = href.substring(1, href.length() - 1);
                }
                URI child = link.resolve(href);
                //download(child, visited);
                Runnable action = () -> download(child, visited);
                new Thread (action).start();
            }
        } catch (Exception error) {
            System.out.println(error);
        }
    }
}
