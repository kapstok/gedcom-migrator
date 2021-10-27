package be.allersma.gedcomtosqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private Connection connection = null;
    private String path;

    public Database(String path) {
        this.path = path;
    }

    public void createNewDatabase() {
        StringBuilder creationStatement = new StringBuilder();

        creationStatement.append("CREATE TABLE individuals (");
        creationStatement.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
        creationStatement.append("given TEXT,");
        creationStatement.append("lastname TEXT,");
        creationStatement.append("nickname TEXT,");
        creationStatement.append("note TEXT,");
        creationStatement.append("sex TEXT NOT NULL");
        creationStatement.append(");");

        execute(creationStatement.toString());
    }

    public void execute(String query) {
        try {
            open();
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            System.err.println("Something went wrong with the statement. Details:\n" + e.getMessage());
            System.err.println("On statement: " + query);
        } finally {
            close();
        }
    }

    private void open() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            System.err.println("Could not connect to database. Details:\n" + e.getMessage());
            System.exit(1);
        }

        System.out.println("Connected to database!");
    }

    private void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("Could not close connection. Details:\n" + e.getMessage());
            System.exit(1);
        }

        System.out.println("Connection to database successfully closed!");
    }
}
