package LZ77_SLP;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;

public class LZ77_2_SLP {

    LZ77parser lz = new LZ77parser();

    public void parseLZ77() {
        try {
            InputStream fileInputStream = new FileInputStream("..\\test\\tt.gz");
            int headerLength = lz.readHeader(fileInputStream);
            System.out.println("Header length: " + headerLength);

            int b = lz.readUByte(fileInputStream);
            while (b != -1) {
                System.out.println(b);
                b = lz.readUByte(fileInputStream);
                // if (b == -1) {
                // lz.readTrailer(fileInputStream);
                // }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    public static void main(String[] args) {
        new LZ77_2_SLP().parseLZ77();
    }
}