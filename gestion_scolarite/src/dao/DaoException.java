package dao;

public class DaoException extends Exception {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    // utile pour envelopper une SQLException avec un message propre
    public static DaoException wrap(String action, Throwable cause) {
        return new DaoException("Erreur DAO (" + action + ") : " + cause.getMessage(), cause);
    }
}
