/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarismdetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import static plagiarismdetection.PlagiarismDetection.readDocFile;
import static plagiarismdetection.PlagiarismDetection.readDocxFile;
import static plagiarismdetection.PlagiarismDetection.readPDFfile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author caomanhhai
 */
public class AbstractDetection {
    String Phieu_text1 = "(|  |\n *)(PHIẾU GIAO NHIỆM VỤ ĐỒ ÁN TỐT NGHIỆP) *\n";
    String Phieu_text2 = "(|  |\n *)(Phiếu Giao Nhiệm Vụ Đồ Án Tốt Nghiệp) *\n";
    String Phieu_text3 = "(|  |\n *)(Phiếu giao nhiệm vụ đồ án tốt nghiệp) *\n";
    
    String Phieu_text_doc1 = "PHIẾU GIAO NHIỆM VỤ ĐỒ ÁN TỐT NGHIỆP";
    String Phieu_text_doc2 = "Phiếu Giao Nhiệm Vụ Đồ Án Tốt Nghiệp";
    String Phieu_text_doc3 = "Phiếu giao nhiệm vụ đồ án tốt nghiệp";
    
    String MucLuc_text1 = "(|  |\n *)(M C   C |MỤC LỤC|CONTENTS|TABLE OF CONTENTS|MỤC LỤC) *\n";
    String MucLuc_text2 = "(|  |\n *)(M c   c |Mục Lục|Contents|Table Of Contents|Mục Lục) *\n";
    String MucLuc_text3 = "(|  |\n *)(Mục lục|Contents|Table of contents|Mục lục) *\n";
    
    String MucLuc_text_doc1 = "M C   C |MỤC LỤC|CONTENTS|TABLE OF CONTENTS|MỤC LỤC";
    String MucLuc_text_doc2 = "M c   c |Mục Lục|Contents|Table Of Contents|Mục Lục";
    String MucLuc_text_doc3 = "Mục lục|Contents|Table of contents|Mục lục";

    String TomTat_text1 = "(|  |\n *)(TÓM TẮT|TÓM TẮT NỘI DUNG|TÓM TẮT NỘI DUNG CỦA ĐỒ ÁN TỐT NGHIỆP|TÓM TẮT NỘI DUNG ĐỒ ÁN TỐT NGHIỆP|TÓM TẮT ĐỒ ÁN|TÓM TẮT NỘI DUNG ĐỒ ÁN:?|TÓM TẮT NỘI DUNG ĐỒ ÁN TỐT NGHIỆP) *\n";
    String TomTat_text2 = "(|  |\n *)(Tóm Tắt|Tóm Tắt Nội Dung|Tóm Tắt Nội Dung của Đồ Án Tốt Nghiệp|Tóm Tắt Nội Dung Đồ Án Tốt Nghiệp|Tóm Tắt Đồ Án|Tóm Tắt Nội Dung Đồ Án:?|Tóm Tắt Nội Dung Đồ Án Tốt Nghiệp) *\n";
    String TomTat_text3 = "(|  |\n *)(Tóm tắt|Tóm tắt nội dung|Tóm tắt nội dung của đồ án tốt nghiệp|Tóm tắt nội dung đồ án tốt nghiệp|Tóm tắt đồ án|Tóm tắt nội dung đồ án:?|Tóm tắt nội dung đồ án tốt nghiệp) *\n";

    String TomTat_text_doc1 = "TÓM TẮT NỘI DUNG ĐỒ ÁN TỐT NGHIỆP|TÓM TẮT NỘI DUNG ĐỒ ÁN TỐT NGHIỆP|TÓM TẮT NỘI DUNG|TÓM TẮT NỘI DUNG CỦA ĐỒ ÁN TỐT NGHIỆP|TÓM TẮT NỘI DUNG ĐỒ ÁN TỐT NGHIỆP|TÓM TẮT ĐỒ ÁN|TÓM TẮT NỘI DUNG ĐỒ ÁN:?|TÓM TẮT NỘI DUNG ĐỒ ÁN TỐT NGHIỆP";
    String TomTat_text_doc2 = "Tóm Tắt Nội Dung Đồ Án Tốt Nghiệp|Tóm Tắt Nội Dung Đồ Án Tốt Nghiệp|Tóm Tắt Nội Dung|Tóm Tắt Nội Dung của Đồ Án Tốt Nghiệp|Tóm Tắt Nội Dung Đồ Án Tốt Nghiệp|Tóm Tắt Đồ Án|Tóm Tắt Nội Dung Đồ Án:?|Tóm Tắt Nội Dung Đồ Án Tốt Nghiệp";
    String TomTat_text_doc3 = "Tóm tắt nội dung đồ án tốt nghiệp|Tóm tắt nội dung đồ án tốt nghiệp|Tóm tắt nội dung|Tóm tắt nội dung của đồ án tốt nghiệp|Tóm tắt nội dung đồ án tốt nghiệp|Tóm tắt đồ án|Tóm tắt nội dung đồ án:?|Tóm tắt nội dung đồ án tốt nghiệp";
    
