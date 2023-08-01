package be.allersma.gedcom.sqlite;

import be.allersma.gedcom.migrator.FieldMarker;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.parser.ModelParser;

import java.io.InputStream;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        Gedcom gedcom = initialize().orElseGet(() -> {
            System.err.println("ERROR: Error in initialization. Aborting ...");
            System.exit(1);
            return null;
        });

        FieldMarker.Branch<Gedcom> fieldMarker = FieldMarker.createMarkerTree(gedcom, true);

        System.out.println("Field that have not been implemented:");
        System.out.println("-------------------------------------");
        fieldMarker.getUnmarkedItems().forEach(System.out::println);

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
}