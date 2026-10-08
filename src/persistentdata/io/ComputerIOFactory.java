package persistentdata.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class ComputerIOFactory implements IOFactory {
    private static final String FULL_FILENAME_TEMPLATE = "saved/%s.txt";

    @Override
    public Writer writer(String filename) {
        try {
            File file = new File(FULL_FILENAME_TEMPLATE.formatted(filename));
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();
            return new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Reader reader(String filename) {
        try {
            File file = new File(FULL_FILENAME_TEMPLATE.formatted(filename));
            if (!file.isFile()) return null;
            return new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
}
