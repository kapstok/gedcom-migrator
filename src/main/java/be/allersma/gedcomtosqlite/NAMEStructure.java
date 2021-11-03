package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NOTE: as of gedcom 5.1 nicknames are no longer specified between double quotes.
 */
public class NAMEStructure extends Structure {
    static final Pattern nameRecordPattern = Pattern.compile("1 NAME ([^/]*) ?/*([^/]*)/*([^/]*)");

    private final String surnames;
    private String givenNames;
    private String nicknames;

    public NAMEStructure(BufferedReader reader, char currentLevel, String startingName) {
        super(reader, currentLevel);
        givenNames = parseGivenNames(startingName).map(s -> "'" + s + "'").orElse("NULL");
        surnames = parseSurnames(startingName).map(s -> "'" + s + "'").orElse("NULL");
        nicknames = "NULL";

        if (givenNames.contains("Antie")) {
            System.out.println("Antie found");
        }

        if (parseSuffix(startingName).isPresent()) {
            System.err.println(
                    "Didn't expect a name having a suffix. Didn't implement this yet. Sorry! Name: " +
                    parseSuffix(startingName)
            );
        }
    }

    public String getGivenNames() {
        return this.givenNames;
    }

    public String getSurnames() {
        return this.surnames;
    }

    public String getNicknames() {
        return this.nicknames;
    }

    // TODO: Implement NOTE record once a 'names' tables is added to Database.
    public void parsePersonalNamePiecesStructure() throws IOException {
        String line;

        while (!isNewStructure() && (line = READER.readLine()) != null) {
            LineCounter.increment();
            line = line.replace("'", "''");

            // groups.get(1) = indent. In which level is the line defined?
            // groups.get(2) = record type. Tells about what kind of data it's about.
            // groups.get(3) = value.
            Pattern pattern = Pattern.compile("([0-9]+) (\\S*) *(.*)");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                // Check record type
                switch (matcher.group(2)) {
                    case "NOTE":
                        System.out.println("Found NOTE item: '" + line + "'");
                        NOTEStructure noteStructure = new NOTEStructure(READER, '2', matcher.group(3));
                        noteStructure.parse();
                        System.err.println("Not implemented NOTE record here. Didn't expect it");
                        break;
                    case "GIVN":
                        if (!givenNames.equals("NULL")) {
                            String msg = String.format(
                                    "Got given names already! Replacing '%s' with '%s'",
                                    givenNames,
                                    matcher.group(3)
                            );
                            System.out.println(msg);
                        }
                        givenNames = "'" + matcher.group(3) + "'";
                        break;
                    case "NICK":
                        nicknames = "'" + matcher.group(3) + "'";
                        break;
                    case "NPFX":
                    case "SPFX":
                    case "SURN":
                    case "NSFX":
                        System.out.println("'" + matcher.group(2) + "' tag not implemented. Skipping ...");
                        break;
                    default:
                        System.err.println(String.format(
                                "ERROR: Unknown item: '%s' (%s)", line, LineCounter.getLineNumber())
                        );
                        break;
                }
            }
        }
    }

    static Optional<String> parseGivenNames(String input) {
        String[] names = input.split(" ");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < names.length; i++) {
            if (names[i].startsWith("/"))
                break;

            if(i != 0) {
                builder.append(" ");
            }

            builder.append(names[i]);
        }

        String result = builder.toString();
        return result.isBlank() ? Optional.empty() : Optional.of(result);
    }

    static Optional<String> parseSurnames(String input) {
        Pattern pattern = Pattern.compile("(.*)/(.*)/(.*)");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            return Optional.empty();
        }

        String result = matcher.group(2);
        return result.isBlank() ? Optional.empty() : Optional.of(result);
    }

    static Optional<String> parseSuffix(String input) {
        Pattern pattern = Pattern.compile("(.*)/(.*)/ (.*)");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            return Optional.empty();
        }

        String result = matcher.group(3);
        return result.isBlank() ? Optional.empty() : Optional.of(result);
    }
}
