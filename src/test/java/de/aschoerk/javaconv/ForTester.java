package de.aschoerk.javaconv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class ForTester extends Base {


    @Test
    public void canConvertCompleteForToRust() {

        String res = call("for (int i = 10; i < 100; i++) { System.out.println(\"i: \" + i); }");
        System.out.println(res);

    }

    @Test
    public void canConvertCompleteWithOutCondToRust() {

        // for (int i = 10; ; i++) { System.out.println("i: " + i); if (i > 100) break; }
        String res = call("for (int i = 10; ; i++) { System.out.println(\"i: \" + i); if (i > 100) break; }");
        System.out.println(res);

    }

    @Test
    public void canConvertEmptyToRust() {
        // int i = 0; for (;;) { System.out.println("i: " + i); if (i > 100) break; else i++; }
        // for (int i = 10; ; i++) { System.out.println("i: " + i); if (i > 100) break; }
        String res = call("int i = 0; for (;;) { System.out.println(\"i: \" + i); if (i > 100) break; else { i++; } }");
        System.out.println(res);

    }

    @Test
    public void canConvertOnlyWithIncToRust() {
        // int i = 0; for (;;) { System.out.println("i: " + i); if (i > 100) break; else i++; }
        // for (int i = 10; ; i++) { System.out.println("i: " + i); if (i > 100) break; }
        String res = call("int i = 0; for (;;i++) { System.out.println(\"i: \" + i); if (i > 100) break;  }");
        System.out.println(res);

    }
}
