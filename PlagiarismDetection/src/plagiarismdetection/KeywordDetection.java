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
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import static plagiarismdetection.AbstractDetection.getAbstractPath;
import static plagiarismdetection.PlagiarismDetection.readDocFile;
import static plagiarismdetection.PlagiarismDetection.readDocxFile;
import static plagiarismdetection.PlagiarismDetection.readPDFfile;
import vn.pipeline.*;

/**
 * <h3>Lớp trích rút từ khóa KeywordDetection:</h3><br>
 * <h3>Các lớp con:</h3><br>
 *  <b>WordType</b>: gồm hai thuộc tính là word (từ) và type (loại từ)<br>
 *  <b>WordInfo</b>: gồm hai thuộc tính là word (từ) và tf_idf (chỉ số tf-idf) của từ<br>
 *  <b>PhraseInfo</b>: gồm hai thuộc tính là phrase (cụm từ) và tf_idfScore (chỉ số tf-idf) của cụm từ<br>
 * 
 * <h3>Các thuộc tính:</h3><br>
 *  <b>wordList (List{@literal <}WordType{@literal >})</b>: Danh sách các đối tượng thuộc lớp WordType theo đúng thứ tự xuất hiện trong toàn bộ văn bản<br>
 *  <b>chunkSize (int)</b>: chiều dài của một đoạn (tính theo số từ)<br>
 *  <b>documentKeywordNumber (int)</b>: số lượng từ khóa của toàn văn bản<br>
 *  <b>topChunkRequirement (int)</b>: số lượng từ khóa của toàn văn bản cần có trong một đoạn để đoạn đó được xem xét xử lý<br>
 *  <b>documentWordsLocation(Map{@literal <}String, List{@literal <}Integer{@literal >}{@literal >})</b>: là một cấu trúc Map có key là một từ xuất hiện trong văn bản, value là danh sách vị trí của từ đó trong văn bản<br>
 *  <b>chunkWordLocationList (List{@literal <}Map{@literal <}String, List{@literal <}Integer{@literal >}{@literal >}{@literal >})</b>: Danh sách các cấu trúc Map như thuộc tính documentWordsLocation, nhưng mỗi cấu trúc Map chỉ tương ứng với một đoạn<br>
 *  <b>file (File)</b>: file chứa văn bản<br>
 *  <b>wordIDFs (Map{@literal <}String, Double{@literal >})</b>: Cấu trúc Map có key là từ trong tập từ vựng, value là chỉ số idf của từ đó<br>
 *  <b>stopWords (Set{@literal <}String{@literal >})</b>: tập hợp các stop word của tiếng Việt<br>
 *  <b>stopWords2 (Set{@literal <}String{@literal >})</b>: tập hợp các stop word của lĩnh vực<br>
 * 
 * @author trinhhaison
 */
public class KeywordDetection {
    
    private List<WordType> wordList;
    private int chunkSize;
    private int documentKeywordNumber;
    private int chunkKeywordNumber;
    private int topChunkRequirement;
    private Map<String, List<Integer>> documentWordsLocation;
    private List<Map<String, List<Integer>>> chunkWordLocationList;
    private File file;
    public static File IDFdataSource;
    public static Map<String, Double> wordIDFs;
    public static File stopWordsFile;
    public static Set<String> stopWords;
    public static Set<String> stopWords2;

