package scratch;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extract PDF files into a directory
 */
public class PDFExtractor extends DirectoryWalker<PDFExtractor.Result> {

    public static void main(String[] args) throws IOException {
        PDFExtractor.pdfToTxt(
                new File("indir"),
                new File("outdir"));
    }

    public static List<Result> pdfToTxt(File indir) throws IOException {
        List<Result> results = new ArrayList<Result>();
        new PDFExtractor().walk(indir, results);
        return results;
    }

    public static void pdfToTxt(File indir, File outdir) throws IOException {
        List<Result> results = pdfToTxt(indir);
        for (Result result : results) {
            String outfilename = FilenameUtils.removeExtension(result.file.getName()) + ".txt";
            File outfile = new File(outdir, outfilename);
            FileUtils.writeStringToFile(outfile, result.txt, "UTF-8", false);
        }
    }

    private PDFExtractor() {
        super();
    }

    protected void handleDirectoryStart(File file, int depth,
                                        Collection<Result> results) throws IOException {
        System.out.println("Parsing " + file.getAbsolutePath());
    }

    protected void handleFile(File file, int depth, Collection<Result> results) throws IOException {
        PDDocument doc = PDDocument.load(file);
//        PDDocument doc = PDDocument.load(file, "password");

//        if (doc.isEncrypted()) {
//            try {
//                doc.decrypt("");
//                doc.setAllSecurityToBeRemoved(true);
//            } catch (CryptographyException | InvalidPasswordException e) {
//                e.printStackTrace();
//            }
//        }
        String txt = new PDFTextStripper().getText(doc);
        doc.close();
        Result result = new Result(file, txt);
        results.add(result);
    }

    public static class Result {
        public File file;
        public String txt;

        public Result(File file, String txt) {
            this.file = file;
            this.txt = txt;
        }
    }
}