package persistentdata;

import dao.PostDAO;
import dao.ReactionDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.Reaction;
import dao.model.User;
import persistentdata.formatted.CSVFormat;
import persistentdata.formatted.CSVFormattedFactory;
import persistentdata.io.ComputerIOFactory;
import persistentdata.io.IOFactory;
import persistentdata.serialization.MessageSerializer;
import persistentdata.serialization.PostSerializer;
import persistentdata.serialization.ReactionSerializer;
import persistentdata.serialization.UserSerializer;

public class DataManager {
    private static DataManager instance;
    public static DataManager getInstance() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    private IOFactory IO;

    // We have assumed that most solutions to the serialization task in week-5 will
    // use a 4-column schema for Users. If this is not the case, you may need to
    // change the number below.
    //
    // TODO: 每种数据用什么格式、几列、存到哪个文件, 全部由你决定(见 Task 3)。
    private DataPipeline<User, String[]> userPipeline;

    private DataPipeline<Post, String[]> postPipeline;

    private DataPipeline<Message, String[]> messagePipeline;

    // TODO: Task 3 —— 回应的持久化管线
    private DataPipeline<Reaction, String[]> reactionPipeline;

    private UserDAO users;
    private PostDAO posts;
    private ReactionDAO reactions;

    public void readAll() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public void writeAll() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

