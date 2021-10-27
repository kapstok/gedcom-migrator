package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class INDIStructure extends Structure {
    private final String KEY;
    private String sex;

    public INDIStructure(BufferedReader reader, String key) {
        super(reader, '0');
        this.KEY = key.substring(2, key.length() - 1);

        try {
            parse();
        } catch (IOException e) {
            System.err.println("Could not parse INDI record. Details: \n" + e.getMessage());
        }
    }

    public String databaseQuery() {
        StringBuilder result = new StringBuilder();
        String values = String.format("VALUES (%s, %s, %s, %s, %s);",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "'" + sex + "'");

        result.append("INSERT INTO individuals (given, lastname, nickname, note, sex) ");
        result.append(values);

        return result.toString();
    }

    private void parse() throws IOException {
        String line;

        while ((line = READER.readLine()) != null && !isNewStructure()) {
            // groups.get(1) = indent. In which level is the line defined?
            // groups.get(2) = record type. Tells about what kind of data it's about.
            // groups.get(3) = value.
            Pattern pattern = Pattern.compile("([0-9]+) (\\S*) *(.*)");
            Matcher matcher = pattern.matcher(line);

            if (!isNewStructure()) {
                if (matcher.find()) {
                    // Check record type
                    switch (matcher.group(2)) {
                        case "SEX":
                            System.out.println("Found SEX item: '" + line + "'");
                            sex = matcher.group(3);
                            break;
                        default:
                            System.out.println("Unkown item: '" + line + "'");
                            break;
                    }
                }
            }
        }

        if (sex == null) {
            sex = "U";
            System.out.println("Missing SEX record, therefore it has been set to 'U' (Unknown).");
        }
    }
}
