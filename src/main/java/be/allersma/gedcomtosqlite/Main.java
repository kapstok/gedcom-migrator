package be.allersma.gedcomtosqlite;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args[0].equals("--version")) {
            printVersionInfo();
        }

        FileReader reader;
        File file = new File(args[0]);
        reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        GedcomParser parser = new GedcomParser(br);

        Database db = new Database(args[1]);
        db.createNewDatabase();
        db.execute(parser.parse());
    }

    public static void printVersionInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("GEDCOM specification: ");
        builder.append("5.5.1");
        System.out.println(builder);
        System.exit(0);
    }
}
