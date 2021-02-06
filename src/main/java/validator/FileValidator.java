package validator;

import lombok.experimental.UtilityClass;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.io.FilenameUtils.getExtension;

@UtilityClass
public class FileValidator {

    public static final String XML_EXTENSION = "xml";

    public static void validateDirectory(File directory) {
        checkNotNull(directory, "The specified directory cannot be null");
    }

    public static void validateXmlFile(File file) {
        checkNotNull(file, "The specified file cannot be null");
        checkArgument(XML_EXTENSION.equalsIgnoreCase(getExtension(file.getName())), "The specified file must be a .xml file");
    }
}
