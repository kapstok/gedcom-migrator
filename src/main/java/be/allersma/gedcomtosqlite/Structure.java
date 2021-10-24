package be.allersma.gedcomtosqlite;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Base for all Structures. A structure is analogous to the structures as defined in the
 * GEDCOM specification.
 */
public abstract class Structure {
    protected final BufferedReader READER;
    protected final char CURRENT_LEVEL;

    public Structure(BufferedReader reader, char currentLevel) {
        this.CURRENT_LEVEL = currentLevel;
        this.READER = reader;
    }

    protected boolean isNewStructure() {
        try {
            READER.mark(1);
            char level = (char) READER.read();
            READER.reset();
            return level > CURRENT_LEVEL;
        } catch (IOException e) {
            e.getMessage();
        }

        return false;
    }
}
