package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GedcomParser {
    private BufferedReader reader;
    private String line;

    public GedcomParser(BufferedReader reader) {
        this.reader = reader;
    }

    public String parse() throws IOException {
        StringBuilder builder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            // groups.get(1) = indent. In which level is the line defined?
            // groups.get(2) = record type. Tells about what kind of data it's about.
            // groups.get(3) = value.
            Pattern pattern = Pattern.compile("([0-9]+) (\\S*) *(.*)");
            Matcher matcher = pattern.matcher(line);
            LineCounter.increment();

            if (matcher.find()) {
                // Check whether it's a root item
                if (matcher.group(1).equals("0")) {
                    // Check record type
                    switch (matcher.group(3)) {
                        case "INDI":
                            System.out.println("Found INDI item: '" + line + "'");
                            INDIStructure structure = new INDIStructure(reader, matcher.group(2));
                            builder.append(structure.databaseQuery());
                            break;
                        case "FAM":
                        case "":
                            System.out.println("Found root item: '" + line + "'");
                            break;
                        default:
                            System.out.println(String.format(
                                    "Unknown root item: '%s' (%s)", line, LineCounter.getLineNumber())
                            );
                            break;
                    }
                }
            } else {
                System.err.println(String.format("Unparsable line: '%s' (%s)", line, LineCounter.getLineNumber()));
            }
        }

        return builder.toString();
    }
}
