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
        this.ioFactory = ioFactory;
        this.formattedFactory = formattedFactory;
        this.serializer = serializer;
        this.filename = filename;
    }

    private static UserDAO users;
    private static PostDAO posts;

    public void writeFrom(Iterator<T> iterator) {
        if (iterator == null) return;
        Writer writer = ioFactory.writer(filename);
        if (writer == null) return;
        try {
            FormattedWriter<S> formattedWriter = formattedFactory.writer(writer);
            formattedWriter.putHeader();
            while (iterator.hasNext()) {
                formattedWriter.putNext(serializer.serialize(iterator.next()));
            }
            formattedWriter.putFooter();
        } finally {
            try {
                writer.close();
            } catch (IOException ignored) {
            }
        }
    }

    public interface AddToDAO<T> {
        void run(T item);
    }

    public void readTo(AddToDAO<T> callback) {
        if (callback == null) return;
        Reader reader = ioFactory.reader(filename);
        if (reader == null) return;
        try {
            FormattedReader<S> formattedReader = formattedFactory.reader(reader);
            while (formattedReader.hasNext()) {
                callback.run(serializer.deserialize(formattedReader.getNext()));
            }
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
    }
}
