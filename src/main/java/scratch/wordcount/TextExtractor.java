package scratch.wordcount;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class TextExtractor {

    public static final String[] SUPPORTED_FILE_FORMATS = {"doc", "ppt", "xls", "pdf", "txt",
            "odt", "odp", "ods", "docx", "pptx", "xlsx"};

    /**
     * @param filename
     * @return Text extracted from file, null on error or unsupported file format
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JDOMException
     * @throws XmlException
     * @throws OpenXML4JException
     */
    public static String getText(String filename) throws FileNotFoundException, IOException,
            JDOMException, OpenXML4JException, XmlException {
        File file = new File(filename);
        if (file.isDirectory()) {
            return null;
        }

        int i = filename.lastIndexOf('.');
        if (!(i > 0 && i < filename.length() - 1)) {
            return null;
        }

        String ext = filename.substring(i + 1).toLowerCase();
        if (ext.equals("doc")) {
            return getTextDoc(file);
        } else if (ext.equals("ppt")) {
            return getTextPpt(file);
        } else if (ext.equals("xls")) {
            return getTextXls(file);
        } else if (ext.equals("docx")) {
            return getTextDocx(file);
        } else if (ext.equals("pptx")) {
            return getTextPptx(file);
        } else if (ext.equals("xlsx")) {
            return getTextXlsx(file);
        } else if (ext.equals("txt")) {
            return getTextTxt(file);
        } else if (ext.equals("pdf")) {
            return getTextPdf(file);
        } else if (ext.equals("odt") || ext.equals("odp") || ext.equals("ods")) {
            return getTextOO(file);
        }
        return null;
    }

    private static String getTextDoc(File file) throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(file);
        WordExtractor ex = new WordExtractor(new POIFSFileSystem(in));
        return ex.getText();
    }

    private static String getTextPpt(File file) throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(file);
        PowerPointExtractor ex = new PowerPointExtractor(new POIFSFileSystem(in));
        return ex.getText();
    }

    private static String getTextXls(File file) throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(file);
        ExcelExtractor ex = new ExcelExtractor(new POIFSFileSystem(in));
        return ex.getText();
    }

    private static String getTextTxt(File file) throws IOException {
        Scanner sc = new Scanner(file);
        StringBuilder sb = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
            sb.append(ls);
        }
        return sb.toString();
    }

    private static String getTextPdf(File file) throws IOException {
        PDFTextStripper pd = new PDFTextStripper();
        PDDocument pdoc = PDDocument.load(file);
        String str = pd.getText(pdoc);
        pdoc.close();
        return str;
    }

    private static String getTextOO(File file) throws ZipException, IOException, JDOMException {
        StringBuffer sb = new StringBuffer();
        ZipFile zipFile = new ZipFile(file);
        Enumeration entries = zipFile.entries();
        ZipEntry entry;

        while (entries.hasMoreElements()) {
            entry = (ZipEntry) entries.nextElement();
            if (entry.getName().equals("content.xml")) {
                SAXBuilder sax = new SAXBuilder();
                Document doc = sax.build(zipFile.getInputStream(entry));
                Element rootElement = doc.getRootElement();
                processElement(rootElement, sb);
                break;
            }
        }
        return sb.toString();
    }

    private static void processElement(Object o, StringBuffer sb) {
        if (o instanceof Element) {
            Element e = (Element) o;
            String elementName = e.getQualifiedName();

            if (elementName.startsWith("text")) {
                if (elementName.equals("text:tab")) {
                    sb.append("\t");
                } else if (elementName.equals("text:s")) {
                    sb.append(" ");
                } else {
                    List children = e.getContent();
                    Iterator it = children.iterator();
                    while (it.hasNext()) {
                        Object child = it.next();
                        if (child instanceof Text) {
                            Text t = (Text) child;
                            sb.append(t.getValue());
                        } else {
                            processElement(child, sb);
                        }
                    }
                }
                if (elementName.equals("text:p")) {
                    sb.append("\n");
                }
            } else {
                List nonTextList = e.getContent();
                Iterator it = nonTextList.iterator();
                while (it.hasNext()) {
                    Object nonTextChild = it.next();
                    processElement(nonTextChild, sb);
                }
            }
        }
    }

    private static String getTextDocx(File file) throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(file);
        XWPFWordExtractor ex = new XWPFWordExtractor(new XWPFDocument(in));
        return ex.getText();
    }

    private static String getTextPptx(File file) throws FileNotFoundException, IOException,
            OpenXML4JException, XmlException {
        XSLFPowerPointExtractor ex = new XSLFPowerPointExtractor(new XSLFSlideShow(file.toString()));
        return ex.getText();
    }

    private static String getTextXlsx(File file) throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(file);
        XSSFExcelExtractor ex = new XSSFExcelExtractor(new XSSFWorkbook(in));
        return ex.getText();
    }

}
