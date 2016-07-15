package de.aschoerk.javaconv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by aschoerk on 03.07.16.
 */
@RunWith(JUnit4.class)
public class StringExpConvTest extends Base {
    @Test
    public void testDecl() {
        String res = call(" class A { void m() { String s = \"5 choose \" + i + \"gdgahdgs\"; };  }  ");
        System.out.println(res);

    }

}
