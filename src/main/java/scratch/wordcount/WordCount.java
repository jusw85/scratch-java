package scratch.wordcount;

import org.ini4j.Wini;
import util.WiniUtil;

import java.io.*;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;

public class WordCount {

    /**
     * Location of configuration file
     */
    private static final String CONF_FILE = new File("./etc/wordcount", "config.ini").getAbsolutePath();

    /**
     * Location of log file
     */
    private static final String LOG_FILE;

    /**
     * Location of output directory
     */
    protected static final String OUT_DIRECTORY;

    /**
     * Location of document directory
     */
    protected static final String IN_DIRECTORY;

    /**
     * Name of consolidated word file in each folder
     */
    protected static final String OUT_FILE;

    /**
     * List of stop words retrieved from file
     */
    private static final String STOP_WORD_FILE;

    /**
     * Object required to read config file
     */
    private static Wini INIFILE;

    static {
        String inDir = null, outDir = null, outFile = null, stopWordFile = null, logFile = null;
        try {
            INIFILE = WiniUtil.createDefaultInstance(CONF_FILE);
        } catch (IOException e) {
        }

        if (INIFILE != null) {
            inDir = WiniUtil.optString(INIFILE, "config", "inDir");
            outDir = WiniUtil.optString(INIFILE, "config", "outDir");
            outFile = WiniUtil.optString(INIFILE, "config", "outFile");
            logFile = WiniUtil.optString(INIFILE, "config", "logFile");
            stopWordFile = WiniUtil.optString(INIFILE, "config", "stopFile");
            if (inDir.equals("") || outDir.equals("") || outFile.equals("")) {
                System.err.println("Error reading config file");
                System.exit(1);
            }
        }
        OUT_DIRECTORY = new String(outDir);
        IN_DIRECTORY = new String(inDir);
        STOP_WORD_FILE = new String(stopWordFile);
        OUT_FILE = new String(outFile);
        LOG_FILE = new String(logFile);
    }

    /**
     * Facilities for logging
     */
    static Logger logger;
    static String newline = System.getProperty("line.separator");

    static {
        logger = Logger.getLogger(WordCount.class.getName());
        logger.setLevel(Level.ALL);

        File logFile = new File(LOG_FILE);
        if (!"".equals(logFile.toString())) {
            try {
                if (!logFile.exists()) {
                    if (logFile.getParentFile() != null && !logFile.getParentFile().exists()) {
                        logFile.getParentFile().mkdirs();
                    }
                    logFile.createNewFile();
                }

                FileHandler fh = new FileHandler(LOG_FILE, false);
                fh.setFormatter(new Formatter() {
                    public String format(LogRecord record) {
                        return record.getMessage() + newline + newline;
                    }
                });
                logger.addHandler(fh);

                Logger rootLogger = Logger.getLogger("");
                Handler[] handlers = rootLogger.getHandlers();
                if (handlers[0] instanceof ConsoleHandler) {
                    rootLogger.removeHandler(handlers[0]);
                }
            } catch (IOException e) {
                System.err.println("Unable to create log file");
            }
        }
    }

    private static ArrayList stopWords;

