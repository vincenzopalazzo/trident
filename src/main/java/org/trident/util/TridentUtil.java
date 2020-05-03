package org.trident.util;

import java.util.Locale;

public class TridentUtil {

    private static final Class TAG = TridentUtil.class;

    public static boolean isOS(OS os){
        String osNow = System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);
        return osNow.contains(os.toString());
    }

    //Contains all methods
    //look inside the ConfigurationSingleton
    public static enum OS{
        LINUX("nux"),
        MAC("mac"),
        WINDOWS("win");

        private String type;

        OS(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
