
import java.io.File; 
  
import net.sourceforge.tess4j.Tesseract; 
import net.sourceforge.tess4j.TesseractException; 
  
public class OCR_App { 
    public static void main(String[] args) 
    { 
        Tesseract tesseract = new Tesseract(); 
        try { 
  
            tesseract.setDatapath("D:/OneDrive/OO/OO_Software/ORC/OCR_Organizer/OCR_Archiver/OCR/tessdata"); 
  
            String text 
                = tesseract.doOCR(new File("D:/OneDrive/Word Docs/Aliquo Stories/Consumption/NYT_Consumption_01.pdf")); 
                
            System.out.print(text); 
        } 
        catch (TesseractException e) { 
            e.printStackTrace(); 
        } 
    }
} 
