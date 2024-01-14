package be.allersma.gedcom.sqlite;

import be.allersma.gedcom.migrator.FunctionMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folg.gedcom.model.*;
import org.folg.gedcom.parser.ModelParser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Gedcom gedcom = initialize().orElseGet(() -> {
            logger.error("Error in initialization. Aborting ...");
            System.exit(1);
            return null;
        });

        FunctionMarker.Branch<Gedcom> functionMarker = FunctionMarker.createMarkerTree(gedcom, true);
        logger.debug("Created Field marker");

        // People
        List<Object> peopleRaw = (List<Object>) functionMarker.invoke("getPeople").orElse(new ArrayList<>());
        List<FunctionMarker.Branch<Person>> people = peopleRaw
                .stream()
                .map(branch -> (FunctionMarker.Branch<Person>)branch)
                .collect(Collectors.toList());

        // Families
        List<Object> famliiesRaw = (List<Object>) functionMarker.invoke("getFamilies").orElse(new ArrayList<>());
        List<FunctionMarker.Branch<Family>> families = famliiesRaw
                .stream()
                .map(branch -> (FunctionMarker.Branch<Family>)branch)
                .collect(Collectors.toList());

        // Family events facts
        List<FunctionMarker.Branch<EventFact>> familyEventsFacts = families.stream()
                .flatMap(familyBranch -> familyBranch.invoke("getEventsFacts").stream())
                .map(object -> (ArrayList<FunctionMarker.Branch<EventFact>>) object)
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());


        // Marriages
        List<Marriage> marriages = new ArrayList<>();
        List<Family> familyValues = families.stream()
                .map(FunctionMarker.Branch::getValue)
                .collect(Collectors.toList());
        for (Family family : familyValues) {
            for (EventFact fact : family.getEventsFacts()) {
                Marriage.generateMarriage(gedcom, family, fact).ifPresent(marriages::add);
            }
        }
        families.forEach(family -> family.mark("getEventsFacts").mark("getNotes"));

        try (PrintWriter writer = new PrintWriter("/tmp/marriages.sql")) {
            String table = "marriages";
            String columns = " (partner1 INTEGER, partner2 INTEGER, children TEXT, date TEXT, place TEXT, notes TEXT);";
            writer.println("CREATE TABLE " + table + columns);
            marriages.forEach(marriage -> writer.println(marriage.toQuery(table)));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
        logger.info("Written marriages data to '/tmp/marriages.sql'.");

//        // Family events facts tagg
//        System.out.println("-------------------------------------");
//        System.out.println("Unique Family Events Facts tags:");
//        System.out.println();
//        familyEventsFacts.stream()
//                .map(fact -> fact.invoke("getTag") + " -- " + fact.invoke("getDisplayType"))
//                .distinct()
//                .forEach(System.out::println);

        if (!people.isEmpty()) {
            summarize(people);
        }
        if (!families.isEmpty()) {
            summarize(families);
        }
        summarize(Collections.singletonList(functionMarker));

        System.exit(0);
    }

    private static Optional<Gedcom> initialize() {
        try {
            Gedcom gedcom;
            InputStream file = Main.class.getClassLoader().getResourceAsStream("fokkens.ged");
            ModelParser parser = new ModelParser();
            gedcom = parser.parseGedcom(file);
            gedcom.createIndexes();
            gedcom.updateReferences();
            return Optional.of(gedcom);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static <T> void summarize(List<FunctionMarker.Branch<T>> branches) {
        if (branches.isEmpty())
            return;
        branches.forEach(branch -> branch
                .mark("toString")
                .mark("hashCode")
                .mark("getClass"));

        System.out.println("-------------------------------------");
        System.out.printf("At: %s%n", branches.get(0).getPath().isEmpty() ? "/" : branches.get(0).getPath());
        System.out.println("Functions that have not been called:");
        System.out.println();
        branches.stream()
                .flatMap(branch -> branch.getUnmarkedItems().stream())
                .distinct()
                .forEach(System.out::println);
    }
}