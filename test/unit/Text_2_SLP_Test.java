package unit;

import slp.util.*;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Text_2_SLP_Test {
    public String inputText;

    @Test
    public void testSpace() {
        inputText = "    ";
        assertEquals(inputText,
                new slp.text_slp.SLP_2_Text().toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

        inputText = "zip zip zip ";
        assertEquals(inputText,
                new slp.text_slp.SLP_2_Text().toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

    }

    @Test
    public void testTab() {
        inputText = "       ";
        assertEquals(inputText,
                new slp.text_slp.SLP_2_Text().toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

        inputText = "   zip     ";
        assertEquals(inputText,
                new slp.text_slp.SLP_2_Text().toText(new slp.text_slp.Text_2_SLP().compress(inputText)));
    }

    @Test
    public void testReturn() {
        inputText = "zip\n";
        assertEquals(inputText,
                new slp.text_slp.SLP_2_Text().toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

        inputText = "zip\r\n";
        assertEquals(inputText,
                new slp.text_slp.SLP_2_Text().toText(new slp.text_slp.Text_2_SLP().compress(inputText)));

        inputText = "zip\r";
        assertEquals(inputText,
                new slp.text_slp.SLP_2_Text().toText(new slp.text_slp.Text_2_SLP().compress(inputText)));
    }

    @Test
    public void testTabSpaceReturn() {
        inputText = "   zzzz zip\n";
        assertEquals(inputText,
                new slp.text_slp.SLP_2_Text().toText(new slp.text_slp.Text_2_SLP().compress(inputText)));
    }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }

    // @Test
    // public void test() {
    // }
}
