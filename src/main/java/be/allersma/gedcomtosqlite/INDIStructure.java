package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class INDIStructure extends Structure {
    private final String KEY;
    private String sex;
    private String note = null;
    private String givenNames = null;
    private String surnames = null;
    private String nicknames = null;
    private StringBuilder additionalStatements = new StringBuilder();

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
        String values = String.format("VALUES (%s, %s, %s, %s, %s, %s);",
                KEY,
                givenNames == null ? "NULL" : givenNames,
                surnames == null ? "NULL" : surnames,
                nicknames == null ? "NULL" : nicknames,
                note == null ? "NULL" : note,
                "'" + sex + "'");

        result.append("INSERT INTO individuals (id, given, lastname, nickname, note, sex) ");
        result.append(values);
        result.append(additionalStatements.toString());

        return result.toString();
    }

    private void parse() throws IOException {
        String line;

        while (!isNewStructure() && (line = READER.readLine()) != null) {
            // groups.get(1) = indent. In which level is the line defined?
            // groups.get(2) = record type. Tells about what kind of data it's about.
            // groups.get(3) = value.
            Pattern pattern = Pattern.compile("([0-9]+) (\\S*) *(.*)");
            Matcher matcher = pattern.matcher(line);
            LineCounter.increment();

            if (matcher.find()) {
                // Check record type
                switch (matcher.group(2)) {
                    case "SEX":
                        sex = matcher.group(3);
                        break;
                    case "NOTE":
                        NOTEStructure noteStructure = new NOTEStructure(READER, '1', matcher.group(3));
                        noteStructure.parse();
                        note = noteStructure.getResult();
                        break;
                    case "NAME":
                        NAMEStructure nameStructure = new NAMEStructure(READER, '1', matcher.group(3));
                        nameStructure.parsePersonalNamePiecesStructure();
                        givenNames = nameStructure.getGivenNames();
                        surnames = nameStructure.getSurnames();
                        nicknames = nameStructure.getNicknames();
                        break;
                    case "OCCU":
                    case "CAST":
                    case "DSCR":
                    case "EDUC":
                    case "IDNO":
                    case "NATI":
                    case "NCHI":
                    case "NMR":
                    case "PROP":
                    case "RELI":
                    case "RESI":
                    case "TITL":
                    case "FACT":
                    case "SSN":
                        IndividualAttributeStructure IAStructure = new IndividualAttributeStructure(READER, '1', line, KEY);
                        additionalStatements.append("\n").append(IAStructure.parse());
                        break;
                    default:
                        System.out.printf(
                                "Unknown item: '%s' (%s)%n", line, LineCounter.getLineNumber());
                        break;
                }
            }
        }

        if (sex == null) {
            sex = "U";
            System.out.println("Missing SEX record, therefore it has been set to 'U' (Unknown).");
        }
    }
}
