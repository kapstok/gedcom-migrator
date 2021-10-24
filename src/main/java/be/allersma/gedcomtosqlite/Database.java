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
        creationStatement.append("ID INTEGER PRIMARY KEY AUTOINCREMENT,");
        creationStatement.append("GIVEN TEXT,");
        creationStatement.append("LASTNAME TEXT,");
        creationStatement.append("NICKNAME TEXT,");
        creationStatement.append("NOTE TEXT,");
        creationStatement.append("SEX TEXT");
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
            System.err.println(String.format("Something went wrong with the statement. Details:\n%s", e.getMessage()));
        } finally {
            close();
        }
    }

    private void open() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            System.err.println(String.format("Could not connect to database. Details:\n%s", e.getMessage()));
            System.exit(1);
        }

        System.out.println("Connected to database!");
    }

    private void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println(String.format("Could not close connection. Details:\n%s", e.getMessage()));
            System.exit(1);
        }

        System.out.println("Connection to database successfully closed!");
    }
}
