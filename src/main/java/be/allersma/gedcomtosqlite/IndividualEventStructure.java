package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndividualEventStructure extends Structure {
    final String FIRST_LINE;
    final String INDIVIDUALS_ID;

    public IndividualEventStructure(BufferedReader reader,
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
            case "BIRT":
            case "DEAT":
            case "BURI":
            case "CREM":
            case "BAPM":
                // builder.append(parseDateValue(currentLevel + 1));
//                String value = matcher.group(3).replace("'", "''");
//                builder.append("INSERT INTO events(individual, type, startDate, endDate, place, note) ");
//                builder.append(String.format("VALUES (%s, 'occupation', NULL, NULL, NULL, '%s');", INDIVIDUALS_ID, value));
//                break;
                // TODO: Implement
                break;
            case "CHR":
            case "ADOP":
            case "BARM":
            case "BASM":
            case "BLES":
            case "CHRA":
            case "CONF":
            case "FCOM":
            case "ORDN":
            case "NATU":
            case "EMIG":
            case "IMMI":
            case "CENS":
            case "PROB":
            case "WILL":
            case "GRAD":
            case "RETI":
            case "EVEN":
                System.err.println("'" + matcher.group(2) + "' Found. No implementation for this kind of record.");
                break;
        }

        return builder.toString();
    }

    @Deprecated
    public String parseEventDetail(char currentLevel) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;

        while (!isNewStructure() && (line = READER.readLine()) != null) {
            // groups.get(1) = indent. In which level is the line defined?
            // groups.get(2) = record type. Tells about what kind of data it's about.
            // groups.get(3) = value.
            Pattern pattern = Pattern.compile("([0-9]+) (\\S*) *(.*)");
            Matcher matcher = pattern.matcher(line);
            LineCounter.increment();

            switch (matcher.group(2)) {
                case "DATE":
                    DATEStructure dateStructure = new DATEStructure(READER, currentLevel, matcher.group(3));
                    dateStructure.parse();
                    dateStructure.getResult();
                    break;
                case "PLAC":
                    PLACStructure placeStructure = new PLACStructure(READER, currentLevel, matcher.group(3));
                    break;
                case "NOTE":
                    NOTEStructure noteStructure = new NOTEStructure(READER, currentLevel, matcher.group(3));
                    break;
                case "AGNC":
                case "RELI":
                case "CAUS":
                case "RESN":
                // Cases from ADDRESS_STRUCTRUE:
                case "ADDR":
                case "PHON":
                case "EMAIL":
                case "FAX":
                case "WWW":
                default:
                    System.err.println("'" + matcher.group(2) + "' Found. No implementation for this kind of record.");
                    break;
            }
        }
        return "";
    }
}
