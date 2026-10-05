package persistentdata.io;

import java.io.*;

public class ComputerIOFactory implements IOFactory {
    private static final String FULL_FILENAME_TEMPLATE = "saved/%s.txt";

    @Override
    public Writer writer(String filename) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public Reader reader(String filename) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

