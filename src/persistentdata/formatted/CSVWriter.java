package persistentdata.formatted;

import persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Writer;

public class CSVWriter implements FormattedWriter<String[]> {
    private final CSVFormat format;
    private final Writer writer;

    public CSVWriter(CSVFormat format, Writer writer) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    private boolean firstRow = true;
    @Override
    public void putHeader() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public void putNext(String[] data) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public void putFooter() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