    String MoDau_text1 = "(|  |\n *)((LỜI|PHẦN) (MỞ|NÓI) ĐẦU *\n|MỞ ĐẦU *\n)";
    String MoDau_text2 = "(|  |\n *)((Lời|Phần) (Mở|Nói) Đầu *\n|Mở Đầu *\n)";
    String MoDau_text3 = "(|  |\n *)((Lời|Phần) (mở|nói) đầu *\n|Mở đầu *\n)";
    
    String MoDau_text_doc1 = "(LỜI|PHẦN) (MỞ|NÓI) ĐẦU|MỞ ĐẦU";
    String MoDau_text_doc2 = "(Lời|Phần) (Mở|Nói) Đầu|Mở Đầu";
    String MoDau_text_doc3 = "(Lời|Phần) (mở|nói) đầu|Mở đầu";

    String KetThuc_text1 = "(|  |\n *)(MỤC LỤC|CONTENTS|TABLE OF CONTENTS|MỤC LỤC|LỜI CẢM ƠN|(LỜI|PHẦN) (MỞ|NÓI) ĐẦU|MỞ ĐẦU|BẢNG PHÂN CÔNG CÔNG VIỆC|ABSTRACT OF THESIS|GIỚI THIỆU CHUNG|DANH MỤC.*|DANH SÁCH.*|PHIẾU GIAO.*|ABSTRACT|LỜI C[ẢÁ]M ƠN|LỜI CÁM ƠN|(CHƯƠNG|PHẦN) (1|MỘT|I)) *\n|(|  |\n *)(CHƯƠNG|PHẦN) (1[^0-9]|MỘT|I[^I]).*\n";
    String KetThuc_text2 = "(|  |\n *)(Mục Lục|Contents|Table Of Contents|Mục Lục|Lời Cảm Ơn|(Lời|Phần) (Mở|Nói) Đầu|Mở Đầu|Bảng Phân Công Công Việc|Abstract Of Thesis|Giới Thiệu Chung|Danh Mục.*|Danh Sách.*|Phiếu Giao.*|Abstract|Lời C[ảá]m Ơn|Lời Cám Ơn|(Chương|Phần) (1|Một|i)) *\n|(|  |\n *)(Chương|Phần) (1[^0-9]|Một|I[^I]).*\n";
    String KetThuc_text3 = "(|  |\n *)(Mục lục|Contents|Table of contents|Mục lục|Lời cảm ơn|(Lời|Phần) (mở|nói) đầu|Mở đầu|Bảng phân công công việc|Abstract of thesis|Giới thiệu chung|Danh mục.*|Danh sách.*|phiếu giao.*|Abstract|Lời c[ảá]m ơn|Lời cám ơn|(Chương|Phần) (1|một|i)) *\n|(|  |\n *)(Chương|Phần) (1[^0-9]|một|i[^i]).*\n";

    String KetThuc_text_doc1 = "MỤC LỤC|CONTENTS|TABLE OF CONTENTS|MỤC LỤC|LỜI CẢM ƠN|(LỜI|PHẦN) (MỞ|NÓI) ĐẦU|MỞ ĐẦU|BẢNG PHÂN CÔNG CÔNG VIỆC|ABSTRACT OF THESIS|GIỚI THIỆU CHUNG|DANH MỤC.*|DANH SÁCH.*|PHIẾU GIAO.*|ABSTRACT|LỜI C[ẢÁ]M ƠN|LỜI CÁM ƠN|(CHƯƠNG|PHẦN) (1|MỘT|I)|(CHƯƠNG|PHẦN) (1[^0-9]|MỘT|I[^I]).*\n";
    String KetThuc_text_doc2 = "Mục Lục|Contents|Table Of Contents|Mục Lục|Lời Cảm Ơn|(Lời|Phần) (Mở|Nói) Đầu|Mở Đầu|Bảng Phân Công Công Việc|Abstract Of Thesis|Giới Thiệu Chung|Danh Mục.*|Danh Sách.*|Phiếu Giao.*|Abstract|Lời C[ảá]m Ơn|Lời Cám Ơn|(Chương|Phần) (1|Một|i)|(Chương|Phần) (1[^0-9]|Một|I[^I]).*\n";
    String KetThuc_text_doc3 = "Mục lục|Contents|Table of contents|Mục lục|Lời cảm ơn|(Lời|Phần) (mở|nói) đầu|Mở đầu|Bảng phân công công việc|Abstract of thesis|Giới thiệu chung|Danh mục.*|Danh sách.*|phiếu giao.*|Abstract|Lời c[ảá]m ơn|Lời cám ơn|(Chương|Phần) (1|một|i)|(Chương|Phần) (1[^0-9]|một|i[^i]).*\n";    
    
