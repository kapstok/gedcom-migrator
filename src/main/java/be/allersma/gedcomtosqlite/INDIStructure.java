package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.io.IOException;

public class INDIStructure extends Structure {
    private final String KEY;

    public INDIStructure(BufferedReader reader, String key) throws IOException {
        super(reader, '0');
        this.KEY = key.substring(2, key.length() - 1);

        // Random stuff to test things
        isNewStructure();
        System.out.println(reader.readLine());
        System.out.println(reader.readLine());
        isNewStructure();
        System.out.println(reader.readLine());
    }

    public String databaseQuery() {
        StringBuilder result = new StringBuilder();

        result.append("INSERT INTO individuals (GIVEN, LASTNAME, NICKNAME, NOTE, SEX) ");
        result.append("VALUES (NULL, NULL, NULL, NULL, NULL);");

        return result.toString();
    }
}
