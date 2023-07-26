package be.allersma.gedcomtosqlite.utils;

import org.folg.gedcom.model.Name;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NOTE: as of gedcom 5.1 nicknames are no longer specified between double quotes.
 */
public class FormattedNameUtil {
    public static Name parseNameValue(Name name) {
        Optional<String> givenNames = parseGivenNames(name.getValue());
        Optional<String> surnames = parseSurnames(name.getValue());
        Optional<String> suffix = parseSuffix(name.getValue());

        if (name.getGiven() == null)
            givenNames.ifPresent(name::setGiven);
        if (name.getSurname() == null)
            surnames.ifPresent(name::setSurname);
        if (name.getSuffix() == null)
            suffix.ifPresent(name::setSuffix);

        return name;
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

        String result = builder.toString().trim();
        return result.isBlank() ? Optional.empty() : Optional.of(result);
    }

    static Optional<String> parseSurnames(String input) {
        Optional<String> surname = surnameOnly(input);
        if (surname.isPresent()) return surname;
        surname = surnameAndSuffix(input);
        if (surname.isPresent()) return surname;
        surname = givenAndSurname(input);
        if (surname.isPresent()) return surname;
        return completeName(input);
    }

    private static Optional<String> surnameOnly(String input) {
        Pattern pattern = Pattern.compile("^/(.+)/$");
        return match(input, pattern);
    }

    private static Optional<String> surnameAndSuffix(String input) {
        Pattern pattern = Pattern.compile("^/(.+)/ .*$");
        return match(input, pattern);
    }

    private static Optional<String> givenAndSurname(String input) {
        Pattern pattern = Pattern.compile("^.* /(.+)/$");
        return match(input, pattern);
    }

    private static Optional<String> completeName(String input) {
        Pattern pattern = Pattern.compile("^.* /(.+)/ .*$");
        return match(input, pattern);
    }

    static Optional<String> parseSuffix(String input) {
        Pattern pattern = Pattern.compile(".*/+ (.*)$");
        return match(input, pattern);
    }

    private static Optional<String> match(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            return Optional.empty();
        }

        String result = matcher.group(1).trim();
        return result.isBlank() ? Optional.empty() : Optional.of(result);
    }
}
