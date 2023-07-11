package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DATEStructure extends Structure {
    private final String DATE_VALUE;
    private String result = null;

    public DATEStructure(BufferedReader reader, char currentLevel, String startingNote) {
        super(reader, currentLevel);
        DATE_VALUE = startingNote;
    }

    public void parse() {
        StringBuilder builder = new StringBuilder();

        Pattern pattern = Pattern.compile("DATE (.*)");
        Matcher matcher = pattern.matcher(DATE_VALUE);
        String dateValue = matcher.group(1);

        if (isDate(dateValue))
            System.err.println("Unsupported DATE value. Value is of type DATE from Gedcom 5.5.1 specification.");

        else if (isDatePeriod(dateValue))
            System.err.println("Unsupported DATE value. Value is of type DATE_PERIOD from Gedcom 5.5.1 specification.");

        else if (isInterpretedDate(dateValue))
            System.err.println("Unsupported DATE value. Value is of type INT from Gedcom 5.5.1 specification.");

        else
            builder.append(parseDateValue(dateValue));

        result = builder.toString();
    }

    /**
     * Date as in DATE defined by Gedcom 5.5.1 specification
     */
    private boolean isDate(String date) {
        String[] DATE_CALENDAR_ESCAPE = {"@#DHEBREW@", "@#DROMAN@", "@#DFRENCH R@", "@#DGREGORIAN@",
        "@#DJULIAN@", "@#DUNKNOWN@"};

        if (date.equals("")) {
            return true;
        }

        String firstWord = date.split(" ", 2)[0];
        return Arrays.asList(DATE_CALENDAR_ESCAPE).contains(firstWord);
    }

    private boolean isDatePeriod(String date) {
        String firstWord = date.split(" ", 2)[0];

        return firstWord.equals("FROM") || firstWord.equals("TO");
    }

    private boolean isInterpretedDate(String date) {
        String firstWord = date.split(" ", 2)[0];
        return firstWord.equals("INT");
    }

    private String parseDateValue(String date) {
        String[] substring = date.split(" ", 2);

        switch (substring[0]) { // firstWord
            case "ABT":
                return "rond " + substring[1];
            case "CAL":
                return substring[1];
            case "EST":
                return "naar schatting " + substring[1];
            case "AFT":
                return "na " + substring[1];
            case "BEF":
                return "voor " + substring[1];
            case "BET":
                Pattern pattern = Pattern.compile("(.*) AND (.*)");
                Matcher matcher = pattern.matcher(substring[1]);
                return "tussen " + matcher.group(1) + " en " + matcher.group(2);
            default:
                return date;
        }
    }

    public String getResult() {
        return "'" + result + "'";
    }
}
