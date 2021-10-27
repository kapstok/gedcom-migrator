package be.allersma.gedcomtosqlite;

public class LineCounter {
    private static long line = 0;

    public static void increment() {
        line++;
    }

    public static String getLineNumber() {
        return Long.toString(line);
    }
}
