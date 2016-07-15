package de.aschoerk.javaconv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by aschoerk on 02.07.16.
 */
@RunWith(JUnit4.class)
public class IntegerConvTest extends Base {
    @Test
    public void testArrays() {
        String res = call(" class A { void m() { double[] testArray = {0, 1, +2., 3.1, -4, 5 * 10}; };  }  ");
        System.out.println(res);
        res = call(" class A { void m() { double[][] testArray = {{0, 1, +2., 3.1, -4, 5}}; };  }  ");
        System.out.println(res);
    }

    @Test
    public void testDecl() {
        String res = call(" class A { void m() { double test = 1; float x = 10; };  }  ");
        System.out.println(res);

    }

    @Test
    public void testExpression() {
        String res = call(" class A { void m() { double test = 1; test = 10 * 1.5; float x = 10; };  }  ");
        System.out.println(res);
        res = call(" class A { void m() { double test = 1; test = 10 * 1.5; float x = 10; int x2 = 20; };  }  ");
        System.out.println(res);
    }

    @Test
    public void testForExpression() {
        String res = call(" class A { void m() { for (int i = 0; i < 10.0; i++) { } };  }  ");
        System.out.println(res);
    }

    @Test
    public void testForExpression2() {

        String res = call(" class A { void m() {" +
                "final int from = 3;\n" +
                "    final int to = source.length + 14;\n" +
                "    final double[] dest = MathArrays.copyOfRange(source, from, to);\n" +
                "\n" +
                "    Assert.assertEquals(dest.length, to - from);\n" +
                "    for (int i = from; i < source.length; i++) {\n" +
                "        Assert.assertEquals(source[i + 1], dest[i - from + 1], 0);\n" +
                "    } };  }  ");
        System.out.println(res);
    }

    @Test
    public void testConst() {
        String res = call(" {\n" +
                "        int j = 0;             \n" +
                "        final double[] u = { 0};       \n" +
                "    }\n  ");
        System.out.println(res);
    }


    @Test
    public void testParameter() {
        String res = call(" class A { void m(double x) { m(10); };  }  ");
        System.out.println(res);

    }

}