    String Chuong1_text1 = "(|  |\n *)(CHƯƠNG|PHẦN) (1|MỘT|I) *\n|(|  |\n *)(CHƯƠNG|PHẦN) (1[^0-9]|MỘT|I[^I]).*\n";
    String Chuong1_text2 = "(|  |\n *)(Chương|Phần) (1|Một|I) *\n";
    String Chuong1_text3 = "(|  |\n *)(Chương|Phần) (1[^0-9]|một|I[^I]).*\n";
    String Chuong1_text4 = "(|  |\n *)(1[^0-9]|I[^I]).*\n";
    
    String Chuong1_text_doc1 = "(CHƯƠNG|PHẦN) (1|MỘT|I)|(CHƯƠNG|PHẦN) (1[^0-9]|MỘT|I[^I]).*\n";
    String Chuong1_text_doc2 = "(Chương|Phần) (1|Một|I) *\n";
    String Chuong1_text_doc3 = "(Chương|Phần) (1[^0-9]|một|I[^II]).*\n";
    
    String Chuong1_origin_text1 = "(PHẦN|CHƯƠNG) (1|MỘT|I[^II|^III|^IV])((.|,|:)| )";
    String Chuong1_origin_text2 = "(Phần|Chương) (1|Một|I[^II|^III|^IV])((.|,|:)| )";
    String Chuong1_origin_text3 = "(Phần|Chương) một((.|,|:)| )";

    String Chuong2_text1 = "(|  |\n *)(CHƯƠNG|PHẦN) (2|HAI|II) *\n|(|  |\n *)(CHƯƠNG|PHẦN) (2[^0-9]|HAI|II[^I]).*\n";
    String Chuong2_text2 = "(|  |\n *)(Chương|Phần) (2|Hai|II) *\n";
    String Chuong2_text3 = "(|  |\n *)(Chương|Phần) (2[^0-9]|hai|II[^I]).*\n";
    String Chuong2_text4 = "(|  |\n *)(2[^0-9]|II[^I]).*\n";
    
    String Chuong2_text_doc1 = "(CHƯƠNG|PHẦN) (2|HAI|II)|(CHƯƠNG|PHẦN) (2[^0-9]|HAI|II[^I]).*\n";
    String Chuong2_text_doc2 = "(Chương|Phần) (2|Hai|II) *\n";
    String Chuong2_text_doc3 = "(Chương|Phần) (2[^0-9]|hai|II[^I]).*\n";
    String Chuong2_text_doc4 = "(2[^0-9]|II[^I]).*\n";
    
    String Chuong2_origin_text1 = "(PHẦN|CHƯƠNG) (2|HAI|II[^I|^III|^IV])((.|,|:)| )";
    String Chuong2_origin_text2 = "(Phần|Chương) (2|HAI|II[^I|^III|^IV])((.|,|:)| )";
    String Chuong2_origin_text3 = "(Phần|Chương) hai((.|,|:)| )";
    
    String KetThuc_MucLuc1 = "(|  |\n *)(TÓM TẮT NỘI DUNG ĐỒ ÁN TỐT NGHIỆP|LỜI CẢM ƠN|(LỜI|PHẦN) (MỞ|NÓI) ĐẦU|MỞ ĐẦU|BẢNG PHÂN CÔNG CÔNG VIỆC|ABSTRACT OF THESIS|DANH MỤC.*|DANH SÁCH.*|PHIẾU GIAO.*|ABSTRACT|LỜI C[ẢÁ]M ƠN|LỜI CÁM ƠN) *\n";
    String KetThuc_MucLuc2 = "(|  |\n *)(Tóm Tắt Nội Dung Đồ Án Tốt Nghiệp|Lời Cảm Ơn|(Lời|Phần) (Mở|Nói) Đầu|Mở Đầu|Bảng Phân Công Công Việc|Abstract Of Thesis|Danh Mục.*|Danh Sách.*|Phiếu Giao.*|Abstract|Lời C[ảá]m Ơn|Lời Cám Ơn) *\n";
    String KetThuc_MucLuc3 = "(|  |\n *)(Tóm tắt nội dung đồ án tốt nghiệp|Lời cảm ơn|(Lời|Phần) (mở|nói) đầu|Mở đầu|Bảng phân công công việc|Abstract of thesis|Danh mục.*|Danh sách.*|phiếu giao.*|Abstract|Lời c[ảá]m ơn|Lời cám ơn) *\n";
    
