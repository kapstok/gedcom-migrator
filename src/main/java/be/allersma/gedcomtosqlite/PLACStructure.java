package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;

public class PLACStructure extends Structure {
    private final String STARTING_NOTE;

    public PLACStructure(BufferedReader reader, char currentLevel, String startingNote) {
        super(reader, currentLevel);
        this.STARTING_NOTE = startingNote;
    }
}
