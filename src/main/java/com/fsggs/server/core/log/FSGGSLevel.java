package com.fsggs.server.core.log;

import org.apache.log4j.Level;

public class FSGGSLevel extends Level {

    private static final int FSGGS_INT = WARN_INT + 50;

    public static final Level FSGGS = new FSGGSLevel(FSGGS_INT, "FSGGS", 7);

    private FSGGSLevel(int arg0, String arg1, int arg2) {
        super(arg0, arg1, arg2);

    }

    public static Level toLevel(String sArg) {
        if (sArg != null && sArg.toUpperCase().equals("FSGGS")) {
            return FSGGS;
        }
        return (Level) toLevel(sArg, Level.DEBUG);
    }

    public static Level toLevel(int val) {
        if (val == FSGGS_INT) {
            return FSGGS;
        }
        return (Level) toLevel(val, Level.DEBUG);
    }

    public static Level toLevel(int val, Level defaultLevel) {
        if (val == FSGGS_INT) {
            return FSGGS;
        }
        return Level.toLevel(val, defaultLevel);
    }

    public static Level toLevel(String sArg, Level defaultLevel) {
        if (sArg != null && sArg.toUpperCase().equals("FSGGS")) {
            return FSGGS;
        }
        return Level.toLevel(sArg, defaultLevel);
    }
}