    String KetThuc_MucLuc_doc1 = "TÓM TẮT NỘI DUNG ĐỒ ÁN TỐT NGHIỆP|LỜI CẢM ƠN|(LỜI|PHẦN) (MỞ|NÓI) ĐẦU|MỞ ĐẦU|BẢNG PHÂN CÔNG CÔNG VIỆC|ABSTRACT OF THESIS|DANH MỤC.*|DANH SÁCH.*|PHIẾU GIAO.*|ABSTRACT|LỜI C[ẢÁ]M ƠN|LỜI CÁM ƠN";
    String KetThuc_MucLuc_doc2 = "Tóm Tắt Nội Dung Đồ Án Tốt Nghiệp|Lời Cảm Ơn|(Lời|Phần) (Mở|Nói) Đầu|Mở Đầu|Bảng Phân Công Công Việc|Abstract Of Thesis|Danh Mục.*|Danh Sách.*|Phiếu Giao.*|Abstract|Lời C[ảá]m Ơn|Lời Cám Ơn";
    String KetThuc_MucLuc_doc3 = "Tóm tắt nội dung đồ án tốt nghiệp|Lời cảm ơn|(Lời|Phần) (mở|nói) đầu|Mở đầu|Bảng phân công công việc|Abstract of thesis|Danh mục.*|Danh sách.*|phiếu giao.*|Abstract|Lời c[ảá]m ơn|Lời cám ơn";
    
    List<String> Phieu_text;
    List<String> TomTat_text;
    List<String> MoDau_text;
    List<String> Chuong1_text;
    List<String> Chuong2_text;
    List<String> KetThuc_text;
    List<String> MucLuc_text;
    List<String> KetThuc_MucLuc;
    List<String> KetThuc_TomTat;

    public AbstractDetection() {
        Phieu_text = new ArrayList<>();
        Phieu_text.add(Phieu_text1);
        Phieu_text.add(Phieu_text2);
        Phieu_text.add(Phieu_text3);
        Phieu_text.add(Phieu_text_doc1);
        Phieu_text.add(Phieu_text_doc2);
        Phieu_text.add(Phieu_text_doc3);
        
        TomTat_text = new ArrayList<>();
        TomTat_text.add(TomTat_text1);
        TomTat_text.add(TomTat_text2);
        TomTat_text.add(TomTat_text3);
        TomTat_text.add(TomTat_text_doc1);
        TomTat_text.add(TomTat_text_doc2);
        TomTat_text.add(TomTat_text_doc3);
        
        MoDau_text = new ArrayList<>();
        MoDau_text.add(MoDau_text1);
        MoDau_text.add(MoDau_text2);
        MoDau_text.add(MoDau_text3);
        MoDau_text.add(MoDau_text_doc1);
        MoDau_text.add(MoDau_text_doc2);
        MoDau_text.add(MoDau_text_doc3);
        
        Chuong1_text = new ArrayList<>();
        Chuong1_text.add(Chuong1_text1);
        Chuong1_text.add(Chuong1_text2);
        Chuong1_text.add(Chuong1_text3);
        Chuong1_text.add(Chuong1_text4);
        Chuong1_text.add(Chuong1_text_doc1);
        Chuong1_text.add(Chuong1_text_doc2);
        Chuong1_text.add(Chuong1_text_doc3);
        Chuong1_text.add(Chuong1_origin_text1);
        Chuong1_text.add(Chuong1_origin_text2);
        Chuong1_text.add(Chuong1_origin_text3);
        
        Chuong2_text = new ArrayList<>();
        Chuong2_text.add(Chuong2_text1);
        Chuong2_text.add(Chuong2_text2);
        Chuong2_text.add(Chuong2_text3);
        Chuong2_text.add(Chuong2_text4);
        Chuong2_text.add(Chuong2_text_doc1);
        Chuong2_text.add(Chuong2_text_doc2);
        Chuong2_text.add(Chuong2_text_doc3);
        Chuong2_text.add(Chuong2_text_doc4);
        Chuong2_text.add(Chuong2_origin_text1);
        Chuong2_text.add(Chuong2_origin_text2);
        Chuong2_text.add(Chuong2_origin_text3);
        
        KetThuc_text = new ArrayList<>();
        KetThuc_text.add(KetThuc_text1);
        KetThuc_text.add(KetThuc_text2);
        KetThuc_text.add(KetThuc_text3);
        KetThuc_text.add(KetThuc_text_doc1);
        KetThuc_text.add(KetThuc_text_doc2);
        KetThuc_text.add(KetThuc_text_doc3);
        
        MucLuc_text = new ArrayList<>();
        MucLuc_text.add(MucLuc_text1);
        MucLuc_text.add(MucLuc_text2);
        MucLuc_text.add(MucLuc_text3);
        MucLuc_text.add(MucLuc_text_doc1);
        MucLuc_text.add(MucLuc_text_doc2);
        MucLuc_text.add(MucLuc_text_doc3);
        
        KetThuc_MucLuc = new ArrayList<>();
        KetThuc_MucLuc.add(KetThuc_MucLuc1);
        KetThuc_MucLuc.add(KetThuc_MucLuc2);
        KetThuc_MucLuc.add(KetThuc_MucLuc3);
        KetThuc_MucLuc.add(KetThuc_MucLuc_doc1);
        KetThuc_MucLuc.add(KetThuc_MucLuc_doc2);
        KetThuc_MucLuc.add(KetThuc_MucLuc_doc3);

        KetThuc_TomTat = new ArrayList<>();
        KetThuc_TomTat.add(KetThuc_MucLuc1);
        KetThuc_TomTat.add(KetThuc_MucLuc2);
        KetThuc_TomTat.add(KetThuc_MucLuc3);
        KetThuc_TomTat.add(KetThuc_MucLuc_doc1);
        KetThuc_TomTat.add(KetThuc_MucLuc_doc2);
        KetThuc_TomTat.add(KetThuc_MucLuc_doc3);
        KetThuc_TomTat.add(Chuong1_origin_text1);
        KetThuc_TomTat.add(Chuong1_origin_text2);
        KetThuc_TomTat.add(Chuong1_origin_text3);
    }
    
