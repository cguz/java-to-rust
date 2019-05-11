package de.aschoerk.javaconv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.junit.Assert;

@RunWith(JUnit4.class)
public class CommentTest extends Base {

    @Test
    public void noDuplicateJavadocComments() {

        String res = call("/**\n" + 
                " * Interface comment\n" + 
                " */\n" + 
                "public interface X {\n" + 
                "  /**\n" + 
                "   * Hello\n" + 
                "   */\n" + 
                "  // World\n" + 
                "  int hello();\n" + 
                "\n" + 
                "  /**\n" + 
                "   * Just javadoc\n" + 
                "   */\n" + 
                "   int ohyes();\n" + 
                "}\n" + 
                "");
        Assert.assertEquals("/**\n" + 
                " * Interface comment\n" + 
                " */\n" + 
                "pub trait X {\n" + 
                "\n" + 
                "    /**\n" + 
                "   * Hello\n" + 
                "   */\n" + 
                "    // World\n" + 
                "    fn  hello(&self) -> i32 ;\n" + 
                "\n" + 
                "    /**\n" + 
                "   * Just javadoc\n" + 
                "   */\n" + 
                "    fn  ohyes(&self) -> i32 ;\n" + 
                "}\n" +
                "\n", res);
    }
    @Test
    public void packageDeclarationJavadoc() {
        String actual = call("/**\n" + 
                " * Licence\n" + 
                " */\n" + 
                "// Comment\n" + 
                "package y;\n" + 
                "\n" + 
                "/**\n" + 
                " * Class.\n" + 
                " */\n" + 
                "public class C{}");
        Assert.assertEquals("/**\n" + 
                " * Licence\n" + 
                " */\n" + 
                "// Comment\n" +
                "// package y;\n" + 
                "\n" +
                "/**\n" + 
                " * Class.\n" + 
                " */\n" + 
                "pub struct C {\n" + 
                "}\n" + 
                "\n" + 
                "impl C {\n" + 
                "}\n" + 
                "\n", actual);
    }
}
