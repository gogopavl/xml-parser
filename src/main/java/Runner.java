import org.xml.sax.SAXException;
import parser.XMLParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Runner {

    private static final Logger log = Logger.getLogger(Runner.class.getName());

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        log.info("Starting execution...");
        XMLParser parser = new XMLParser(new File("src/main/resources/error-files"));
        parser.parse();
        parser.writeToCsv();
        log.info("Completed execution");
    }
}