    public String nomalize(String text){
        text = text.replaceAll("[sS]inh viên thực hiện.*\n", " ");
        text = text.replaceAll("\n+", "\n");
        text = text.replace("TC \"LỜI MỞ ĐẦU\" \\f A \\l \"1\"  TC \"LỜI MỞ ĐẦU\" \\f C \\l \"1\"", "");
        return text;
    }
    
    public  Matcher search(List<String> list, String text){
        Pattern p;
        Matcher m;
        for(String regex : list){
            p = Pattern.compile(regex);
            m = p.matcher(text);
            if(m.find() == true){
                return m;
            }
        }
        
        return null;
    }
    
//    public String getSubTomTat(String)
    
    public String getTomTat(String text){
        Matcher MucLuc = search(MucLuc_text, text);
        
        String TruocMucLuc = "";
        String SauMucLuc = "";
        Matcher TomTat_;
        Matcher MoDau_;
        Matcher KetThucTomTat;
        Matcher KetThucMoDau;
        Matcher PhieuDanhGia_;
        Matcher PhieuDanhGia__;
        Matcher _PhieuDanhGia;
        Matcher KetThucDanhGia;
        Matcher _TomTat;
        Matcher _MoDau;
        Matcher contain_title;
        
        try{
            if(MucLuc != null){
                TruocMucLuc = text.substring(0, MucLuc.end());
                SauMucLuc = text.substring(MucLuc.end() + 600);
            }else{
                SauMucLuc = text;
            }
            
            _TomTat = search(TomTat_text, TruocMucLuc);
            if(_TomTat != null){
                KetThucTomTat = search(KetThuc_text, TruocMucLuc.substring(_TomTat.end()));

                String tomtat1 = TruocMucLuc.substring(_TomTat.start(), _TomTat.end() + KetThucTomTat.start());
                String title_txt = TruocMucLuc.substring(_TomTat.end() + KetThucTomTat.start());
                contain_title = search(Chuong2_text, title_txt);
                String all_tomtat = tomtat1;
                if (contain_title != null){
                    String last_tomtat = title_txt.substring(contain_title.start());
                    String first_tomtat = title_txt.substring(0, contain_title.start());
                    if (first_tomtat.length() < 1000){
                        return all_tomtat + first_tomtat + last_tomtat;
                    }
                }

                return tomtat1;
            }else{
                TomTat_ = search(TomTat_text, SauMucLuc);
                if (TomTat_ != null){
                    try{
                        KetThucTomTat = search(KetThuc_text, SauMucLuc.substring(TomTat_.end()));

                        String tomtat1 = SauMucLuc.substring(TomTat_.start(), TomTat_.end() + KetThucTomTat.start());
                        String title_txt = SauMucLuc.substring(TomTat_.end() + KetThucTomTat.start());
                        contain_title = search(Chuong2_text, title_txt);
                        String all_tomtat = tomtat1;
                        if (contain_title != null){
                            String first_tomtat = title_txt.substring(0, contain_title.start());
                            String last_tomtat = title_txt.substring(contain_title.start());
                            if (first_tomtat.length() < 1000){
                                all_tomtat += first_tomtat;
                                Matcher get_title = search(KetThuc_TomTat, last_tomtat);
                                if (get_title != null){
                                    return all_tomtat + last_tomtat.substring(0, get_title.start());
                                }
                                return all_tomtat;
                            }
                        }

                        return tomtat1;
                    }
                    catch(Exception e){
                    }
                }else{
                    _MoDau = search(MoDau_text, TruocMucLuc);
                    if (_MoDau != null){
                        KetThucMoDau = search(KetThuc_text, TruocMucLuc.substring(_MoDau.end()));

                        String modau1 = TruocMucLuc.substring(_MoDau.start(), _MoDau.end() + KetThucMoDau.start());
                        String title_txt = TruocMucLuc.substring(_MoDau.end() + KetThucMoDau.start());
                        contain_title = search(Chuong2_text, title_txt);
                        String all_modau = modau1;

                        if (contain_title != null){
                            String last_modau = title_txt.substring(contain_title.start());
                            String first_modau = title_txt.substring(0, contain_title.start());

                            if (first_modau.length() < 1000){
                                return all_modau + first_modau + last_modau;
                            }
                        }

                        return modau1;
                    }else{
                        MoDau_ = search(MoDau_text, SauMucLuc);
                        if (MoDau_ != null){
                            try{   
                                KetThucMoDau = search(KetThuc_text, SauMucLuc.substring(MoDau_.end()));

                                if (KetThucMoDau == null){
                                    String txt = SauMucLuc.substring(MoDau_.start());
                                    if (txt.length() > 6000){
                                        return txt.substring(0, 6000);
                                    }else{
                                        return txt;
                                    }
                                }
                                String modau1 = SauMucLuc.substring(MoDau_.start(), MoDau_.end() + KetThucMoDau.start());
                                String title_txt = SauMucLuc.substring(MoDau_.end() + KetThucMoDau.start());
                                contain_title = search(Chuong2_text, title_txt);
                                String all_modau = modau1;
                                if (contain_title != null){
                                    String first_modau = title_txt.substring(0, contain_title.start());
                                    String last_modau = title_txt.substring(contain_title.start());

                                    if (first_modau.length() < 1000){
                                        all_modau += first_modau;
                                        Matcher get_title = search(KetThuc_TomTat, last_modau);
                                        if (get_title != null){
                                            return all_modau + last_modau.substring(0, get_title.start());
                                        }
                                    }

                                    return modau1;
                                }else{
                                    return all_modau;
                                }
                            }
                            catch(Exception e){

                            }
                        }else{
                            _PhieuDanhGia = search(Phieu_text, TruocMucLuc);
                            if (_PhieuDanhGia != null){
                                KetThucDanhGia = search(KetThuc_text, TruocMucLuc.substring(_PhieuDanhGia.end()));;
                                return TruocMucLuc.substring(_PhieuDanhGia.start(), _PhieuDanhGia.end() + KetThucDanhGia.start());
                            }else{
                                PhieuDanhGia_ = search(Phieu_text, SauMucLuc);
                                if (PhieuDanhGia_ != null){
                                    try{
                                        PhieuDanhGia__ = search(MoDau_text, SauMucLuc.substring(PhieuDanhGia_.end()));
                                        KetThucDanhGia = search(KetThuc_text, SauMucLuc.substring(PhieuDanhGia_.end() + PhieuDanhGia__.end()));
                                        return SauMucLuc.substring(PhieuDanhGia_.start() + PhieuDanhGia__.end(),PhieuDanhGia_.end() + PhieuDanhGia__.end() + KetThucDanhGia.start());
                                    }
                                    catch(Exception e){
                                        KetThucDanhGia = search(KetThuc_text, SauMucLuc.substring(PhieuDanhGia_.end()));
                                        return SauMucLuc.substring(PhieuDanhGia_.start(), PhieuDanhGia_.end() + KetThucDanhGia.start());
                                    }
                                }else{
                                    return "";
                                }
                            }
                        }
                    }
                }
            }
//            }
        }catch(Exception e){
            return "";
        }
        return "";
    }
    
