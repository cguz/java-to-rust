package de.aschoerk.javaconv;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by aschoerk on 15.05.16.
 */
@RunWith(JUnit4.class)
public class DeclarationsTest extends Base {
    @Test
    public void canConvertFieldDeclaration() {
        assertThat(call("class A { int i; }"), containsString("i: i32"));
        assertThat(call("class A { int i = 1; }"), containsString("i: i32 = 1"));
    }

    @Test
    public void canConvertMethodParameter() {
        assertThat(call("class A { void m(int i) { }; }"), containsString("i: i32"));
    }

    @Test
    public void canConvertVariableDeclaration() {
        assertThat(call("class A { void m() { int i; }; }"), containsString("i: i32"));
        assertThat(call("class A { void m() { int i = 2; }; }"), containsString("i: i32 = 2;"));
    }

    @Ignore
    @Test
    public void canConvertArrayDeclaration() {
        int[] a;
        int b[] = { 1, 2};
        int c[][] = new int[1][2];
        int d[][] = { {1 , 4, 5, 6}, { 1, 6 }};

        assertThat(call("int[] a;"), containsString("let a: i32[];"));   // no reasonable conversion possible here
        assertThat(call("int b[] = { 1, 2};"), containsString("let b: [i32; 2] = [1, 2, ]"));
        assertThat(call("int c[][] = new int[1][2];"), containsString("let c: [[i32; 2]; 1] = [[0; 2]; 1];"));
        assertThat(call("int d[][] = { {1 , 4, 5, 6}, { 1, 6 }};"), containsString("let d: [[i32; 4]; 2] = [[1, 4, 5, 6, ]\n" +
                "    , [1, 6, ]\n" +
                "    , ]\n" +
                "    ;"));

    }

    @Test
    public void putSelfAsParam() {
        assertThat(call("void method() { }"), containsString("method(&self)"));
        assertThat(call("static void staticMethod() { }"), containsString("static_method()"));
    }

    @Test
    public void enumDeclarationCreatesNewBlock() {
        assertThat(call("class X {\n" +
                " enum A { AA; private final int id; }\n" +
                " enum B { BB; private final int id; }\n" +
                "}"), containsString("let id: i32"));
    }

}
