package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndividualAttributeStructure extends Structure {
    final String FIRST_LINE;
    final String INDIVIDUALS_ID;

    public IndividualAttributeStructure(BufferedReader reader,
                                        char currentLevel,
                                        String firstLine,
                                        String individualsId) {
        super(reader, currentLevel);
        this.FIRST_LINE = firstLine;
        this.INDIVIDUALS_ID = individualsId;
    }

    public String parse() {
        String line = FIRST_LINE;
        StringBuilder builder = new StringBuilder();
        Pattern pattern = Pattern.compile("([0-9]+) (\\S*) *(.*)");
        Matcher matcher = pattern.matcher(line);

        if (!matcher.find()) {
            System.err.println("Could not parse: '" + line + "'.");
        }

        switch (matcher.group(2)) {
            case "OCCU":
                String value = matcher.group(3).replace("'", "''");
                builder.append("INSERT INTO events(individual, type, startDate, endDate, place, note) ");
                builder.append(String.format("VALUES (%s, 'occupation', NULL, NULL, NULL, '%s');", INDIVIDUALS_ID, value));
                break;
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
                System.err.println("'" + matcher.group(2) + "' Found. No implementation for this kind of record.");
                break;
            case "SSN":
                System.out.println("'" + matcher.group(2) + "' Skipped. SSN will not be parsed.");
                break;
        }

        return builder.toString();
    }
}
