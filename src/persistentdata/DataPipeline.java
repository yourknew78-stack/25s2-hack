package persistentdata;

import persistentdata.formatted.*;
import persistentdata.io.IOFactory;
import persistentdata.serialization.*;
import dao.*;

import java.io.*;
import java.util.Iterator;

public class DataPipeline<T, S> {
    private final IOFactory ioFactory;
    private final FormattedFactory<S> formattedFactory;
    private final Serializer<T, S> serializer;
    private final String filename;

    public DataPipeline(IOFactory ioFactory, FormattedFactory<S> formattedFactory, Serializer<T, S> serializer, String filename) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    private static UserDAO users;
    private static PostDAO posts;

    public void writeFrom(Iterator<T> iterator) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public interface AddToDAO<T> {
        void run(T item);
    }

    public void readTo(AddToDAO<T> callback) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

