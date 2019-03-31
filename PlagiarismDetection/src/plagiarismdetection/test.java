/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarismdetection;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author DucPC
 */
public class test {
    public static void main(String[] args) throws IOException {
//        String filename = "C:\\Users\\DucPC\\Documents\\NetBeansProjects\\3.doc";
//        String extension = "doc";
//        
//        if(extension.equals("doc") || extension.equals("docx")){
//                XWPFDocument docx = new XWPFDocument(POIXMLDocument.openPackage(filename));
//
//                System.out.println(docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages());
//            }
//            else if(extension.equals("pdf")){
//                PDDocument document= PDDocument.load(new File(filename));
////                PDDocumentInformation info = document.getDocumentInformation();
//                System.out.println( "Page Count=" + document.getNumberOfPages() );
//            }

            String text = "LỜI NÓI ĐẦU\n" +
"Trong thời đại kỷ nguyên số, các thiết bị điện tử cùng hàng loạt các công cụ trợ giúp, cuộc sống con người ngày càng trở nên văn minh và hiện đại hơn. Con người không ngừng tìm tòi và sáng tạo nên các thiết bị thông minh, các hệ thống tự động phục vụ trong cuộc sống hàng ngày có khả năng hiểu các yêu cầu của con người. Cách thức tương tác giữa người và máy càng trở nên dễ dàng và thân thiện. Con người đang chuyển dần phương thức tương tác người – máy bằng các thiết bị phần cứng như bàn phím, chuột, màn hình… sang các phương thức tương tác bằng cử chỉ, giọng nói. Và một trong các hướng nghiên cứu sử dụng tiếng nói trong tương tác người – máy ngày càng được nhiều người quan tâm. Những nghiên cứu này có liên quan trực tiếp tới các kết quả của chuyên ngành xử lý tiếng nói.\n" +
"Tổng hợp tiếng nói là một phần trong xử lý tiếng nói đang được nghiên cứu khá rộng rãi trên thế giới và đã có những kết quả đáng khả quan. Viện Nghiên cứu Quốc tế MICA đã và đang định hướng tổng hợp tiếng nói là một trong những nghiên cứu lâu dài của viện. Với các bài báo nghiên cứu cùng các kết quả thực nghiệm gần đây, MICA đã xây dựng nên một thư viện tổng hợp Tiếng Việt có chất lượng tốt và gần giống với tiếng nói tự nhiên. Với ý định tìm hiểu về các phương pháp tổng hợp tiếng nói nói chung và Tiếng Việt nói chung để phát triển các ứng dụng và dịch vụ tổng hợp tiếng trên các thiết bị di động, Viện Nghiên cứu Quốc Tế MICA là địa điểm phù hợp cho công việc nghiên cứu tổng hợp tiếng cũng như phát triển các ứng dụng.\n" +
"Với đề tài đồ án tốt nghiệp “Tìm hiểu và xây dựng engine tổng hợp tiếng nói từ văn bản trên android”, sự giới thiệu của ThS. Lê Tấn Hùng tới Viện Nghiên cứu Quốc tế MICA, được sự giúp đỡ nhiệt tình và tận tâm của TS. Trần Đỗ Đạt và TS. Mạc Đăng Khoa cùng với nhóm sinh viên nghiên cứu trên viện MICA về xử lý tiếng nói, tôi đã tìm hiểu được phần nào về tổng hợp tiếng nói và ứng dụng để xây dựng nên một Engine Text-To-Speech chạy trên nền tảng Android.\n" +
"Với nội dung đồ án cùng quá trình nghiên cứu, làm việc trên viện MICA, bài báo cáo đồ án tốt nghiệp được chia làm 5 chương với các phần cụ thể như sau:\n" +
"Chương 1. Tiếng nói và tổng hợp tiếng nói. Chương trình bày lý thuyết về tiếng nói, các đặc tính của tiếng nói và biểu diễn tiếng nói dưới dạng tín hiệu số. Và giới thiệu qua 3 phương pháp tổng hợp tiếng nói phổ biến cùng với mô hình tổng hợp tiếng nói từ văn bản.\n" +
"Chương 2. Tổng hợp tiếng việt bằng phương pháp ghép nối các đơn vị âm không đồng nhất. Chương này trình bày các đặc điểm ngữ âm Tiếng Việt cùng với quy trình xây dựng bộ tổng hợp Tiếng Việt mà MICA đã thực hiện.\n" +
"";
            
            System.out.println(text.length());
    }
}
