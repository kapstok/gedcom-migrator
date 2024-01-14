package be.allersma.gedcom.migrator;

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

class FunctionMarkerTest {
    private FunctionMarker.Branch<Gedcom> functionMarker;

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
        functionMarker = FunctionMarker.createMarkerTree(gedcom);
    }

    @Test
    public void gedcomLoaded() {
        assertNotNull(gedcom);
    }

    @Test
    public void showUnmarkedItems() {
        List<String> originalLeaves = functionMarker.getUnmarkedItems();
        functionMarker.mark("getPeople");
        List<String> unmarkedLeaves = functionMarker.getUnmarkedItems();

        assertEquals(originalLeaves.size() - 1, unmarkedLeaves.size());
    }

    @Test
    public void basicBranchTest() {
        Optional<Object> people = functionMarker.invoke("getPeople");
        assertTrue(people.isPresent());
        assertEquals(2, ((List)people.get()).size());
    }

    @Test
    public void recursionTest() {
        Optional<Object> people = functionMarker.invoke("getPeople");
        assertTrue(people.isPresent());
        assertInstanceOf(List.class, people.get());
        assertInstanceOf(FunctionMarker.Branch.class, ((List<FunctionMarker.Branch<Person>>)people.get()).get(0));
        assertInstanceOf(Person.class, ((FunctionMarker.Branch<Person>)((List<?>) people.get()).get(0)).value);

        List<FunctionMarker.Branch<Note>> note = (List<FunctionMarker.Branch<Note>>)((List<FunctionMarker.Branch>)people.get()).get(0).invoke("getNotes").get();
        assertInstanceOf(List.class, note);
        assertEquals(1, note.size());
        assertInstanceOf(Note.class, note.get(0).value);

        Optional<Object> noteValue = note.get(0).invoke("getValue");
        assertTrue(noteValue.isPresent());
        assertInstanceOf(String.class, noteValue.get());
        assertFalse(((String)noteValue.get()).isBlank());
    }

    @Test
    public void ignoreNullFieldsTest() {
        FunctionMarker.Branch<Gedcom> functionMarkerNonNull = FunctionMarker.createMarkerTree(gedcom, true);
        List<String> orginalNonNullFields = functionMarkerNonNull.getUnmarkedItems();

        functionMarkerNonNull.mark("getSources");
        assertEquals(orginalNonNullFields.size(), functionMarkerNonNull.getUnmarkedItems().size());

        FunctionMarker.Branch<Person> rossNull = ((List<FunctionMarker.Branch<Person>>) functionMarker.invoke("getPeople").get()).get(0);
        FunctionMarker.Branch<Person> rossNonNull = ((List<FunctionMarker.Branch<Person>>)functionMarkerNonNull.invoke("getPeople").get()).get(0);
        assertEquals(85, rossNull.getUnmarkedItems().size());
        assertEquals(8, rossNonNull.getUnmarkedItems().size());
    }

    @Test
    public void pathTest() {
        assertTrue(functionMarker.getPath().isEmpty());
        FunctionMarker.Branch<Person> ross = ((List<FunctionMarker.Branch<Person>>) functionMarker.invoke("getPeople").get()).get(0);
        assertEquals("/getPeople", ross.getPath());
    }

    @Test
    public void markAllNoHashTest() {
        List<String> originalItems = functionMarker.getUnmarkedItems();
        functionMarker.markAll();
        List<String> unmarkedItems = functionMarker.getUnmarkedItems();
        assertFalse(originalItems.isEmpty());
        assertTrue(unmarkedItems.isEmpty());
    }

    @Test
    public void markAllRightHashTest() {
        List<String> originalItems = functionMarker.getUnmarkedItems();
        functionMarker.markAll(functionMarker.getMarkAllHash());
        List<String> unmarkedItems = functionMarker.getUnmarkedItems();
        assertFalse(originalItems.isEmpty());
        assertTrue(unmarkedItems.isEmpty());
    }

    @Test
    public void markAllWrongHashTest() {
        List<String> originalItems = functionMarker.getUnmarkedItems();
        functionMarker.markAll("BAD-HASH");
        List<String> unmarkedItems = functionMarker.getUnmarkedItems();
        assertFalse(originalItems.isEmpty());
        assertEquals(originalItems.size(), unmarkedItems.size());
    }
}