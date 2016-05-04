package de.aschoerk.javaconv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

/**
 * Created by aschoerk on 03.05.16.
 */
@RunWith(JUnit4.class)
public class IdTrackerTest {

    private IdTracker callIt(String sourceCode) throws ParseException {
        CompilationUnit cu = PartParser.createCompilationUnit(sourceCode);
        IdTrackerVisitor idTrackerVisitor = new IdTrackerVisitor();
        IdTracker idTracker = new IdTracker();
        idTrackerVisitor.visit(cu,idTracker);
        return idTracker;
    }

    @Test
    public void testLocalVariableDeclaration() throws ParseException {
        IdTracker res = callIt("class A { void method() { int lv; } }");
    }

    @Test
    public void testInstanceVariableDeclaration() throws ParseException {
        IdTracker res = callIt("class A { int iv; void method() { } }");
    }

    @Test
    public void testParameterDeclaration() throws ParseException {
        IdTracker res = callIt("class A { void method(int pi) { } }");
    }

    @Test
    public void testParameterUsage() throws ParseException {
        IdTracker res = callIt("class A { void method(int pi) { System.out.println(pi); } }");
    }

    @Test
    public void testInstanceVariableUsage() throws ParseException {
        IdTracker res = callIt("class A { int iv; void method() { System.out.println(iv); } }");
    }
    @Test
    public void testLocalVariableUsage() throws ParseException {
        IdTracker res = callIt("class A { void method() { int lv = 1; System.out.println(lv); } }");
    }

}