    static {
        stopWords = new ArrayList();
        Scanner sc = null;
        try {
            sc = new Scanner(new File(STOP_WORD_FILE));
            while (sc.hasNext()) {
                String word = sc.next().toLowerCase();
                if (!stopWords.contains(word)) {
                    stopWords.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            logger.warning("Stopword file not found, ignoring...");
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    /**
     * Format and write a List of Map.Entry<String, Integer> values to file
     *
     * @param ls
     * @param filename
     */
    public static void writeList(List ls, File filename) {
        if (ls == null || filename == null) {
            return;
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filename));
            for (Iterator it = ls.iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                Object[] tmp = {entry.getKey(), entry.getValue()};
                out.write(String.format("%-20s\t\t%d\r\n", tmp));
            }
        } catch (IOException e) {
            logger.warning("Cannot write to file");
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                logger.warning("Error closing file: " + filename);
                return;
            }
        }
    }

    /**
     * Sort a HashMap based on value
     *
     * @param A HashMap
     * @return A sorted list of Map.Entry
     */
    public static List hashSort(HashMap hm) {
        if (hm == null) {
            return null;
        }
        List ls = new LinkedList(hm.entrySet());
        Collections.sort(ls, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) o1).getValue()).compareTo(((Map.Entry) o2).getValue());
            }
        });
        Collections.reverse(ls);
        return ls;
    }

    /**
     * Counts number of words in file
     *
     * @param Path to file
     * @return Hashmap of <String, Integer> of words in file
     */
    public static HashMap countWords(String filename) {
        if (filename == null) {
            return null;
        }

        /* Read input */
        String str = null;
        try {
            str = TextExtractor.getText(filename);
        } catch (Exception e) {
            logger.warning("Unable to parse file: " + filename + newline + "\t" + e.getMessage());
            return null;
        }
        if (str == null) {
            logger.warning("Invalid file format: " + filename);
            return null;
        }

        /* Count words */
        Scanner sc = new Scanner(str);
        HashMap hm = new HashMap();
        while (sc.hasNext()) {
            String word = sc.next().toLowerCase();
            if (!stopWords.contains(word)) {
                if (hm.containsKey(word)) {
                    hm.put(word, new Integer(((Integer) hm.get(word)).intValue() + 1));
                } else {
                    hm.put(word, new Integer(1));
                }
            }
        }
        return hm;
    }

    /**
     * Main
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
        File inDir = new File(WordCount.IN_DIRECTORY);

        boolean isSuccessful = new Object() {

            /**
             * Returns the relative path of a file from a root path
             * @param base
             * @param path
             * @return File representing the relative path of the file
             */
            private String getRelativePath(String base, String path) {
                return new File(base).toURI().relativize(new File(path).toURI()).getPath();
            }

            /**
             * Counts the number of words in a file and returns a HashMap
             * @param file
             * @return HashMap containing number of words in file
             */
            private HashMap processFile(File file) {
                if (!file.exists()) {
                    logger.warning("File not found: " + file.getAbsolutePath());
                    return null;
                }
                if (file.isDirectory()) {
                    processDirectory(file);
                }
                String filename = file.getAbsolutePath();
                int i = filename.lastIndexOf('.');
                if (!(i > 0 && i < filename.length() - 1)) {
                    return null;
                }
                String ext = filename.substring(i + 1).toLowerCase();
                if (!Arrays.asList(TextExtractor.SUPPORTED_FILE_FORMATS).contains(ext)) {
                    return null;
                }
                return WordCount.countWords(filename);
            }

            /**
             * Counts the number of words in each file in a directory and writes to an out directory
             * @param directory
             */
            public boolean processDirectory(File directory) {
                if (!directory.exists() || directory.isFile()) {
                    logger.warning("Directory not found: " + directory.getAbsolutePath());
                    return false;
                }
                File outDir = new File(WordCount.OUT_DIRECTORY, getRelativePath(IN_DIRECTORY,
                        directory.getAbsolutePath()));
                if (!outDir.exists() && !outDir.mkdirs()) {
                    logger.warning("Unable to create output folder");
                    return false;
                }

                HashMap allWords = new HashMap();
                for (Iterator it = Arrays.asList(directory.listFiles()).iterator(); it.hasNext(); ) {
                    File f = (File) it.next();
                    HashMap hm = null;
                    if (f.isDirectory()) {
                        processDirectory(f);
                    } else {
                        hm = processFile(f);
                    }
                    if (hm == null) {
                        continue;
                    }
                    List ls = WordCount.hashSort(hm);

                    WordCount.writeList(ls, new File(outDir, f.getName() + ".out"));

                    for (Iterator it2 = hm.entrySet().iterator(); it2.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) it2.next();
                        if (allWords.containsKey(entry.getKey())) {
                            int newCount = ((Integer) allWords.get(entry.getKey())).intValue()
                                    + ((Integer) entry.getValue()).intValue();
                            allWords.put(entry.getKey(), new Integer(newCount));
                        } else {
                            allWords.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                WordCount.writeList(WordCount.hashSort(allWords), new File(outDir,
                        WordCount.OUT_FILE));
                return true;
            }
        }.processDirectory(inDir);

        if (isSuccessful) {
            System.out.println("Done. Output written to " + WordCount.OUT_DIRECTORY);
        }

    }
}