    public List<WordType> getWordList() {
        return wordList;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getDocumentKeywordNumber() {
        return documentKeywordNumber;
    }

    public int getChunkKeywordNumber() {
        return chunkKeywordNumber;
    }

    public int getTopChunkRequirement() {
        return topChunkRequirement;
    }

    public Map<String, List<Integer>> getDocumentWordsLocation() {
        return documentWordsLocation;
    }

    public List<Map<String, List<Integer>>> getChunkWordLocationList() {
        return chunkWordLocationList;
    }

    public File getFile() {
        return file;
    }
    
    class WordType{
        String word;
        String type;

        public WordType(String word, String type) {
            this.word = word;
            this.type = type;
        }

        public String getWord() {
            return word;
        }

        public String getType() {
            return type;
        }
    }
    
    class WordInfor{
        String word;
        double tf_idf;

        public WordInfor(String word, double tf_idf) {
            this.word = word;
            this.tf_idf = tf_idf;
        }

        public String getWord() {
            return word;
        }

        public double getTf_idf() {
            return tf_idf;
        }
    }
    
    public class PhraseInfo{
        String phrase;
        double tf_idfScore;

        public PhraseInfo(String phrase, double tf_idfScore) {
            this.phrase = phrase;
            this.tf_idfScore = tf_idfScore;
        }

        public String getPhrase() {
            return phrase;
        }

        public double getTf_idfScore() {
            return tf_idfScore;
        }
    }
    
    /**
     *<b>Phương thức khởi tạo</b>: Khởi tạo một đối tượng thuộc lớp KeywordDetection với các tham số của constructor như sau: String đường_dẫn_đến_file_đồ_án(doc, docx, pdf), int kích_thước_(số_từ)_của_một_chunk,  int số_từ_khóa_lấy_trong_toàn_văn_bản, int số_lượng_từ_khóa_lấy_ra_từ_mỗi_chunk, int số_lượng_từ_khóa_cần_thiết_của_một_chunk_có_trong_các_từ_khóa_của_toàn_văn_bản<br>
    */
    
    public KeywordDetection(String filePath, int chunkSize, int documentKeyWordNumber, int chunkKeyWordNumber, int topChunkRequirement) throws FileNotFoundException, IOException, ClassNotFoundException, InvalidFormatException {
        wordList = new ArrayList<>();
        documentWordsLocation = new HashMap<>();
        chunkWordLocationList = new ArrayList<>();
        this.chunkSize = chunkSize;
        this.chunkKeywordNumber = chunkKeyWordNumber;
        this.documentKeywordNumber = documentKeyWordNumber;
        this.topChunkRequirement = topChunkRequirement;
        
        file = new File(filePath); 
        
        String extension = PlagiarismDetection.getFileExtension(filePath).toLowerCase();
        String text;
        int is_pdf = 0;
        if(extension.equals("doc")){
            text = readDocFile(filePath);
        }
        else if(extension.equals("docx")){
            text = readDocxFile(filePath);
        }
        else if(extension.equals("pdf")){
            text = readPDFfile(filePath);
            is_pdf = 1;
        }
        else{
            text = "";
        }
        AbstractDetection ad = new AbstractDetection();
        String abs = ad.getAbstract(filePath, text, is_pdf);

        String[] paragraphs = abs.split("\n");
        
        int wordCount = 0;
        List<Integer> temp;
        List<Word> words;
        String wordTemp;
        
        for(String sCurrentLine : paragraphs){
            words = PlagiarismDetection.POS(sCurrentLine);
            for(Word word: words){
                wordTemp = word.getForm().replaceAll("_", " ");
                wordTemp = wordTemp.toLowerCase();
                wordList.add(new WordType(wordTemp, word.getPosTag()));
                if(documentWordsLocation.containsKey(wordTemp)){
                    temp = documentWordsLocation.get(wordTemp);
                    temp.add(wordCount);
                }
                else{
                    temp = new ArrayList<>();
                    temp.add(wordCount);
                    documentWordsLocation.put(wordTemp, temp);
                }
                
                wordCount++;
            }
            wordList.add(new WordType(".", "CH"));
            wordCount++;
        }
        int chunkNumber = wordCount / chunkSize;
        
        Map<String, List<Integer>> chunkWordLocations;
        String tempString;
        int tempCount;
        
        for (int i = 0; i < chunkNumber; i++){
            chunkWordLocations = new HashMap<>();
            
            for(int j = 0; j < chunkSize; j++){
                
                tempCount = i * chunkSize + j;
                tempString = wordList.get(tempCount).getWord();
                
                if (chunkWordLocations.containsKey(tempString)){
                    temp = chunkWordLocations.get(tempString);
                    temp.add(tempCount);
                }
                else{
                    temp = new ArrayList<>();
                    temp.add(tempCount);
                    chunkWordLocations.put(tempString, temp);
                }
            }
            chunkWordLocationList.add(chunkWordLocations);
            
        }
        if((wordCount % chunkSize) > 0){
            if(chunkNumber == 0){
                chunkWordLocationList.add(new HashMap<>());
                chunkWordLocations = chunkWordLocationList.get(0);
            }
            else{
                chunkWordLocations = chunkWordLocationList.get(chunkNumber - 1);
            }
            
            for(int j = chunkNumber * chunkSize; j < wordCount; j++){
                
                tempString = wordList.get(j).getWord();
                if (chunkWordLocations.containsKey(tempString)){
                    temp = chunkWordLocations.get(tempString);
                    temp.add(j);
                }
                else{
                    temp = new ArrayList<>();
                    temp.add(j);
                    chunkWordLocations.put(tempString, temp);
                }
            }
        }  
    }
    
    /**
     * <h3>Tham số</h3>
     * <b>corpus</b>: là một cấu trúc Map có key là từ, value là danh sách vị trí xuất hiện từ đó
     * 
     * <h3>Giá trị trả về</h3>
     * một cấu trúc Map có key là từ, value là chỉ số tf-idf của từ đó
    */
    public Map<String, Double> getWordTF_IDFMaps(Map<String, List<Integer>> corpus){
        Map<String, Double> result = new HashMap<>();
        
        for(Map.Entry<String, List<Integer>> entry : corpus.entrySet()){
            if(wordIDFs.containsKey(entry.getKey())){
                result.put(entry.getKey(), entry.getValue().size() * wordIDFs.get(entry.getKey()));
            } 
        }
        
        return result;
    }
    
    
    /**
     * <h3>Tham số</h3>
     * <b>corpus</b>: là một cấu trúc Map có key là từ, value là danh sách vị trí xuất hiện từ đó
     * 
     * <h3>Giá trị trả về</h3>
     * một danh sách các đối tượng thuộc lớp WordInfor, sắp xếp theo chỉ số tf-idf của từ giảm dần
    */
    public List<WordInfor> getWordTF_IDFs(Map<String, List<Integer>> corpus) throws FileNotFoundException, IOException, ClassNotFoundException{
        ArrayList<WordInfor> wordInforList = new ArrayList<>();
        
        for(Map.Entry<String, List<Integer>> entry : corpus.entrySet()){
            if(wordIDFs.containsKey(entry.getKey())){
                wordInforList.add(new WordInfor(entry.getKey(), entry.getValue().size() * wordIDFs.get(entry.getKey())));
            }
            
        }
        
        Collections.sort(wordInforList, new Comparator<WordInfor>() {
            @Override
            public int compare(WordInfor wordInfor1, WordInfor wordInfor2)
            {

                if(wordInfor1.getTf_idf() > wordInfor2.getTf_idf()){
                    return -1;
                }
                else if(wordInfor1.getTf_idf() == wordInfor2.getTf_idf()){
                    return 0;
                }
                return 1;
            }
        });
        
        return wordInforList;
    }
    
    
    /**
     * <h3>Giá trị trả về</h3>
     * một cấu trúc Map có key là từ khóa của văn bản, value là một danh sách các vị trí của từ khóa đó trong văn bản
    */
    public Map<String, List<Integer>> getDocumentKeyWords() throws IOException, FileNotFoundException, ClassNotFoundException{
        Map<String, List<Integer>> result = new HashMap<>();
        
        List<WordInfor> wordInforList = getWordTF_IDFs(documentWordsLocation);
        int wordInforListSize = wordInforList.size();
        
        int j = 0;
        int i = 0;
        String temp;
        
        while(j < documentKeywordNumber){
            if(i == wordInforListSize) break;
            
            temp = wordInforList.get(i).getWord();
            if(!(stopWords.contains(temp)) && !(stopWords2.contains(temp))){
                j++;
                result.put(temp, documentWordsLocation.get(temp));
            }
            
            i++;
        }        

        return result;
    }
    
    /**
     *<h3>Giá trị trả về</h3>
     * Một danh sách các chunk được chọn để tiếp tục tìm từ khóa, mỗi chunk được biểu diễn bởi một cấu trúc Map, có key là từ trong chunk, value là danh sách vị trí của từ đó trong chunk
     */
    public List<Map<String, List<Integer>>> getTopChunks() throws IOException, FileNotFoundException, ClassNotFoundException{
        List<Map<String, List<Integer>>> topChunks = new ArrayList<>();
        Map<String, List<Integer>> documentKeyWords = getDocumentKeyWords();
        
        int tempCount = 0;
        for(Map<String, List<Integer>> chunk : chunkWordLocationList){
            
            tempCount = 0;
            
            for(Map.Entry<String, List<Integer>> entry : chunk.entrySet()){
                if(documentKeyWords.containsKey(entry.getKey())){
                    
                    tempCount += entry.getValue().size();
                    if(tempCount == topChunkRequirement){
                        topChunks.add(chunk);
                        break;
                    }
                }
            }
        }
        
        return topChunks;
    }
    
    /**
     * <h3>Tham số</h3>
     * <b>chunk</b>: là một cấu trúc Map biểu diễn chunk có key là từ trong chunk, value là danh sách vị trí xuất hiện từ đó trong chunk
     * 
     * <h3>Giá trị trả về</h3>
     * Một cấu trúc Map có key là các từ khóa của chunk, value là danh sách xuất hiện các từ khóa đó trong chunk
    */
    public Map<String, List<Integer>> getChunkKeyWords(Map<String, List<Integer>> chunk) throws IOException, FileNotFoundException, ClassNotFoundException{
        Map<String, List<Integer>> result = new HashMap<>();
        
        List<WordInfor> wordInforList = getWordTF_IDFs(chunk);
        int wordInforListSize = wordInforList.size();
        
        int j = 0;
        int i = 0;
        String temp;
        
        while(j < chunkKeywordNumber){
            if(i == wordInforListSize) break;
            
            temp = wordInforList.get(i).getWord();
            if(!(stopWords.contains(temp)) && !(stopWords2.contains(temp))){
                j++;
                result.put(temp, chunk.get(temp));
            }
            
            i++;
        }  
        
        return result;
    }
    
    /**
     * Ý nghĩa: trả về một cụm từ của một từ bằng cách kết hợp từ đó với các từ xung quanh nó nếu có thể
     * <h3>Tham số</h3>
     * <b>chunkTF_IDFs</b>: là một cấu trúc Map biểu diễn chunk có key là từ trong chunk, value là danh sách vị trí xuất hiện từ đó trong chunk
     * <b>position</b>: là vị trí của từ
     * 
     * <h3>Giá trị trả về</h3>
     * Một đối tượng thuộc lớp PhraseInfo của cụm từ
    */
    public PhraseInfo keyPraseDetect(int position, Map<String, Double> chunkTF_IDFs) throws FileNotFoundException, IOException, ClassNotFoundException{
        StringBuffer result = new StringBuffer(wordList.get(position).getWord());
        
        String wordType = wordList.get(position).getType();
        int listSize = wordList.size();
        
        double tf_idfScore = chunkTF_IDFs.get(wordList.get(position).getWord());
        double phraseTf_idfScore = tf_idfScore;
        
        // trigram
        if(position <= (listSize - 3)){
            if((wordType.equals("N") ||
                wordType.equals("Nb") ||
                wordType.equals("Ny") ||
                wordType.equals("V") ||
                wordType.equals("Vb") ||
                wordType.equals("Np"))
                &&
               (wordList.get(position + 2).getType().equals("N") ||
                wordList.get(position + 2).getType().equals("Nb") ||
                wordList.get(position + 2).getType().equals("Ny") ||
                wordList.get(position + 2).getType().equals("V") ||
                wordList.get(position + 2).getType().equals("Vb") ||
                wordList.get(position + 2).getType().equals("A") ||
                wordList.get(position + 2).getType().equals("Np"))
                &&
                (!wordList.get(position + 1).getType().equals("CH"))
                &&
                (wordList.get(position + 2).getWord().indexOf(' ') != -1)
                &&
                (wordList.get(position + 1).getWord().indexOf(' ') != -1)){
                
                if(!(stopWords.contains(wordList.get(position + 2).getWord())) && !(stopWords2.contains(wordList.get(position + 2).getWord()))){
                    if(chunkTF_IDFs.containsKey(wordList.get(position + 1).getWord())){
                        phraseTf_idfScore += chunkTF_IDFs.get(wordList.get(position + 1).getWord());
                    }

                    if(chunkTF_IDFs.containsKey(wordList.get(position + 2).getWord())){
                        phraseTf_idfScore += chunkTF_IDFs.get(wordList.get(position + 2).getWord());
                    }
                    
                    if((phraseTf_idfScore / 3) >= (tf_idfScore / 2)){
                        result.append(" ").append(wordList.get(position + 1).getWord()).append(" ").append(wordList.get(position + 2).getWord());
                        return (new PhraseInfo(result.toString(), phraseTf_idfScore));
                    }
                    else{
                        result = new StringBuffer(wordList.get(position).getWord());
                        phraseTf_idfScore = tf_idfScore;
                    }
                }
            }
        }
        
        if(position >= 2){
            if((wordList.get(position - 2).getType().equals("N") ||
                wordList.get(position - 2).getType().equals("Nb") ||
                wordList.get(position - 2).getType().equals("Ny") ||
                wordList.get(position - 2).getType().equals("V") ||
                wordList.get(position - 2).getType().equals("Vb") ||
                wordList.get(position - 2).getType().equals("Np"))
                &&
               (wordType.equals("N") ||
                wordType.equals("Nb") ||
                wordType.equals("Ny") ||
                wordType.equals("V") ||
                wordType.equals("Vb") ||
                wordType.equals("A") ||
                wordType.equals("Np"))
                &&
                (!wordList.get(position - 1).getType().equals("CH"))
                &&
                (wordList.get(position - 2).getWord().indexOf(' ') != -1)
                &&
                (wordList.get(position - 1).getWord().indexOf(' ') != -1)){
                
                if(!(stopWords.contains(wordList.get(position - 2).getWord())) && !(stopWords2.contains(wordList.get(position - 2).getWord()))){
                    if(chunkTF_IDFs.containsKey(wordList.get(position - 1).getWord())){
                        phraseTf_idfScore += chunkTF_IDFs.get(wordList.get(position - 1).getWord());
                    }

                    if(chunkTF_IDFs.containsKey(wordList.get(position - 2).getWord())){
                        phraseTf_idfScore += chunkTF_IDFs.get(wordList.get(position - 2).getWord());
                    }
                    
                    if((phraseTf_idfScore / 3) >= (tf_idfScore / 2)){
                        result.insert(0, wordList.get(position - 2).getWord() + " " + wordList.get(position - 1).getWord() + " ");
                        
                        return (new PhraseInfo(result.toString(), phraseTf_idfScore));
                    }
                    else{
                        result = new StringBuffer(wordList.get(position).getWord());
                        phraseTf_idfScore = tf_idfScore;
                    }
                }  
            }
        }
        
        if((position >= 1) && (position <= (listSize - 2))){
            if((wordList.get(position - 1).getType().equals("N") ||
                wordList.get(position - 1).getType().equals("Nb") ||
                wordList.get(position - 1).getType().equals("Ny") ||
                wordList.get(position - 1).getType().equals("V") ||
                wordList.get(position - 1).getType().equals("Vb") ||
                wordList.get(position - 1).getType().equals("Np"))
                &&
               (wordList.get(position + 1).getType().equals("N") ||
                wordList.get(position + 1).getType().equals("Nb") ||
                wordList.get(position + 1).getType().equals("Ny") ||
                wordList.get(position + 1).getType().equals("V") ||
                wordList.get(position + 1).getType().equals("Vb") ||
                wordList.get(position + 1).getType().equals("A") ||
                wordList.get(position + 1).getType().equals("Np"))
                &&
                (wordList.get(position + 1).getWord().indexOf(' ') != -1)
                &&
                (wordList.get(position - 1).getWord().indexOf(' ') != -1)){
            
                if((!(stopWords.contains(wordList.get(position - 1).getWord())))
                    &&
                    (!(stopWords.contains(wordList.get(position + 1).getWord())))
                    &&
                    (!(stopWords2.contains(wordList.get(position - 1).getWord())))
                    &&
                    (!(stopWords2.contains(wordList.get(position + 1).getWord())))){
                    
                    if(chunkTF_IDFs.containsKey(wordList.get(position - 1).getWord())){
                        phraseTf_idfScore += chunkTF_IDFs.get(wordList.get(position - 1).getWord());
                    }

                    if(chunkTF_IDFs.containsKey(wordList.get(position + 1).getWord())){
                        phraseTf_idfScore += chunkTF_IDFs.get(wordList.get(position + 1).getWord());
                    }
                    
                    if((phraseTf_idfScore / 3) >= (tf_idfScore / 2)){
                        
                        result.append(" ").append(wordList.get(position + 1).getWord());
                        
                        result.insert(0, wordList.get(position - 1).getWord() + " ");
                        
                        return (new PhraseInfo(result.toString(), phraseTf_idfScore));
                    }
                    else{
                        result = new StringBuffer(wordList.get(position).getWord());
                        phraseTf_idfScore = tf_idfScore;
                    }
                }
            }
        }
        
        //bigram
        if(position <= (listSize - 2)){
            if((wordType.equals("N") ||
                wordType.equals("Nb") ||
                wordType.equals("Ny") ||
                wordType.equals("V") ||
                wordType.equals("Vb") ||
                wordType.equals("Np"))
                &&
               (wordList.get(position + 1).getType().equals("N") ||
                wordList.get(position + 1).getType().equals("Nb") ||
                wordList.get(position + 1).getType().equals("Ny") ||
                wordList.get(position + 1).getType().equals("V") ||
                wordList.get(position + 1).getType().equals("Vb") ||
                wordList.get(position + 1).getType().equals("A") ||
                wordList.get(position + 1).getType().equals("Np"))
                &&
                (wordList.get(position + 1).getWord().indexOf(' ') != -1)){
                
                if(!(stopWords.contains(wordList.get(position + 1).getWord())) && !(stopWords2.contains(wordList.get(position + 1).getWord()))){
                    if(chunkTF_IDFs.containsKey(wordList.get(position + 1).getWord())){
                        phraseTf_idfScore += chunkTF_IDFs.get(wordList.get(position + 1).getWord());
                    }
                    
                    if((phraseTf_idfScore / 2) >= (2 * tf_idfScore / 3)){
                        result.append(" ").append(wordList.get(position + 1).getWord());
                        return (new PhraseInfo(result.toString(), phraseTf_idfScore));
                    }
                    else{
                        result = new StringBuffer(wordList.get(position).getWord());
                        phraseTf_idfScore = tf_idfScore;
                    }
                }  
            }
        }
        
        if(position >= 1){
            if((wordList.get(position - 1).getType().equals("N") ||
                wordList.get(position - 1).getType().equals("Nb") ||
                wordList.get(position - 1).getType().equals("Ny") ||
                wordList.get(position - 1).getType().equals("V") ||
                wordList.get(position - 1).getType().equals("Vb") ||
                wordList.get(position - 1).getType().equals("Np"))
                &&
               (wordType.equals("N") ||
                wordType.equals("V") ||
                wordType.equals("A") ||
                wordType.equals("Np"))
                &&
                (wordList.get(position - 1).getWord().indexOf(' ') != -1)){
                
                if(!(stopWords.contains(wordList.get(position - 1).getWord())) && !(stopWords2.contains(wordList.get(position - 1).getWord()))){
                    if(chunkTF_IDFs.containsKey(wordList.get(position - 1).getWord())){
                        phraseTf_idfScore += chunkTF_IDFs.get(wordList.get(position - 1).getWord());
                    }
                    
                    if((phraseTf_idfScore / 2) >= (2 * tf_idfScore / 3)){
                        result.insert(0, wordList.get(position - 1).getWord() + " ");
                        return (new PhraseInfo(result.toString(), phraseTf_idfScore));
                    }
                    else{
                        result = new StringBuffer(wordList.get(position).getWord());
                        phraseTf_idfScore = tf_idfScore;
                    }
                }
            }
        }
        
        return (new PhraseInfo(result.toString(), phraseTf_idfScore));
    }  
    
    /**
     * Ý nghĩa: trả về các cụm từ khóa của văn bản bằng cách kết hợp các từ khóa của văn bản đó với các từ xung quanh nó nếu có thể (sự kết hợp một từ khóa với một (hay nhiều) từ cạnh nó chỉ xảy ra khi chúng luôn đi cùng nhau trong văn bản)
     * 
     * <h3>Giá trị trả về</h3>
     * Một danh sách các đối tượng thuộc lớp PhraseInfo của các cụm từ
    */
    public List<PhraseInfo> documentKeyPhraseDetect() throws FileNotFoundException, IOException, ClassNotFoundException{
        
        Map<String, List<Integer>> keyWords = getDocumentKeyWords();
        StringBuffer keyword;
        List<Integer> locations;
        int firstLocation;
        int lastLocation;
        String wordType;
        int listSize = wordList.size();
        String tempWord1;
        String tempWord2;
        boolean isPhrase;
        Map<String, Double> phraseMap = new HashMap<>();
        Map<String, Double> wordTf_IdfMap = getWordTF_IDFMaps(documentWordsLocation);
        double tempTf_Idf;
        String tempKeyPhrase;
        
        for(Map.Entry<String, List<Integer>> entry : keyWords.entrySet()){
            isPhrase = true;
            keyword = new StringBuffer(entry.getKey());
            locations = entry.getValue();
            firstLocation = locations.get(0);
            lastLocation = locations.get(locations.size() - 1);
            wordType = wordList.get(firstLocation).getType();
            tempTf_Idf = wordTf_IdfMap.get(entry.getKey());
            
            //trigram
            if(lastLocation <= (listSize - 3)){
                tempWord1 = wordList.get(firstLocation + 1).getWord();
                tempWord2 = wordList.get(firstLocation + 2).getWord();
            if((wordType.equals("N") ||
                wordType.equals("Nb") ||
                wordType.equals("Ny") ||
                wordType.equals("V") ||
                wordType.equals("Vb") ||
                wordType.equals("Np"))
                &&
               (wordList.get(firstLocation + 2).getType().equals("N") ||
                wordList.get(firstLocation + 2).getType().equals("Nb") ||
                wordList.get(firstLocation + 2).getType().equals("Ny") ||
                wordList.get(firstLocation + 2).getType().equals("V") ||
                wordList.get(firstLocation + 2).getType().equals("Vb") ||
                wordList.get(firstLocation + 2).getType().equals("A") ||
                tempWord2.equals("Np"))
                &&
                (!wordList.get(firstLocation + 1).getType().equals("CH"))
                &&
                (tempWord2.indexOf(' ') != -1)
                &&
                (tempWord1.indexOf(' ') != -1)){
                    if(!(stopWords.contains(tempWord2))){
                        for(int location : locations){
                            if(!(wordList.get(location + 1).getWord().equals(tempWord1) && wordList.get(location + 2).getWord().equals(tempWord2))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.append(" ").append(tempWord1).append(" ").append(tempWord2);
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }
                        isPhrase = true;
                    }
                }
            }
            
            if(firstLocation >= 2){
                tempWord1 = wordList.get(firstLocation - 1).getWord();
                tempWord2 = wordList.get(firstLocation - 2).getWord();
                if((wordList.get(firstLocation - 2).getType().equals("N") ||
                    wordList.get(firstLocation - 2).getType().equals("Nb") ||
                    wordList.get(firstLocation - 2).getType().equals("Ny") ||
                    wordList.get(firstLocation - 2).getType().equals("V") ||
                    wordList.get(firstLocation - 2).getType().equals("Vb") ||
                    wordList.get(firstLocation - 2).getType().equals("Np"))
                    &&
                   (wordType.equals("N") ||
                    wordType.equals("Nb") ||
                    wordType.equals("Ny") ||
                    wordType.equals("V") ||
                    wordType.equals("Vb") ||
                    wordType.equals("A") ||
                    wordType.equals("Np"))
                    &&
                    (!wordList.get(firstLocation - 1).getType().equals("CH"))
                    &&
                    (tempWord2.indexOf(' ') != -1)
                    &&
                    (tempWord1.indexOf(' ') != -1)){

                    if(!(stopWords.contains(tempWord2))){
                        for(int location : locations){
                            if(!(wordList.get(location - 1).getWord().equals(tempWord1) && wordList.get(location - 2).getWord().equals(tempWord2))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.insert(0, tempWord2 + " " + tempWord1 + " ");
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }
                        isPhrase = true;
                    }
                }
            }
            
            if((firstLocation >= 1) && (lastLocation <= (listSize - 2))){
                    tempWord1 = wordList.get(firstLocation - 1).getWord();
                    tempWord2 = wordList.get(firstLocation + 1).getWord();
                if((wordList.get(firstLocation - 1).getType().equals("N") ||
                    wordList.get(firstLocation - 1).getType().equals("Nb") ||
                    wordList.get(firstLocation - 1).getType().equals("Ny") ||
                    wordList.get(firstLocation - 1).getType().equals("V") ||
                    wordList.get(firstLocation - 1).getType().equals("Vb") ||
                    wordList.get(firstLocation - 1).getType().equals("Np"))
                    &&
                   (wordList.get(firstLocation + 1).getType().equals("N") ||
                    wordList.get(firstLocation + 1).getType().equals("Nb") ||
                    wordList.get(firstLocation + 1).getType().equals("Ny") ||
                    wordList.get(firstLocation + 1).getType().equals("V") ||
                    wordList.get(firstLocation + 1).getType().equals("Vb") ||
                    wordList.get(firstLocation + 1).getType().equals("A") ||
                    wordList.get(firstLocation + 1).getType().equals("Np"))
                    &&
                    (tempWord2.indexOf(' ') != -1)
                    &&
                    (tempWord1.indexOf(' ') != -1)){

                    if((!(stopWords.contains(tempWord1)))
                        &&
                        (!(stopWords.contains(tempWord2)))
                        ){

                        for(int location : locations){
                            if(!(wordList.get(location - 1).getWord().equals(tempWord1) && wordList.get(location + 1).getWord().equals(tempWord2))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.append(" ").append(tempWord2);
                            keyword.insert(0, tempWord1 + " ");
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }
                        isPhrase = true;
                    }
                }
            }
            
            //bigram
            if(lastLocation <= (listSize - 2)){
                tempWord1 = wordList.get(firstLocation + 1).getWord();
                if((wordType.equals("N") ||
                    wordType.equals("Nb") ||
                    wordType.equals("Ny") ||
                    wordType.equals("V") ||
                    wordType.equals("Vb") ||
                    wordType.equals("Np"))
                    &&
                   (wordList.get(firstLocation + 1).getType().equals("N") ||
                    wordList.get(firstLocation + 1).getType().equals("Nb") ||
                    wordList.get(firstLocation + 1).getType().equals("Ny") ||
                    wordList.get(firstLocation + 1).getType().equals("V") ||
                    wordList.get(firstLocation + 1).getType().equals("Vb") ||
                    wordList.get(firstLocation + 1).getType().equals("A") ||
                    wordList.get(firstLocation + 1).getType().equals("Np"))
                    &&
                    (tempWord1.indexOf(' ') != -1)){

                    if(!(stopWords.contains(tempWord1))){
                        for(int location : locations){
                            if(!(wordList.get(location + 1).getWord().equals(tempWord1))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.append(" ").append(tempWord1);
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }
                        isPhrase = true;
                    }  
                }
            }
            
            if(firstLocation >= 1){
                tempWord1 = wordList.get(firstLocation - 1).getWord();
                if((wordList.get(firstLocation - 1).getType().equals("N") ||
                    wordList.get(firstLocation - 1).getType().equals("Nb") ||
                    wordList.get(firstLocation - 1).getType().equals("Ny") ||
                    wordList.get(firstLocation - 1).getType().equals("V") ||
                    wordList.get(firstLocation - 1).getType().equals("Vb") ||
                    wordList.get(firstLocation - 1).getType().equals("Np"))
                    &&
                   (wordType.equals("N") ||
                    wordType.equals("V") ||
                    wordType.equals("A") ||
                    wordType.equals("Np"))
                    &&
                    (tempWord1.indexOf(' ') != -1)){

                    if(!(stopWords.contains(tempWord1))){
                        for(int location : locations){
                            if(!(wordList.get(location - 1).getWord().equals(tempWord1))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.insert(0, tempWord1 + " ");
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }

                    }
                }
            }
            phraseMap.put(entry.getKey(), tempTf_Idf);
        }
        
        List<PhraseInfo> result = new ArrayList<>();
        for(Map.Entry<String, Double> entry : phraseMap.entrySet()){
            result.add(new PhraseInfo(entry.getKey(), entry.getValue()));
        }   
        sortPhraseInfoList(result);
        
        return result;
    }
    
    /**
     * Ý nghĩa: trả về các cụm từ khóa của văn bản được lấy từ các chunk bằng cách kết hợp các từ khóa của văn bản đó với các từ xung quanh nó nếu có thể (sự kết hợp một từ khóa với một (hay nhiều) từ cạnh nó chỉ xảy ra khi chúng luôn đi cùng nhau trong văn bản)
     * 
     * <h3>Giá trị trả về</h3>
     * Một danh sách các đối tượng thuộc lớp PhraseInfo của các cụm từ
    */
    public List<PhraseInfo> documentKeyPhraseDetectFromChunks() throws FileNotFoundException, IOException, ClassNotFoundException{
        List<Map<String, List<Integer>>> topChunks = getTopChunks();
        Set<String> keyWordSet = new HashSet<>();
        Map<String, List<Integer>> tempList;
        
        for(Map<String, List<Integer>> chunk : topChunks){
            tempList = getChunkKeyWords(chunk);
            for(Map.Entry<String, List<Integer>> entry : tempList.entrySet()){
                keyWordSet.add(entry.getKey());
            }
        }
        
//        Map<String, List<Integer>> keyWords = getDocumentKeyWords();
        StringBuffer keyword;
        List<Integer> locations;
        int firstLocation;
        int lastLocation;
        String wordType;
        int listSize = wordList.size();
        String tempWord1;
        String tempWord2;
        boolean isPhrase;
        Map<String, Double> phraseMap = new HashMap<>();
        Map<String, Double> wordTf_IdfMap = getWordTF_IDFMaps(documentWordsLocation);
        double tempTf_Idf;
        String tempKeyPhrase;
        
        for(String keyWord : keyWordSet){
            isPhrase = true;
            keyword = new StringBuffer(keyWord);
            locations = documentWordsLocation.get(keyWord);
            firstLocation = locations.get(0);
            lastLocation = locations.get(locations.size() - 1);
            wordType = wordList.get(firstLocation).getType();
            tempTf_Idf = wordTf_IdfMap.get(keyWord);
            
            //trigram
            if(lastLocation <= (listSize - 3)){
                tempWord1 = wordList.get(firstLocation + 1).getWord();
                tempWord2 = wordList.get(firstLocation + 2).getWord();
            if((wordType.equals("N") ||
                wordType.equals("Nb") ||
                wordType.equals("Ny") ||
                wordType.equals("V") ||
                wordType.equals("Vb") ||
                wordType.equals("Np"))
                &&
               (wordList.get(firstLocation + 2).getType().equals("N") ||
                wordList.get(firstLocation + 2).getType().equals("Nb") ||
                wordList.get(firstLocation + 2).getType().equals("Ny") ||
                wordList.get(firstLocation + 2).getType().equals("V") ||
                wordList.get(firstLocation + 2).getType().equals("Vb") ||
                wordList.get(firstLocation + 2).getType().equals("A") ||
                tempWord2.equals("Np"))
                &&
                (!wordList.get(firstLocation + 1).getType().equals("CH"))
                &&
                (tempWord2.indexOf(' ') != -1)
                &&
                (tempWord1.indexOf(' ') != -1)){
                    if(!(stopWords.contains(tempWord2))){
                        for(int location : locations){
                            if(!(wordList.get(location + 1).getWord().equals(tempWord1) && wordList.get(location + 2).getWord().equals(tempWord2))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.append(" ").append(tempWord1).append(" ").append(tempWord2);
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }
                        isPhrase = true;
                    }
                }
            }
            
            if(firstLocation >= 2){
                tempWord1 = wordList.get(firstLocation - 1).getWord();
                tempWord2 = wordList.get(firstLocation - 2).getWord();
                if((wordList.get(firstLocation - 2).getType().equals("N") ||
                    wordList.get(firstLocation - 2).getType().equals("Nb") ||
                    wordList.get(firstLocation - 2).getType().equals("Ny") ||
                    wordList.get(firstLocation - 2).getType().equals("V") ||
                    wordList.get(firstLocation - 2).getType().equals("Vb") ||
                    wordList.get(firstLocation - 2).getType().equals("Np"))
                    &&
                   (wordType.equals("N") ||
                    wordType.equals("Nb") ||
                    wordType.equals("Ny") ||
                    wordType.equals("V") ||
                    wordType.equals("Vb") ||
                    wordType.equals("A") ||
                    wordType.equals("Np"))
                    &&
                    (!wordList.get(firstLocation - 1).getType().equals("CH"))
                    &&
                    (tempWord2.indexOf(' ') != -1)
                    &&
                    (tempWord1.indexOf(' ') != -1)){

                    if(!(stopWords.contains(tempWord2))){
                        for(int location : locations){
                            if(!(wordList.get(location - 1).getWord().equals(tempWord1) && wordList.get(location - 2).getWord().equals(tempWord2))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.insert(0, tempWord2 + " " + tempWord1 + " ");
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }
                        isPhrase = true;
                    }
                }
            }
            
            if((firstLocation >= 1) && (lastLocation <= (listSize - 2))){
                    tempWord1 = wordList.get(firstLocation - 1).getWord();
                    tempWord2 = wordList.get(firstLocation + 1).getWord();
                if((wordList.get(firstLocation - 1).getType().equals("N") ||
                    wordList.get(firstLocation - 1).getType().equals("Nb") ||
                    wordList.get(firstLocation - 1).getType().equals("Ny") ||
                    wordList.get(firstLocation - 1).getType().equals("V") ||
                    wordList.get(firstLocation - 1).getType().equals("Vb") ||
                    wordList.get(firstLocation - 1).getType().equals("Np"))
                    &&
                   (wordList.get(firstLocation + 1).getType().equals("N") ||
                    wordList.get(firstLocation + 1).getType().equals("Nb") ||
                    wordList.get(firstLocation + 1).getType().equals("Ny") ||
                    wordList.get(firstLocation + 1).getType().equals("V") ||
                    wordList.get(firstLocation + 1).getType().equals("Vb") ||
                    wordList.get(firstLocation + 1).getType().equals("A") ||
                    wordList.get(firstLocation + 1).getType().equals("Np"))
                    &&
                    (tempWord2.indexOf(' ') != -1)
                    &&
                    (tempWord1.indexOf(' ') != -1)){

                    if((!(stopWords.contains(tempWord1)))
                        &&
                        (!(stopWords.contains(tempWord2)))
                        ){

                        for(int location : locations){
                            if(!(wordList.get(location - 1).getWord().equals(tempWord1) && wordList.get(location + 1).getWord().equals(tempWord2))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.append(" ").append(tempWord2);
                            keyword.insert(0, tempWord1 + " ");
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }
                        isPhrase = true;
                    }
                }
            }
            
            //bigram
            if(lastLocation <= (listSize - 2)){
                tempWord1 = wordList.get(firstLocation + 1).getWord();
                if((wordType.equals("N") ||
                    wordType.equals("Nb") ||
                    wordType.equals("Ny") ||
                    wordType.equals("V") ||
                    wordType.equals("Vb") ||
                    wordType.equals("Np"))
                    &&
                   (wordList.get(firstLocation + 1).getType().equals("N") ||
                    wordList.get(firstLocation + 1).getType().equals("Nb") ||
                    wordList.get(firstLocation + 1).getType().equals("Ny") ||
                    wordList.get(firstLocation + 1).getType().equals("V") ||
                    wordList.get(firstLocation + 1).getType().equals("Vb") ||
                    wordList.get(firstLocation + 1).getType().equals("A") ||
                    wordList.get(firstLocation + 1).getType().equals("Np"))
                    &&
                    (tempWord1.indexOf(' ') != -1)){

                    if(!(stopWords.contains(tempWord1))){
                        for(int location : locations){
                            if(!(wordList.get(location + 1).getWord().equals(tempWord1))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.append(" ").append(tempWord1);
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }
                        isPhrase = true;
                    }  
                }
            }
            
            if(firstLocation >= 1){
                tempWord1 = wordList.get(firstLocation - 1).getWord();
                if((wordList.get(firstLocation - 1).getType().equals("N") ||
                    wordList.get(firstLocation - 1).getType().equals("Nb") ||
                    wordList.get(firstLocation - 1).getType().equals("Ny") ||
                    wordList.get(firstLocation - 1).getType().equals("V") ||
                    wordList.get(firstLocation - 1).getType().equals("Vb") ||
                    wordList.get(firstLocation - 1).getType().equals("Np"))
                    &&
                   (wordType.equals("N") ||
                    wordType.equals("V") ||
                    wordType.equals("A") ||
                    wordType.equals("Np"))
                    &&
                    (tempWord1.indexOf(' ') != -1)){

                    if(!(stopWords.contains(tempWord1))){
                        for(int location : locations){
                            if(!(wordList.get(location - 1).getWord().equals(tempWord1))){
                                isPhrase = false;
                                break;
                            }
                        }
                        if(isPhrase == true){
                            keyword.insert(0, tempWord1 + " ");
                            tempKeyPhrase = keyword.toString();
                            if(!(phraseMap.containsKey(tempKeyPhrase) && (phraseMap.get(tempKeyPhrase) > tempTf_Idf))){
                                phraseMap.put(tempKeyPhrase, tempTf_Idf);
                            }
                            continue;
                        }

                    }
                }
            }
            phraseMap.put(keyWord, tempTf_Idf);
        }
        
        List<PhraseInfo> result = new ArrayList<>();
        for(Map.Entry<String, Double> entry : phraseMap.entrySet()){
            result.add(new PhraseInfo(entry.getKey(), entry.getValue()));
        }   
        sortPhraseInfoList(result);
        
        return result;
    }
    
    /**
     * Ý nghĩa: trả về các cụm từ khóa của một chunk
     * <h3>Tham số</h3>
     * <b>chunk</b>: là một cấu trúc Map biểu diễn chunk có key là từ trong chunk, value là danh sách vị trí xuất hiện từ đó trong chunk
     * 
     * <h3>Giá trị trả về</h3>
     * Một danh sách các đối tượng thuộc lớp PhraseInfo của các cụm từ khóa của chunk sắp xếp theo thứ tự chỉ số tf-idf giảm dần
    */
    public List<PhraseInfo> getChunkKeyPhrases(Map<String, List<Integer>> chunk) throws IOException, FileNotFoundException, ClassNotFoundException{
        List<PhraseInfo> keyPhrasesInfo = new ArrayList<>();
        Map<String, List<Integer>> chunkKeyWords = getChunkKeyWords(chunk);
        Map<String, Double> chunkTF_IDFs = getWordTF_IDFMaps(chunk);
        PhraseInfo temp;
        Set<String> phraseSet = new HashSet<>();
        
        for(Map.Entry<String, List<Integer>> entry : chunkKeyWords.entrySet()){
            for(int position : entry.getValue()){
                temp = keyPraseDetect(position, chunkTF_IDFs);
                if(!phraseSet.contains(temp.getPhrase())){
                    keyPhrasesInfo.add(temp);
                    phraseSet.add(temp.getPhrase());
                }
            }
        }
        
        sortPhraseInfoList(keyPhrasesInfo);
        
        return keyPhrasesInfo;
    }
    /**
     * Ý nghĩa: Sắp xếp các cụm từ theo chỉ số tf-idf giảm dần
     * <h3>Tham số</h3>
     *<b>keyPhrasesInfo</b>: là một danh sách đối tượng của lớp PhraseInfo
     */
    public void sortPhraseInfoList(List<PhraseInfo> keyPhrasesInfo){
        Collections.sort(keyPhrasesInfo, new Comparator<PhraseInfo>() {
            @Override
            public int compare(PhraseInfo x1, PhraseInfo x2)
            {

                if(x1.getTf_idfScore() > x2.getTf_idfScore()){
                    return -1;
                }
                else if(x1.getTf_idfScore() == x2.getTf_idfScore()){
                    return 0;
                }
                return 1;
            }
        });
        
    }
    
    public Map<String, Double> getKeyPhraseMap() throws IOException, FileNotFoundException, ClassNotFoundException{
        Map<String, Double> result = new HashMap<>();
        List<Map<String, List<Integer>>> topChunks = getTopChunks();
        List<PhraseInfo> tempPhraseInfor;
        double tempTF_IDF;
        
        for(Map<String, List<Integer>> chunk : topChunks){
            tempPhraseInfor = getChunkKeyPhrases(chunk);
            for(PhraseInfo phrase : tempPhraseInfor){
                if(result.containsKey(phrase.getPhrase())){
                    tempTF_IDF = result.get(phrase.getPhrase());
                    result.put(phrase.getPhrase(), tempTF_IDF + phrase.getTf_idfScore());
                }
                else{
                    result.put(phrase.getPhrase(), phrase.getTf_idfScore());
                }
            }
        }
        return result;
    }
    
    /**
     * Ý nghĩa: Lấy các cụm từ khóa của văn bản được lấy từ các cụm từ khóa của các chunk
     * <h3>Tham số</h3>
     *<b>subKeyAllow</b>: là một giá trị boolean, true nếu cho phép các cụm từ khóa bao chứa nhau, false nếu không cho phép các cụm từ khóa bao chứa nhau
     * 
     * <h3>Giá trị trả về</h3>
     * Một danh sách các đối tượng thuộc lớp PhraseInfo của các cụm từ khóa sắp xếp theo thứ tự tf-idf giảm dần
     */
    public List<PhraseInfo> getKeyPhraseList(boolean subKeyAllow) throws IOException, FileNotFoundException, ClassNotFoundException{
        
        List<PhraseInfo> result = new ArrayList<>();
        List<Map<String, List<Integer>>> topChunks = getTopChunks();
        List<PhraseInfo> phraseList = new ArrayList<>();
        List<PhraseInfo> temp;
        
        for(Map<String, List<Integer>> chunk : topChunks){
            temp = getChunkKeyPhrases(chunk);
            phraseList.addAll(temp);
        }
        
        sortPhraseInfoList(phraseList);
        
        if(subKeyAllow == true){
            HashSet<String> phraseSet = new HashSet<>();
            for(PhraseInfo phrase : phraseList){
                if(!phraseSet.contains(phrase.getPhrase())){
                    phraseSet.add(phrase.getPhrase());
                    result.add(phrase);
                }
            }
        }
        else{
            boolean checkSubKey;
            ArrayList<String> checkList = new ArrayList<>();
            for(PhraseInfo phrase : phraseList){
                
                checkSubKey = false;
                
                for(String checkPhrase : checkList){
                    if(checkPhrase.contains(phrase.getPhrase())){
                        checkSubKey = true;
                        break;
                    }
                }
                
                if(checkSubKey == false){
                    checkList.add(phrase.getPhrase());
                    result.add(phrase);
                }
            }
        }
        
        return result;
    }
    
    /**
    *<b>EnvirInitialize()</b>: Khởi tạo các biến tĩnh, bao gồm danh sách từ vựng cùng với chỉ số idf, danh sách các stop word<br> 
    */
    public static void EnvirInitialize() throws FileNotFoundException, IOException, ClassNotFoundException{
        String idfFilePath = "C:\\Users\\DucPC\\Documents\\NetBeansProjects\\PlagiarismDetection\\src\\data\\IDFdata";
        String stopWordFilePath = "C:\\Users\\DucPC\\Documents\\NetBeansProjects\\PlagiarismDetection\\src\\data\\StopWords";
        IDFdataSource = new File(idfFilePath);
        stopWordsFile = new File(stopWordFilePath);
        
        FileInputStream fileIn = new FileInputStream(IDFdataSource);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        wordIDFs = (HashMap<String, Double>) in.readObject();
        fileIn.close();
        in.close();

        fileIn = new FileInputStream(stopWordsFile);
        in = new ObjectInputStream(fileIn);
        stopWords = (HashSet<String>) in.readObject();
        fileIn.close();
        in.close();
        
        fileIn = new FileInputStream("C:\\Users\\DucPC\\Documents\\NetBeansProjects\\PlagiarismDetection\\src\\data\\StopWords2");
        in = new ObjectInputStream(fileIn);
        stopWords2 = (HashSet<String>) in.readObject();
        fileIn.close();
        in.close();
    }
    
    public static void main(String[] args) {
        try{
            EnvirInitialize();
            
            //Path report
            String path_file = "C:\\Users\\DucPC\\Documents\\NetBeansProjects\\2019.03.18.12.57.44.pdf";
            
            KeywordDetection keywordDetect = new KeywordDetection(path_file, 50, 10, 3, 2);
            List<PhraseInfo> phraseList = keywordDetect.documentKeyPhraseDetectFromChunks();
            
            String path_keywords = getAbstractPath(path_file, "keyword");
            System.out.println(path_keywords);

            PrintWriter out = new PrintWriter(path_keywords);
            
            //Save keyword file
            for(PhraseInfo phrase : phraseList){
//                System.out.println(phrase.getPhrase() + " " + phrase.getTf_idfScore());
                out.println(phrase.getPhrase() + " " + phrase.getTf_idfScore());
            }
            
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
