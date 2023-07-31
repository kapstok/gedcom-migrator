package be.allersma.gedcomtosqlite;

import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.parser.ModelParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FieldMarkerTest {
    private FieldMarker.Branch<Gedcom> fieldMarker;

    private static Gedcom gedcom;

    @BeforeAll
    public static void initializeAll() throws SAXParseException, IOException {
        InputStream stream = ParsingTest.class.getClassLoader().getResourceAsStream("dummy.ged");
        ModelParser parser = new ModelParser();
        gedcom = parser.parseGedcom(stream);
        assertNotNull(gedcom);
        gedcom.createIndexes();
        gedcom.updateReferences();
    }

    @BeforeEach
    public void initializeEach() {
        fieldMarker = FieldMarker.createMarkerTree(gedcom);
    }

    @Test
    public void gedcomLoaded() {
        assertNotNull(gedcom);
    }

    @Test
    public void showUnmarkedItems() {
        List<String> originalLeaves = fieldMarker.getUnmarkedItems();
        Optional<Object> people = fieldMarker.invoke("getPeople");
        List<String> unmarkedLeaves = fieldMarker.getUnmarkedItems();

        assertEquals(originalLeaves.size() - 1, unmarkedLeaves.size());
    }

    @Test
    public void basicBranchTest() {
        Optional<Object> people = fieldMarker.invoke("getPeople");
        assertTrue(people.isPresent());
        assertEquals(2, ((List)people.get()).size());
    }

    @Test
    public void recursionTest() {
        Optional<Object> people = fieldMarker.invoke("getPeople");
        assertTrue(people.isPresent());
        assertInstanceOf(List.class, people.get());
        assertInstanceOf(FieldMarker.Branch.class, ((List<FieldMarker.Branch<Person>>)people.get()).get(0));
        assertInstanceOf(Person.class, ((FieldMarker.Branch<Person>)((List<?>) people.get()).get(0)).value);

        List<FieldMarker.Branch<Note>> note = (List<FieldMarker.Branch<Note>>)((List<FieldMarker.Branch>)people.get()).get(0).invoke("getNotes").get();
        assertInstanceOf(List.class, note);
        assertEquals(1, note.size());
        assertInstanceOf(Note.class, note.get(0).value);

        Optional<Object> noteValue = note.get(0).invoke("getValue");
        assertTrue(noteValue.isPresent());
        assertInstanceOf(String.class, noteValue.get());
        assertFalse(((String)noteValue.get()).isBlank());
    }
}