    public String getMucLuc(String text){
        try{
            Matcher MucLuc = search(MucLuc_text, text);
            String MucLuc_text = text.substring(MucLuc.start());

            Matcher KetThucMucLuc;
            Matcher _Chuong1;
            Matcher Chuong1_;
            if (MucLuc != null){
                KetThucMucLuc = search(KetThuc_MucLuc, MucLuc_text);
                if (KetThucMucLuc == null){
                    _Chuong1 = search(Chuong1_text, MucLuc_text);
                    if (_Chuong1 != null){
                        Chuong1_ = search(Chuong1_text, MucLuc_text.substring(_Chuong1.end()));
                        if (Chuong1_ != null){
                            
                            String result = MucLuc_text.substring(0, _Chuong1.end() + Chuong1_.start());
                            Matcher tmp;
                            if (result.length() < 100){
                                tmp = search(Chuong1_text, MucLuc_text.substring(_Chuong1.end() + Chuong1_.end()));
                                if (tmp != null){
                                    return result + MucLuc_text.substring(_Chuong1.end() + Chuong1_.start(), _Chuong1.end() + Chuong1_.start() + tmp.start());
                                }else{
                                    return result;
                                }
                            }
                            
                            return result;
                        }else{
                            return MucLuc_text.substring(0, _Chuong1.end());
                        }
                    }
                    
                    return MucLuc_text;
                }
                else{
                    String contain = MucLuc_text.substring(0, KetThucMucLuc.end());
                    Matcher tmp;
                    String contain_tmp = MucLuc_text.substring(KetThucMucLuc.end());
                    
                    int old_len = 0;
                    while (contain.length() < 1000){
                        tmp = search(KetThuc_MucLuc, contain_tmp);
                        if (tmp != null){
                            contain += contain_tmp.substring(0, tmp.end());
                            contain_tmp = contain_tmp.substring(tmp.end());
                        }else{
                            _Chuong1 = search(Chuong1_text, contain_tmp);
                            if (_Chuong1 != null){
                                Chuong1_ = search(Chuong1_text, MucLuc_text.substring(_Chuong1.end()));
                                if (Chuong1_ != null){
                                    contain += contain_tmp.substring(0, _Chuong1.end() + Chuong1_.start());
                                    contain_tmp = contain_tmp.substring(_Chuong1.end() + Chuong1_.start());

                                }
                            }       
                        }
                        
                        if (old_len == contain.length()){
                            break;
                        }
                        old_len = contain.length();
                    }
                    return contain;
                }
            }
        }catch(Exception e){
            return "";
        }
        return "";
    }
    
