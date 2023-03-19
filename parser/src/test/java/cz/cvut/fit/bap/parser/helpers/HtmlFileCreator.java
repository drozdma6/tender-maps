package cz.cvut.fit.bap.parser.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Creates new or returns already created html file for testing from url with given fileName
 */
public class HtmlFileCreator{
    public File ensureCreatedHtmlFile(String url, String fileName) throws IOException{
        // Create a file and write the HTML content to it
        File file = new File("src/test/resources/testFiles/" + fileName);
        if (file.exists()){
            return file;
        }

        // Connect to the URL and retrieve the HTML content
        Document doc = Jsoup.connect(url).get();

        FileWriter writer = new FileWriter(file);
        writer.write(doc.html());
        writer.close();
        return file;
    }
}
