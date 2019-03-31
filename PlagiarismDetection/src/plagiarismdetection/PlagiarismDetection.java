/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarismdetection;

//import edu.stanford.nlp.ling.WordTag;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.hwpf.HWPFDocument;
//import org.apache.poi.hwpf.extractor.WordExtractor;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFParagraph;
//import vn.hus.nlp.tagger.VietnameseMaxentTagger;
import vn.pipeline.*;

/**
 *
 * @author trinhhaison
 */
public class PlagiarismDetection {
    
    /**
     */
    
    public ArrayList<HashSet<String>> docSet;
    public ArrayList<HashSet<String>> docxSet;
    public ArrayList<HashSet<String>> textSet;
    public HashMap<String, Integer> wordMap; 
    public FileCollection fileCollect;
    
    public PlagiarismDetection(String pathToSource) {
        docSet = new ArrayList<>();
        docxSet = new ArrayList<>();
        textSet = new ArrayList<>();
        wordMap = new HashMap<>();
        fileCollect = new FileCollection(pathToSource);
    }
    
    public static List<Word> POS(String a) throws IOException{
        String[] annotators = {"wseg", "pos"};
        VnCoreNLP pipeline = new VnCoreNLP(annotators);
        Annotation annotation = new Annotation(a);
        pipeline.annotate(annotation); 
        return annotation.getWords();
    }
    
    public static String readPDFfile(String fileName) throws IOException{
        try (PDDocument document = PDDocument.load(new File(fileName))) {

            document.getClass();

            if (!document.isEncrypted()) {
			
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();

                String pdfFileInText = tStripper.getText(document);
//                System.out.println(pdfFileInText);
                return pdfFileInText;

            }

        }
        return "";
    }
    
    public static String readDocxFile(String fileName) throws FileNotFoundException, InvalidFormatException, IOException {
            FileInputStream fis = new FileInputStream(fileName);
            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
//            xdoc.getParagraphs().get(0).
            return extractor.getText();
    }
    
    public static String readDocFile(String fileName) throws FileNotFoundException, IOException, IOException{
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            HWPFDocument document = new HWPFDocument(fis);
            WordExtractor extractor = new WordExtractor(document);
//            xdoc.getParagraphs().get(0).
            return extractor.getText();
    }
    
    public static String getFileExtension(String fileName){
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException {
        String a = "xin chào, tôi là Sơn. Tôi sống ở thị xã Bắc Giang (trước là Hà Bắc)\n Tôi có một tình yêu: vật lý";
        List<Word> wordList = POS(a);
        String temp;
        for(Word word : wordList){
            temp = word.getForm().replaceAll("_", " ");
            System.out.println(temp + ": " + word.getPosTag());
        }
    }
}
