package de.aschoerk.javaconv;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by aschoerk on 01.05.16.
 */
@RunWith(JUnit4.class)
public class SnakeTest extends Base {



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

    @Test public void testTestAnnotation() {
        assertThat(call("@Test void testMethod() { int i; }"), containsString("#[test]"));
        assertThat(call("@Test\n void testMethod() { int a; }"), containsString("#[test]"));
        assertThat(call("@Test(expected = Exception.class)\n void testMethod() { int b; }"), containsString("#[test]"));
    }

    @Ignore
    @Test
    public void testArrayConv() {
        assertThat(call("final int tmp[][] = new int[2][4];"), containsString("let tmp: [[i32; 4]; 2] = [[0; 4]; 2];"));
        assertThat(call("final double tmp[][] = new double[2][4];"), containsString("let tmp: [[f64; 4]; 2] = [[0.0; 4]; 2];"));
        assertThat(call("final Double tmp[][] = new Double[2][4];"), containsString("let tmp: [[Option<Double>; 4]; 2] = [[None; 4]; 2];"));
        assertThat(call("final Double tmp[] = {0.1, 0.2};"), containsString("let tmp: [Double; 2] = [0.1, 0.2, ]"));
    }

}
