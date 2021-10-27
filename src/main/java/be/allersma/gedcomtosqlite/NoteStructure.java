package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteStructure extends Structure {
    private final String STARTING_NOTE;
    private String result = null;

    public NoteStructure(BufferedReader reader, char currentLevel, String startingNote) {
        super(reader, currentLevel);
        STARTING_NOTE = startingNote;
    }

    public void parse() throws IOException {
        String line;
        StringBuilder builder = new StringBuilder(STARTING_NOTE);

        while (!isNewStructure() && (line = READER.readLine()) != null) {
            LineCounter.increment();
            line = line.replace("'", "''"); // Escape single quote in sqlite3. Somehow it ain't workin'

            // groups.get(1) = indent. In which level is the line defined?
            // groups.get(2) = record type. Tells about what kind of data it's about.
            // groups.get(3) = value.
            Pattern pattern = Pattern.compile("([0-9]+) (\\S*) *(.*)");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                // Check record type
                switch (matcher.group(2)) {
                    case "CONT":
                        builder.append(matcher.group(3)).append("\n");
                        break;
                    case "CONC":
                        builder.append(matcher.group(3));
                        break;
                    default:
                        System.err.println(String.format(
                                "ERROR: Unknown item: '%s' (%s)", line, LineCounter.getLineNumber())
                        );
                        break;
                }
            }
        }

        result = builder.toString();
    }

    public String getResult() {
        return result;
    }
}
