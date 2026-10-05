package persistentdata.formatted;

import java.io.Reader;
import java.io.Writer;

public class CSVFormattedFactory implements FormattedFactory<String[]> {
    private final CSVFormat format;

    public CSVFormattedFactory(CSVFormat format) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public FormattedWriter<String[]> writer(Writer documentWriter) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public FormattedReader<String[]> reader(Reader documentReader) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

