package scratch;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

/**
 * Testing various methods for reading lines from a large file
 */
public class ReadLargeFile {

    public static void main(String[] args) throws IOException {
//        generateLargeRandomFile(new File("./largefile.txt"));

        File file = new File("./largefile.txt");
        long start = System.nanoTime();

//        int lineCount = readFileBufferedInputStreamArray(file);
//        int lineCount = readFileBufferedInputStreamChar(file);
        int lineCount = readFileBufferedReader(file);
//        int lineCount = readFileScanner(file);

        long end = System.nanoTime();
        System.out.println("lines=" + lineCount);
        System.out.println("time=" + (end - start) / 1000000000.0);
    }

    // time=20.112295836
    public static int readFileBufferedInputStreamArray(File inFile) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile));) {
            StringBuffer sb = new StringBuffer();
            String line;
            int lineCount = 0;
            byte[] buffer = new byte[1024];
            int bytesread;
            while ((bytesread = bis.read(buffer)) != -1) {
                for (int i = 0; i < bytesread; i++) {
                    if (buffer[i] == 13) {
                        lineCount++;
                        line = sb.toString();
                        sb.setLength(0);
                    } else {
                        sb.append(buffer[i]);
                    }
                }
            }
            return lineCount;
        }
    }

    // time=24.584751414
    public static int readFileBufferedInputStreamChar(File inFile) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile));) {
            StringBuffer sb = new StringBuffer();
            String line;
            int lineCount = 0;
            int c;
            while ((c = bis.read()) != -1) {
                if (c == 10) {
                    lineCount++;
                    line = sb.toString();
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
            return lineCount;
        }
    }

    // time=4.068943958
    public static int readFileBufferedReader(File inFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inFile));) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
            }
            return lineCount;
        }
    }

    // time=104.122032828
    public static int readFileScanner(File inFile) throws IOException {
        try (Scanner sc = new Scanner(inFile);) {
            String line;
            int lineCount = 0;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                lineCount++;
            }
            return lineCount;
        }
    }

    public static void generateLargeRandomFile(File outFile) throws IOException {
        Random random = new Random();
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(outFile, true));) {
            for (int i = 0; i < 400000; i++) {
                for (int j = 0; j < 1200; j++) {
                    bos.write(random.nextInt(94) + 33);
                }
                bos.write(13);
                bos.write(10);
            }
        }
    }
}
