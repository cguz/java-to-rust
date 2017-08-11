package de.aschoerk.doxiaconv;

import org.apache.maven.doxia.ConverterException;
import org.apache.maven.doxia.UnsupportedFormatException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class DoxiaConverterTest {
    DoxiaConverter converter;

    @Before
    public void beforeDoxiaConverterTest() {
        converter = new DoxiaConverter();
    }

    @Test
    public void testTWikiMacros() throws IOException, UnsupportedFormatException, ConverterException {
        String toConvert = "%INCLUDE{\"Wikisystem.NoteBox\" section=\"info\" TEXT=\"Dokumenten-Status: In Arbeit\"}%\n" +
                "\n" +
                "%TOC{depth=\"2\"}%";
        String result = converter.convert(toConvert, "twiki", "confluence");

        System.out.println("\n\n" + result);

    }

    @Test
    public void testNormalCode() throws IOException, UnsupportedFormatException, ConverterException {
        String toConvert = "---+ Twiki Java Parser\n" +
                "\n" +
                "---++ Features\n" +
                "\n" +
                "This parser of the [[http://www.twiki.org][TWiki]] text format supports most\n" +
                "of http://twiki.org/cgi-bin/view/TWiki/TextFormattingRules formatting commands.\n" +
                "\n" +
                "---+++ General\n" +
                "\n" +
                "   * Paragraps,\n" +
                "   * Wiki Words\n" +
                "      * WikiWord\n" +
                "      * Web.WikiWord#anchor,  \n" +
                "      * escaped: !WikiWord\n" +
                "   * Forced Links:\n" +
                "      * [[wiki word]]\n" +
                "      * escaped ![[wiki word]]\n" +
                "   * Specific links: \n" +
                "      * [[http://www.zauber.com.ar][Zauber]],\n" +
                "      * prevention: ![[http://www.zauber.com.ar][Forced links]]\n" +
                "   * Anchors: [[#AnchorEnd][End]]\n" +
                "   * inline urls:\n" +
                "      * http://twiki.org/\n" +
                "   * mailto link:\n" +
                "      * [[mailto:a@z.com Mail]]\n" +
                "      * [[mailto:?subject=Hi Hi]]\n" +
                "\n" +
                "---+++ Text Format:\n" +
                "\n" +
                "   * *bold*\n" +
                "   * _italic_\n" +
                "   * __bold italic__\n" +
                "   * =Fixedfont=\n" +
                "   * ==Bold fixed==\n" +
                "\n" +
                "And nested formats like:\n" +
                "   * *bold with _italic_ and some =fixed= and bold*\n" +
                "Make sure there is no space between the text and the bold, italic, or other\n" +
                "indicators (* _ __ = ==).\n" +
                "\n" +
                "---+++ Lists\n" +
                "\n" +
                "   * items\n" +
                "      * nested items\n" +
                "      * ordered list\n" +
                "         * arabic numerals\n" +
                "            1. item\n" +
                "            1. item\n" +
                "            1. ...\n" +
                "         * uppercase letters\n" +
                "            A. item\n" +
                "            A. item\n" +
                "            A. ...\n" +
                "         * lowercase letters\n" +
                "            a. item\n" +
                "            a. item\n" +
                "            a. ...\n" +
                "         * uppercase roman numerals\n" +
                "            A. item\n" +
                "            A. item\n" +
                "            A. ....\n" +
                "         * Uppercase Roman Numerals\n" +
                "            I. item\n" +
                "            I. item\n" +
                "            I. ...\n" +
                "         * Lowercase Roman Numerals\n" +
                "            i. item\n" +
                "            i. item\n" +
                "            i. ....\n" +
                "\n" +
                "---+++ Separators\n" +
                "\n" +
                "Up\n" +
                "---------------------------\n" +
                "Down\n" +
                "\n" +
                "---+++ Table\n" +
                "\n" +
                " | *A* | *B* | *C* |\n" +
                " | Foo | bar | Foo |\n" +
                " | Bar | Foo | bar |\n" +
                " | Foo | bar | Foo |\n" +
                "\n" +
                "---++ Missing things\n" +
                "---+++ Verbating Mode\n" +
                "<verbatim>\n" +
                "class CatAnimal {\n" +
                "  void purr() {\n" +
                "      <code here>\n" +
                "}\n" +
                "</verbatim>\n" +
                "\n" +
                "---+++ Definition List\n" +
                "(i don't use it)\n" +
                "   $ Sushi: Japan\n" +
                "      $ Dim Sum: S.F.\n" +
                "   $ Asado: Argentina\n" +
                "\n" +
                "---+++ Diable Links\n" +
                "\n" +
                "<noautolink>\n" +
                "   RedHat &\n" +
                "  SuSE\n" +
                "</noautolink>\n" +
                "   \n" +
                "---+++ Html\n" +
                "    *  <pre>some text</pre>\n" +
                "\n" +
                "#EndAnchor\n";
        String result = converter.convert(toConvert, "twiki", "confluence");

        System.out.println(result);
    }
}