    public String getTenPage(String text){
        return "";
    }
    
    public static String getAbstractPath(String full_path, String file_extension){
        int index = 0;
        for (int i = full_path.length() - 1; i > 0; i --){
            if (full_path.charAt(i) == '.'){
                index = i;
                break;
            }
        }
        String path = "";
        for (int i = 0; i < index + 1; i ++){
            path += full_path.charAt(i);
        }
        path += file_extension;
        return path;
    }
    
    public String getAbstract(String path, String text, int is_pdf) 
            throws FileNotFoundException, IOException{       
        String result = "";
        List<String> contain_sentence = new ArrayList<>();
        if (text.length()/4 < 20000 && text.length() > 20000){
            text = nomalize(text.substring(0, 20000));
        }else if(text.length()/4 > 20000){
            text = nomalize(text.substring(0, text.length()/4));
        }
        
        String TomTat = getTomTat(text);
        String MucLuc = getMucLuc(text);
        
        if (TomTat.equals("") && MucLuc.equals("")){
            if (text.length() > 15000){
                TomTat = text.substring(0, 15000);
            }else{
                TomTat = text;
            }
            
            String array1[] = MucLuc.split("\n");
            String array2[] = TomTat.split("\n");
            try{
                boolean is_stop = false;
                int i = 0;
                for (String temp: array1){
                    contain_sentence.add(temp);
                    contain_sentence.add("\n");
                }
                contain_sentence.add("\n\n\n");

                for (String temp: array2){
                    contain_sentence.add(temp);
                    contain_sentence.add("\n");
                }

            }catch(Exception e){
                return "";
            }

            for(String sent: contain_sentence){
                result += sent;
            }

            String abstract_path = getAbstractPath(path, "abstract");
            try (PrintWriter out = new PrintWriter(abstract_path)) {
                for (String line : result.split("\n")){
                    out.println(line);
                }
            }

            return result;
        }else{
        
            String array1[] = MucLuc.split("\n");
            String array2[] = TomTat.split("\n");
            try{
                boolean is_stop = false;
                int i = 0;
                for (String temp: array1){
                    if (i > 15){
                        if (temp.toUpperCase().contains("DANH MỤC") || 
                                temp.toUpperCase().contains("MỤC LỤC") ||
                                temp.toUpperCase().contains("DANH MỤC")){
                            if(temp.toUpperCase().contains("MỤC LỤC MỤC LỤC")){
                                continue;
                            }
                            break;
                        }

                        if (temp.contains("Danh mục các hình ảnh trong Đồ án") ||
                            temp.contains("MỤC LỤC HÌNH VẼ")){
                            break;
                        }

                        if (temp.equals("LÝ DO CHỌN ĐỀ TÀI :") ||
                            temp.toUpperCase().contains("BẢNG GIẢI NGHĨA CÁC KÝ TỰ, CHỮ VIẾT TẮT")){
                            break;
                        }
                    }
                    i += 1;
                    if (is_stop == true){
                        break;
                    }
                    if (temp.toUpperCase().contains("TÀI LIỆU THAM KHẢO") ||
                            temp.toUpperCase().contains("TÀI LIỆU THAM KHẢO")){
                        is_stop = true;
                    }

                    if (is_pdf == 1){
                        if(temp.contains("Lớp Hệ Thống Thông Tin")){
                            continue;
                        }
                    }
                    if (temp.toUpperCase().contains("SINH VIÊN")){
                        continue;
                    }

                    contain_sentence.add(temp);
                    contain_sentence.add("\n");
                }
                contain_sentence.add("\n\n\n");

                for (String temp: array2){
                    if (is_pdf == 1){
                        if(temp.contains("Lớp Hệ Thống Thông Tin")){
                            continue;
                        }
                    }else{
                        if(temp.contains("....................................")){
                            break;
                        }
                    }
                    if (temp.toUpperCase().contains("ABSTRACT OF THESIS") ||
                            temp.toUpperCase().contains("ABTRACT OF THESIS") ||
                            temp.toUpperCase().contains("ABSTRACT") ||
                            temp.toUpperCase().contains("LỜI CẢM ƠN") ||
                            temp.toUpperCase().contains("MỤC LỤC") ||
                            temp.toUpperCase().contains("DANH SÁCH CÁC TỪ VIẾT TẮT") ||
                            temp.toUpperCase().contains("SUMMARY CONTENT OF GRADUATION PROJECT")||
                            temp.toUpperCase().contains("LỜI CẢM ƠN") ||
                            temp.toUpperCase().contains("DANH MỤC HÌNH") ||
                            temp.toUpperCase().contains("DANH SÁCH MỤC") ||
                            temp.toUpperCase().contains("DANH SÁCH CÁC HÌNH")){
                        break;
                    }

                    if (temp.length() > 14){
                        if (temp.substring(0, 15).toUpperCase().contains("SINH VIÊN")){
                            continue;
                        }
                    }

                    contain_sentence.add(temp);
                    contain_sentence.add("\n");
                }

            }catch(Exception e){
                return "";
            }

            for(String sent: contain_sentence){
                result += sent;
            }

            String abstract_path = getAbstractPath(path, "abstract");
            try (PrintWriter out = new PrintWriter(abstract_path)) {
                for (String line : result.split("\n")){
                    out.println(line);
                }
            }

            return result;
        }
    }
    
    public static void main(String[] args) throws IOException, FileNotFoundException, InvalidFormatException {            
        String fileName = "C:\\Users\\DucPC\\Documents\\NetBeansProjects\\2_without_mucluc.docx";
        String extension = PlagiarismDetection.getFileExtension(fileName).toLowerCase();

        String text;
        int is_pdf = 0;
        if(extension.equals("doc")){
            text = readDocFile(fileName);
        }
        else if(extension.equals("docx")){
            text = readDocxFile(fileName);
        }
        else if(extension.equals("pdf")){
            text = readPDFfile(fileName);
            is_pdf = 1;
        }
        else{
            System.out.println("wrong extension");
            return;
        }
        
        AbstractDetection ad = new AbstractDetection();
        System.out.println("---------------");
        System.out.println(ad.getAbstract(
                "C:\\Users\\DucPC\\Documents\\NetBeansProjects\\extract_summary.txt",
                text, is_pdf));
    }
}
