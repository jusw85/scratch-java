package util;

import org.ini4j.Config;
import org.ini4j.Wini;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WiniUtil {

    public static Wini createDefaultInstance(String file) throws IOException {
        return createDefaultInstance(new File(file));
    }

    public static Wini createDefaultInstance(File file) throws IOException {
        return createDefaultInstance(new FileInputStream(file));
    }

    public static Wini createDefaultInstance(InputStream inputStream) throws IOException {
        Wini ini = new Wini();
        ini.setConfig(getDefaultConfig());
        ini.load(inputStream);
        return ini;
    }

    private static Config getDefaultConfig() {
        Config cfg = Config.getGlobal().clone();
        cfg.setEscape(false);
        cfg.setEscapeNewline(false);
        cfg.setGlobalSection(true);
        cfg.setEmptyOption(true);
        cfg.setMultiOption(false);
        cfg.setPathSeparator(Wini.PATH_SEPARATOR);
        return cfg;
    }

    public static boolean getBoolean(Wini ini, String section, String option) throws Exception {
        String result = getString(ini, section, option);
        if (result.equalsIgnoreCase("true")) {
            return true;
        } else if (result.equalsIgnoreCase("false")) {
            return false;
        }
        throw new Exception("Ini[" + section + "," + option + "] is not a boolean");
    }

    public static boolean optBoolean(Wini ini, String section, String option) {
        return optBoolean(ini, section, option, false);
    }

    public static boolean optBoolean(Wini ini, String section, String option, boolean defaultValue) {
        try {
            return getBoolean(ini, section, option);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getString(Wini ini, String section, String option) throws Exception {
        String result = ini.get(section, option);
        if (result != null) {
            return result;
        }
        throw new Exception("Ini[" + section + "," + option + "] is not a string");
    }

    public static String optString(Wini ini, String section, String option) {
        return optString(ini, section, option, "");
    }

    public static String optString(Wini ini, String section, String option, String defaultValue) {
        try {
            return getString(ini, section, option);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static char getChar(Wini ini, String section, String option) throws Exception {
        String result = ini.get(section, option);
        if (result != null && result.length() == 1) {
            return result.charAt(0);
        }
        throw new Exception("Ini[" + section + "," + option + "] is not a character");
    }

    public static char optChar(Wini ini, String section, String option) {
        return optChar(ini, section, option, (char) 0);
    }

    public static char optChar(Wini ini, String section, String option, char defaultValue) {
        try {
            return getChar(ini, section, option);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInt(Wini ini, String section, String option) throws Exception {
        String result = getString(ini, section, option);
        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            throw new Exception("Ini[" + section + "," + option + "] is not an int");
        }
    }

    public static int optInt(Wini ini, String section, String option) {
        return optInt(ini, section, option, 0);
    }

    public static int optInt(Wini ini, String section, String option, int defaultValue) {
        try {
            return getInt(ini, section, option);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long getLong(Wini ini, String section, String option) throws Exception {
        String result = getString(ini, section, option);
        try {
            return Long.parseLong(result);
        } catch (NumberFormatException e) {
            throw new Exception("Ini[" + section + "," + option + "] is not a long");
        }
    }

    public static long optLong(Wini ini, String section, String option) {
        return optLong(ini, section, option, 0L);
    }

    public static long optLong(Wini ini, String section, String option, long defaultValue) {
        try {
            return getLong(ini, section, option);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float getFloat(Wini ini, String section, String option) throws Exception {
        String result = getString(ini, section, option);
        try {
            return Float.parseFloat(result);
        } catch (NumberFormatException e) {
            throw new Exception("Ini[" + section + "," + option + "] is not a float");
        }
    }

    public static float optFloat(Wini ini, String section, String option) {
        return optFloat(ini, section, option, 0.0f);
    }

    public static float optFloat(Wini ini, String section, String option, float defaultValue) {
        try {
            return getFloat(ini, section, option);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double getDouble(Wini ini, String section, String option) throws Exception {
        String result = getString(ini, section, option);
        try {
            return Double.parseDouble(result);
        } catch (NumberFormatException e) {
            throw new Exception("Ini[" + section + "," + option + "] is not a double");
        }
    }

    public static double optDouble(Wini ini, String section, String option) {
        return optDouble(ini, section, option, 0.0d);
    }

    public static double optDouble(Wini ini, String section, String option, double defaultValue) {
        try {
            return getDouble(ini, section, option);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
