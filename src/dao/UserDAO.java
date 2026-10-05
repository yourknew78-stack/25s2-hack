package dao;

import dao.model.User;

import java.util.Iterator;
import java.util.UUID;

public class UserDAO extends DAO<User> {
    // TODO: apply the Singleton design pattern to this class.
    // You may modify the existing constructor, add new constructors,
    // and add new helper method and private fields.
    /**
     * Generates a UserDAO. We enforce uniqueness in usernames (but not in passwords),
     * and further two usernames are considered identical if they are equal, ignoring case
     */
    public UserDAO() {
        super((o1, o2) -> o1.username().compareToIgnoreCase(o2.username()));
        throw new UnsupportedOperationException("TODO: 待实现");
    }
    private static UserDAO instance;
    public static UserDAO getInstance() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    /**
     * Attempts to authenticate as a particular user. If the user exists
     * and their passwords match, the login is considered successful.
     * @param username the username
     * @param password the password
     * @return the User if successful, null otherwise
     */
    public User login(String username, String password) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    /**
     * Attempts to register a new user. Users must have unique usernames,
     * and their usernames must contain only alphanumeric characters.
     * Usernames can be between 4 and 20 characters long.
     * Passwords must be at least four characters long, and can include
     * any codepoints.
     * @param username the desired username
     * @param password the desired password
     * @return the newly-created User if successful, null otherwise
     */
    public User register(String username, String password) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    /**
     * Fetches a User by just a UUID
     * @param id the UUID to search for
     * @return the user if they exist, else null
     */
    public User getByUUID(UUID id) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

