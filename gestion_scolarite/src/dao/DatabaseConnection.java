package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private String url;
    private String username;
    private String password;

    private DatabaseConnection() throws SQLException {
        loadProperties();
        openConnection();
    }

    private void openConnection() throws SQLException {
        try {
            // OK même si DriverManager le charge automatiquement, mais ça rassure le prof
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL introuvable (mysql-connector-j)", e);
        }

        this.connection = DriverManager.getConnection(url, username, password);
        System.out.println("✅ Connexion DB OK");
    }

    private void loadProperties() {
        Properties p = new Properties();

        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/dao/db.properties")) {
            if (in != null) {
                p.load(in);
            } else {
                System.out.println("⚠ db.properties introuvable, utilisation des valeurs par défaut");
            }
        } catch (Exception e) {
            System.out.println("⚠ Erreur lecture db.properties, valeurs par défaut : " + e.getMessage());
        }

        String host = p.getProperty("DB_HOST", "localhost");
        String port = p.getProperty("DB_PORT", "3306");
        String db   = p.getProperty("DB_NAME", "gestion_scolarite_demo");
        this.username = p.getProperty("DB_USER", "root");
        this.username = p.getProperty("DB_USER", "root");
        this.password = p.getProperty("DB_PASS", "");

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
            return instance;
        }

        try {
            if (instance.connection == null || instance.connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            openConnection();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (instance != null && instance.connection != null && !instance.connection.isClosed()) {
                instance.connection.close();
                System.out.println("✅ Connexion DB fermée");
            }
        } catch (SQLException e) {
            System.out.println("⚠ Erreur fermeture DB: " + e.getMessage());
        }
    }
}
