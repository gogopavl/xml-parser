package parser;

import dto.SegmentDto;
import exception.XMLParserException;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.XMLUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static validator.FileValidator.XML_EXTENSION;
import static validator.FileValidator.validateDirectory;
import static validator.FileValidator.validateXmlFile;

public class XMLParser {

    private static final Logger log = Logger.getLogger(XMLParser.class.getName());

    private final File input;

    private final Set<String> articles;

    private final List<SegmentDto> segments;

    @SneakyThrows
    public XMLParser(File input) {
        this.input = input;
        this.segments = new ArrayList<>();
        this.articles = Files.lines(Paths.get("src/main/resources/articles.txt"))
                .map(String::trim)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void parse() {
        log.info(format("Parsing {0}", input.getName()));
        if (input.isDirectory()) {
            extractDataFromDirectory(input);
        } else {
            extractDataFromFile(input);
        }
    }

    private void extractDataFromDirectory(File directory) {
        validateDirectory(directory);
        Stream.of(directory.listFiles())
                .filter(file -> XML_EXTENSION.equalsIgnoreCase(getExtension(file.getName())))
                .forEach(this::extractDataFromFile);
    }

    private void extractDataFromFile(File file) {
        validateXmlFile(file);
        try {
            Document document = XMLUtils.getDocumentFromXmlFile(file);
            appendElements(file, document);
        } catch (ParserConfigurationException | IOException | SAXException exception) {
            log.severe(exception.getMessage());
            throw new XMLParserException(exception.getMessage());
        }
    }

    private void appendElements(File file, Document document) {
        NodeList nodes = document.getElementsByTagName("segment");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);
            String nodeValue = getNodeValue(currentNode);
            if (isNotEmpty(nodeValue) && this.articles.contains(nodeValue)) {
                SegmentDto segmentDto = SegmentDto.fromNode(currentNode);
                segmentDto.setFilename(file.getName());
                segments.add(segmentDto);
            }
        }
    }

    private String getNodeValue(Node node) {
        return nonNull(node.getFirstChild()) && nonNull(node.getFirstChild().getNodeValue()) ?
                node.getFirstChild().getNodeValue().trim() : null;
    }

    public void writeToCsv() {
        log.info("Writing to segments.tsv");
        try (PrintWriter printWriter = new PrintWriter(new File("segments.tsv"))) {
            segments.stream()
                    .map(SegmentDto::toTsv)
                    .forEach(printWriter::println);
            printWriter.close();
        } catch (FileNotFoundException exception) {
            log.severe(exception.getMessage());
            throw new XMLParserException(exception.getMessage());
        }
    }
}
