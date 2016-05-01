package de.aschoerk.javaconv;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by aschoerk on 01.05.16.
 */
@RunWith(JUnit4.class)
public class SnakeTest {

    private String call(String s) {
        return JavaConverter.convert2Rust(s);
    }

    @Test
    public void testMethodCall() {
        assertThat(call("xAA.xAAB()"), containsString("x_a_a.x_a_a_b"));
    }

    @Test
    public void testVarDecl() {
        assertThat(call("String xTestString;"), containsString("x_test_string"));
    }

    @Test
    public void testMethodDecl() {
        assertThat(call("String methodMIs();"), containsString("method_m_is"));
    }
}
