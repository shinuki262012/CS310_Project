package unit;

import slp.text_slp.*;
import slp.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class Text_2_SLP_Test {
        public String inputText;
        // TODO: Change it to suit your test folder
        public String testDir = "~\\test\\";
        public Text_2_SLP text2slp;
        public SLP_2_Text slp2text;
        public Folca folca;

        @Test
        public void testSpace() {
                inputText = "    ";
                assertEquals(inputText,
                                new slp.text_slp.SLP_2_Text()
                                                .toText(new slp.text_slp.Text_2_SLP().compress(inputText)));
                inputText = "zip zip zip ";
                assertEquals(inputText,
                                new slp.text_slp.SLP_2_Text()
                                                .toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

        }

        @Test
        public void testTab() {
                inputText = "       ";
                assertEquals(inputText,
                                new slp.text_slp.SLP_2_Text()
                                                .toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

                inputText = "   zip     ";
                assertEquals(inputText,
                                new slp.text_slp.SLP_2_Text()
                                                .toText(new slp.text_slp.Text_2_SLP().compress(inputText)));
        }

        @Test
        public void testReturn() {
                inputText = "zip\n\n";
                assertEquals(inputText,
                                new slp.text_slp.SLP_2_Text()
                                                .toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

                inputText = "zip\r\n";
                assertEquals(inputText,
                                new slp.text_slp.SLP_2_Text()
                                                .toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

                inputText = "zip\r";
                assertEquals(inputText,
                                new slp.text_slp.SLP_2_Text()
                                                .toText(new slp.text_slp.Text_2_SLP().compress(inputText)));
        }

        @Test
        public void testTabSpaceReturn() {
                inputText = "   zzzz zip\n";
                assertEquals(inputText,
                                new slp.text_slp.SLP_2_Text()
                                                .toText(new slp.text_slp.Text_2_SLP().compress(inputText)));
        }

        @Test
        public void offlineTest1() {
                text2slp = new Text_2_SLP();
                slp2text = new SLP_2_Text();
                try {
                        File f1 = new File(testDir + "t");
                        File f2 = new File(testDir + "t(1)");
                        text2slp.text_2_slp(f1.toString());

                        slp2text.toText(new ParseCFG(f1 + ".cfg").getCFG(), f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (IOException e) {
                }
        }

        @Test
        public void offlineTest2() {
                text2slp = new Text_2_SLP();
                slp2text = new SLP_2_Text();
                try {
                        File f1 = new File(testDir + "128random");
                        File f2 = new File(testDir + "128random(1)");
                        text2slp.text_2_slp(f1.toString());

                        slp2text.toText(new ParseCFG(f1 + ".cfg").getCFG(), f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (IOException e) {
                }
        }

        @Test
        public void offlineTest3() {
                text2slp = new Text_2_SLP();
                slp2text = new SLP_2_Text();
                try {
                        File f1 = new File(testDir + "repeated");
                        File f2 = new File(testDir + "repeated(1)");
                        text2slp.text_2_slp(f1.toString());

                        slp2text.toText(new ParseCFG(f1 + ".cfg").getCFG(), f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (IOException e) {
                }
        }

        /* FOLCA */
        @Test
        public void onlineTest1() {
                folca = new Folca();
                try {
                        File f1 = new File(testDir + "t");
                        File f2 = new File(testDir + "t(1)");
                        folca.compress(true, f1.toString());

                        slp2text.toText(new ParseCFG(f1 + ".cfg").getCFG(), f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (Exception e) {
                }
        }

        @Test
        public void onlineTest2() {
                folca = new Folca();
                try {
                        File f1 = new File(testDir + "128random");
                        File f2 = new File(testDir + "128random(1)");
                        folca.compress(true, f1.toString());

                        slp2text.toText(new ParseCFG(f1 + ".cfg").getCFG(), f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (Exception e) {
                }
        }

        @Test
        public void onlineTest3() {
                folca = new Folca();
                try {
                        File f1 = new File(testDir + "repeated");
                        File f2 = new File(testDir + "repeated(1)");
                        folca.compress(true, f1.toString());

                        slp2text.toText(new ParseCFG(f1 + ".cfg").getCFG(), f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (Exception e) {
                }
        }

        @Test
        public void onlineTest4() {
                folca = new Folca();
                try {
                        File f1 = new File(testDir + "t");
                        File f2 = new File(testDir + "t(1)");
                        folca.compress(false, f1.toString());

                        new POPPT_2_TEXT().poppt_2_text(new ParsePOPPT().parsePOPPT(testDir + "t.slp"), f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (Exception e) {
                }
        }

        @Test
        public void onlineTest5() {
                folca = new Folca();
                try {
                        File f1 = new File(testDir + "128random");
                        File f2 = new File(testDir + "128random(1)");
                        folca.compress(false, f1.toString());

                        new POPPT_2_TEXT().poppt_2_text(new ParsePOPPT().parsePOPPT(testDir + "repeated.slp"),
                                        f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (Exception e) {
                }
        }

        @Test
        public void onlineTest6() {
                folca = new Folca();
                try {
                        File f1 = new File(testDir + "repeated");
                        File f2 = new File(testDir + "repeated(1)");
                        folca.compress(false, f1.toString());

                        new POPPT_2_TEXT().poppt_2_text(new ParsePOPPT().parsePOPPT(testDir + "repeated.slp"),
                                        f1.toString());

                        byte[] file1 = Files.readAllBytes(f1.toPath());
                        byte[] file2 = Files.readAllBytes(f2.toPath());
                        System.out.println(file1.toString());

                        assertEquals(true, Arrays.equals(file1, file2));
                } catch (Exception e) {
                }
        }

}
