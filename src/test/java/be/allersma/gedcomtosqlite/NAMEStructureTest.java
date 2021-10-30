package be.allersma.gedcomtosqlite;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NAMEStructureTest {
    @Test
    public void parseGivenNameOnlyTest() {
        String subject = "William Lee";

        Optional<String> givenNamesResult = NAMEStructure.parseGivenNames(subject);
        assertTrue(givenNamesResult.isPresent());
        assertEquals("William Lee", givenNamesResult.get());

        Optional<String> surnamesResult = NAMEStructure.parseSurnames(subject);
        assertFalse(surnamesResult.isPresent());

        Optional<String> suffixResult = NAMEStructure.parseSuffix(subject);
        assertFalse(suffixResult.isPresent());
    }


    @Test
    public void parseSurnameOnlyTest() {
        String subject = "/Parry/";

        Optional<String> givenNamesResult = NAMEStructure.parseGivenNames(subject);
        assertFalse(givenNamesResult.isPresent());

        Optional<String> surnamesResult = NAMEStructure.parseSurnames(subject);
        assertTrue(surnamesResult.isPresent());
        assertEquals("Parry", surnamesResult.get());

        Optional<String> suffixResult = NAMEStructure.parseSuffix(subject);
        assertFalse(suffixResult.isPresent());
    }

    @Test
    public void parseNameTest() {
        String subject = "William Lee /Parry/";

        Optional<String> givenNamesResult = NAMEStructure.parseGivenNames(subject);
        assertTrue(givenNamesResult.isPresent());
        assertEquals("William Lee", givenNamesResult.get());

        Optional<String> surnamesResult = NAMEStructure.parseSurnames(subject);
        assertTrue(surnamesResult.isPresent());
        assertEquals("Parry", surnamesResult.get());

        Optional<String> suffixResult = NAMEStructure.parseSuffix(subject);
        assertFalse(suffixResult.isPresent());
    }

    @Test
    public void parseSurnameWithPrefixTest() {
        String subject = "/Parry/ Jr.";

        Optional<String> givenNamesResult = NAMEStructure.parseGivenNames(subject);
        assertFalse(givenNamesResult.isPresent());

        Optional<String> surnamesResult = NAMEStructure.parseSurnames(subject);
        assertTrue(surnamesResult.isPresent());
        assertEquals("Parry", surnamesResult.get());

        Optional<String> suffixResult = NAMEStructure.parseSuffix(subject);
        assertTrue(suffixResult.isPresent());
        assertEquals("Jr.", suffixResult.get());
    }

    @Test
    public void parseNameWithPrefixTest() {
        String subject = "William Lee /Parry/ Jr.";

        Optional<String> givenNamesResult = NAMEStructure.parseGivenNames(subject);
        assertTrue(givenNamesResult.isPresent());
        assertEquals("William Lee", givenNamesResult.get());

        Optional<String> surnamesResult = NAMEStructure.parseSurnames(subject);
        assertTrue(surnamesResult.isPresent());
        assertEquals("Parry", surnamesResult.get());

        Optional<String> suffixResult = NAMEStructure.parseSuffix(subject);
        assertTrue(suffixResult.isPresent());
        assertEquals("Jr.", suffixResult.get());
    }

    /**
     * Edge cases that have been found by debugging.
     */
    @Test
    public void edgeCasesTest() {
        String subject = "Attie ( Antie /PIJTERS/";

        Optional<String> givenNamesResult = NAMEStructure.parseGivenNames(subject);
        assertTrue(givenNamesResult.isPresent());
        assertEquals("Attie ( Antie", givenNamesResult.get());

        Optional<String> surnamesResult = NAMEStructure.parseSurnames(subject);
        assertTrue(surnamesResult.isPresent());
        assertEquals("PIJTERS", surnamesResult.get());
    }
}