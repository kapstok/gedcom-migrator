package be.allersma.gedcom.sqlite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Family;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Marriage {
    private final int partner1;
    private final int partner2;
    private final List<Integer> children;
    private final String date;
    private final String place;
    private final String notes;

    private static final Logger logger = LogManager.getLogger(Marriage.class);
    private static final Pattern idPattern = Pattern.compile("^I[0-9]+");

    public Marriage(int partner1, int partner2, List<Integer> children, String date, String place, String notes) {
        this.partner1 = partner1;
        this.partner2 = partner2;
        this.children = children;
        this.date = date;
        this.place = place;
        this.notes = notes;
    }

    public static Optional<Marriage> generateMarriage(Gedcom gedcom, Family family, EventFact marriage) {
        if (marriage.getTag().equals("MARR")) {
            int partnerIndex = 0;
            int[] partners = new int[2];
            String date = marriage.getDate() == null ? "" : marriage.getDate();
            String place = "";
            String notes;
            List<Integer> children;

            if (marriage.getNoteRefs().isEmpty()) {
                notes = "";
            } else {
                notes = marriage.getNoteRefs()
                        .stream()
                        .map(note -> note + " ")
                        .collect(Collectors.joining());
            }

            if (marriage.getPlace() != null) {
                place += marriage.getPlace();
            }
            if (marriage.getAddress() != null) {
                place += place.isEmpty() ? marriage.getAddress() : " " + marriage.getAddress();
            }

            for (Person husband : family.getHusbands(gedcom)) {
                if (partnerIndex > 1) {
                    logger.error("Marriage with more than two persons found. What to do?");
                    return Optional.empty();
                } else {
                    Optional<Integer> id = indiIdToInt(husband.getId());
                    if (id.isPresent()) {
                        partners[partnerIndex++] = id.get();
                    }
                }
            }

            for (Person wife : family.getWives(gedcom)) {
                if (partnerIndex > 1) {
                    logger.error("Marriage with more than two persons found. What to do?");
                    return Optional.empty();
                } else {
                    Optional<Integer> id = indiIdToInt(wife.getId());
                    if (id.isPresent()) {
                        partners[partnerIndex++] = id.get();
                    }
                }
            }

            children = family.getChildren(gedcom)
                    .stream()
                    .map(Person::getId)
                    .map(Marriage::indiIdToInt)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            while (partnerIndex < 2) {
                partners[partnerIndex++] = -1;
            }

            return Optional.of(new Marriage(partners[0], partners[1], children, date, place, notes));
        } else {
            logger.error("Unknown tag '{}' found", marriage.getTag());
            return Optional.empty();
        }
    }

    public String toQuery(String table) {
        String separator = " ";
        String childrenRaw = this.children.stream().map(childId -> childId + separator).collect(Collectors.joining());
        String children = childrenRaw.length() >= separator.length() ? childrenRaw.substring(0, childrenRaw.length() - separator.length()) : childrenRaw;

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ").append(table).append(" (partner1, partner2, children, date, place, notes)\n");
        query.append("VALUES (")
                .append(partner1).append(", ")
                .append(partner2).append(", '")
                .append(children).append("', '")
                .append(date).append("', '")
                .append(place).append("', '")
                .append(notes).append("');");

        return query.toString();
    }

    private static Optional<Integer> indiIdToInt(String id) {
        Matcher matcher = idPattern.matcher(id);
        if (matcher.find()) {
            return Optional.of(Integer.parseInt(id.substring(1)));
        } else {
            logger.error("Invalid INDI id found: " + id);
            return Optional.empty();
        }
    }
}
