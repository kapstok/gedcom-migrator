package be.allersma.gedcom.migrator.utils;

import org.folg.gedcom.model.Name;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FormattedNameUtilTest {
    private Name name;

    @BeforeEach
    public void setUp() {
        name = new Name();
    }

    @Test
    public void parseGivenNameOnlyTest() {
        name.setValue("William Lee");

        Optional<String> givenResult = FormattedNameUtil.parseGivenNames(name.getValue());
        assertTrue(givenResult.isPresent());
        assertEquals("William Lee", givenResult.get());

        Optional<String> surnamesResult = FormattedNameUtil.parseSurnames(name.getValue());
        assertTrue(surnamesResult.isEmpty());

        Optional<String> suffixResult = FormattedNameUtil.parseSuffix(name.getValue());
        assertTrue(suffixResult.isEmpty());

        FormattedNameUtil.parseNameValue(name);
        assertEquals("William Lee", name.getGiven());
        assertNull(name.getSurname());
        assertNull(name.getSuffix());
    }


    @Test
    public void parseSurnameOnlyTest() {
        name.setValue("/Parry/");

        Optional<String> givenNamesResult = FormattedNameUtil.parseGivenNames(name.getValue());
        assertTrue(givenNamesResult.isEmpty());

        Optional<String> surnamesResult = FormattedNameUtil.parseSurnames(name.getValue());
        assertTrue(surnamesResult.isPresent());
        assertEquals("Parry", surnamesResult.get());

        Optional<String> suffixResult = FormattedNameUtil.parseSuffix(name.getValue());
        assertTrue(suffixResult.isEmpty());

        FormattedNameUtil.parseNameValue(name);
        assertNull(name.getGiven());
        assertNull(name.getSuffix());
        assertEquals("Parry", name.getSurname());
    }

    @Test
    public void parseNameTest() {
        name.setValue("William Lee /Parry/");

        Optional<String> givenNamesResult = FormattedNameUtil.parseGivenNames(name.getValue());
        assertTrue(givenNamesResult.isPresent());
        assertEquals("William Lee", givenNamesResult.get());

        Optional<String> surnamesResult = FormattedNameUtil.parseSurnames(name.getValue());
        assertTrue(surnamesResult.isPresent());
        assertEquals("Parry", surnamesResult.get());

        Optional<String> suffixResult = FormattedNameUtil.parseSuffix(name.getValue());
        assertFalse(suffixResult.isPresent());

        FormattedNameUtil.parseNameValue(name);
        assertEquals("William Lee", name.getGiven());
        assertEquals("Parry", name.getSurname());
        assertNull(name.getSuffix());
    }

    @Test
    public void parseSurnameWithSuffixTest() {
        name.setValue("/Parry/ Jr.");

        Optional<String> givenNamesResult = FormattedNameUtil.parseGivenNames(name.getValue());
        assertFalse(givenNamesResult.isPresent());

        Optional<String> surnamesResult = FormattedNameUtil.parseSurnames(name.getValue());
        assertTrue(surnamesResult.isPresent());
        assertEquals("Parry", surnamesResult.get());

        Optional<String> suffixResult = FormattedNameUtil.parseSuffix(name.getValue());
        assertTrue(suffixResult.isPresent());
        assertEquals("Jr.", suffixResult.get());

        FormattedNameUtil.parseNameValue(name);
        assertNull(name.getGiven());
        assertEquals("Parry", name.getSurname());
        assertEquals("Jr.", name.getSuffix());
    }

    @Test
    public void parseNameWithSuffixTest() {
        name.setValue("William Lee /Parry/ Jr.");

        Optional<String> givenNamesResult = FormattedNameUtil.parseGivenNames(name.getValue());
        assertTrue(givenNamesResult.isPresent());
        assertEquals("William Lee", givenNamesResult.get());

        Optional<String> surnamesResult = FormattedNameUtil.parseSurnames(name.getValue());
        assertTrue(surnamesResult.isPresent());
        assertEquals("Parry", surnamesResult.get());

        Optional<String> suffixResult = FormattedNameUtil.parseSuffix(name.getValue());
        assertTrue(suffixResult.isPresent());
        assertEquals("Jr.", suffixResult.get());

        FormattedNameUtil.parseNameValue(name);
        assertEquals("William Lee", name.getGiven());
        assertEquals("Parry", name.getSurname());
        assertEquals("Jr.", name.getSuffix());
    }

    /**
     * Possible formats according to the Gedcom 5.5.1 specification:
     * <cite>
     * NAME_PERSONAL:=
     * [
     * {NAME_TEXT} |
     * /{NAME_TEXT}/ |
     * {NAME_TEXT} /{NAME_TEXT}/ |
     * /{NAME_TEXT}/ {NAME_TEXT} |
     * {NAME_TEXT} /{NAME_TEXT}/ {NAME_TEXT}
     * ]
     * </cite>
     * So if there are no whitespaces used, you have to see it as the first case
     * <cite>{NAME_TEXT}</cite>
     */
    @Test
    public void parseNameSurnameSuffixNoWhitespace() {
        name.setValue("William Lee/Parry/Jr.");

        Optional<String> givenNamesResult = FormattedNameUtil.parseGivenNames(name.getValue());
        assertTrue(givenNamesResult.isPresent());
        assertEquals("William Lee/Parry/Jr.", givenNamesResult.get());

        Optional<String> surnamesResult = FormattedNameUtil.parseSurnames(name.getValue());
        assertTrue(surnamesResult.isEmpty());

        Optional<String> suffixResult = FormattedNameUtil.parseSuffix(name.getValue());
        assertTrue(suffixResult.isEmpty());

        FormattedNameUtil.parseNameValue(name);
        assertEquals("William Lee/Parry/Jr.", name.getGiven());
        assertNull(name.getSurname());
        assertNull(name.getSuffix());
    }

    @Test
    @Disabled
    public void parseNameWithPrefixTest() {
        name.setValue("Lt. Cmndr. Joseph /Allen/ jr.");
        assert false;
    }

    @Test
    @Disabled
    public void parseNameWithSurnamePrefixTest() {
        name.setValue("de, la Cruz");
        assert false;
    }

    @Test
    public void expressionWithSideEffectsTest() {
        name.setValue("William Lee /Parry/ Jr.");

        Name returnValue = FormattedNameUtil.parseNameValue(name);
        assertEquals(name.getValue(), returnValue.getValue());
        assertEquals(name.getGiven(), returnValue.getGiven());
        assertEquals(name.getSurname(), returnValue.getSurname());
    }

    /**
     * Edge cases that have been found by debugging.
     */
    @Test
    public void edgeCasesTest() {
        name.setValue("Attie ( Antie /PIJTERS/");

        Optional<String> givenNamesResult = FormattedNameUtil.parseGivenNames(name.getValue());
        assertTrue(givenNamesResult.isPresent());
        assertEquals("Attie ( Antie", givenNamesResult.get());

        Optional<String> surnamesResult = FormattedNameUtil.parseSurnames(name.getValue());
        assertTrue(surnamesResult.isPresent());
        assertEquals("PIJTERS", surnamesResult.get());

        FormattedNameUtil.parseNameValue(name);
        assertEquals("Attie ( Antie", name.getGiven());
        assertEquals("PIJTERS", name.getSurname());
    }
}