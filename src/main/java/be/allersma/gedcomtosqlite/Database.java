package be.allersma.gedcomtosqlite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            try {
                writeErrorStatementToFile("/tmp/error-statement.txt", query);
            } catch (IOException e2) {
                System.out.println("Could not write statement to file. Details:\n" + e2.getMessage());
            }
        } finally {
            close();
        }
    }

    private void writeErrorStatementToFile(String path, String content) throws IOException {
        File file = new File(path);
        if (file.createNewFile()) {
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();
            System.out.printf("Statement written to '%s'%n", path);
        } else {
            System.out.printf("'%s' exists. Removing file...%n", path);

            if (file.delete()) {
                System.out.println("Done!");
                writeErrorStatementToFile(path, content);
            } else {
                System.err.println("Error: could not delete file.");
            }
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
