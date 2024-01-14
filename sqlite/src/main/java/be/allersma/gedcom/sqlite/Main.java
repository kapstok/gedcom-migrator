package be.allersma.gedcom.sqlite;

import be.allersma.gedcom.migrator.FunctionMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.parser.ModelParser;

import java.io.InputStream;
import java.util.ArrayList;
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

        List<Object> peopleRaw = (List<Object>) functionMarker.invoke("getPeople").orElse(new ArrayList<>());
        List<FunctionMarker.Branch<Person>> people = peopleRaw
                .stream()
                .map(branch -> (FunctionMarker.Branch<Person>)branch)
                .collect(Collectors.toList());

        if (!people.isEmpty()) {
            summarize(people.get(0));
        }
        summarize(functionMarker);

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

    private static void summarize(FunctionMarker.Branch<?> branch) {
        branch
                .mark("toString")
                .mark("hashCode")
                .mark("getClass");

        System.out.println("-------------------------------------");
        System.out.printf("At: %s%n", branch.getPath().isEmpty() ? "/" : branch.getPath());
        System.out.println("Functions that have not been called:");
        System.out.println();
        branch.getUnmarkedItems().forEach(System.out::println);
    }
}