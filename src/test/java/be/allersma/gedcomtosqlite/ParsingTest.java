package be.allersma.gedcomtosqlite;

import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.parser.ModelParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ParsingTest {
    private static Gedcom gedcom;

    // TODO: Make dummy gedcom file in resources for testing
    @BeforeAll
    public static void initialize() throws SAXParseException, IOException {
        InputStream stream = ParsingTest.class.getClassLoader().getResourceAsStream("fokkens.ged");
        ModelParser parser = new ModelParser();
        gedcom = parser.parseGedcom(stream);
        assertNotNull(gedcom);
        gedcom.createIndexes();
        gedcom.updateReferences();
    }

    @Test
    public void gedcomLoaded() {
        assertNotNull(gedcom);
    }

    @Test
    public void doesAaltjeExistTest() {
        Person aaltje = gedcom.getPerson("I262");
        assertNotNull(aaltje, "Aaltje does not exist");
        assertEquals(1, aaltje.getNames().size());
        assertEquals("Aaltje /BULTENA/", aaltje.getNames().get(0).getValue());
    }

    @Test
    public void getNickName() {
        Person greet = gedcom.getPerson("I265");
        assertNotNull(greet);
        assertEquals(1, greet.getNames().size());
        assertEquals("Greet", greet.getNames().get(0).getNickname());
    }
}
