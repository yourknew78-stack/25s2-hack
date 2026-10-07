package persistentdata.formatted;

import persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Writer;

public class CSVWriter implements FormattedWriter<String[]> {
    private final CSVFormat format;
    private final Writer writer;

    public CSVWriter(CSVFormat format, Writer writer) {
        this.format = format;
        this.writer = writer;
    }

    private boolean firstRow = true;
    @Override
    public void putHeader() {
        firstRow = true;
    }

    @Override
    public void putNext(String[] data) {
        if (data == null || data.length != format.COLUMN_COUNT) {
            throw new PersistentDataException("Expected %s columns".formatted(format.COLUMN_COUNT));
        }
        try {
            if (!firstRow) writer.write(format.LINE_SEPARATOR);
            firstRow = false;
            for (int i = 0; i < data.length; i++) {
                if (i > 0) writer.write(format.FIELD_SEPARATOR);
                String value = data[i] == null ? "" : data[i];
                boolean escape = value.indexOf(format.FIELD_SEPARATOR) >= 0
                        || value.indexOf(format.LINE_SEPARATOR) >= 0
                        || value.indexOf(format.ESCAPE_MARKER) >= 0;
                if (escape) writer.write(format.ESCAPE_MARKER);
                for (int j = 0; j < value.length(); j++) {
                    char c = value.charAt(j);
                    if (c == format.ESCAPE_MARKER) writer.write(c);
                    writer.write(c);
                }
                if (escape) writer.write(format.ESCAPE_MARKER);
            }
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        }
    }

    @Override
    public void putFooter() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        }
    }
}

