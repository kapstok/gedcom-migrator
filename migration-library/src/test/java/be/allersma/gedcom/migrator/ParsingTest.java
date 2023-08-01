package be.allersma.gedcom.migrator;

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

    @BeforeAll
    public static void initialize() throws SAXParseException, IOException {
        InputStream stream = ParsingTest.class.getClassLoader().getResourceAsStream("dummy.ged");
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
        Person ross = gedcom.getPerson("I262");
        assertNotNull(ross, "Ross does not exist");
        assertEquals(1, ross.getNames().size());
        assertEquals("Ross /Werner/", ross.getNames().get(0).getValue());
    }

    @Test
    public void getNickName() {
        Person peterPan = gedcom.getPerson("I265");
        assertNotNull(peterPan);
        assertEquals(1, peterPan.getNames().size());
        assertEquals("Peter Pan", peterPan.getNames().get(0).getNickname());
    }
}
