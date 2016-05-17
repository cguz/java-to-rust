package de.aschoerk.javaconv;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

/**
 * Created by aschoerk on 03.05.16.
 */
@RunWith(JUnit4.class)
public class IdTrackerTest {

    private IdTracker callIt(String sourceCode) throws ParseException {
        CompilationUnit cu = PartParser.createCompilationUnit(sourceCode);
        IdTrackerVisitor idTrackerVisitor = new IdTrackerVisitor();
        IdTracker idTracker = new IdTracker();
        idTrackerVisitor.visit(cu, idTracker);
        return idTracker;
    }

    void checkDeclarationsFor(IdTracker idTracker) {
        Map<String, List<Node>> usages = idTracker.getUsages();
        Map<String, List<Node>> decls = idTracker.getDeclarations();
        Map<String, List<Node>> changes = idTracker.getChanges();

    }

    @Test
    public void testLocalVariableDeclaration() throws ParseException {
        IdTracker res = callIt("class A {\n void method() {\n int lv; } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(0));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(1));
    }

    @Test
    public void testInstanceVariableDeclaration() throws ParseException {
        IdTracker res = callIt("class A { int iv; void method() { } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(0));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(1));
    }

    @Test
    public void testParameterDeclaration() throws ParseException {
        IdTracker res = callIt("class A { void method(int pi) { } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(0));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(1));
    }

    @Test
    public void testParameterUsage() throws ParseException {
        IdTracker res = callIt("class A { void method(int pi) { System.out.println(pi); } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(1));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(1));
    }

    @Test
    public void testInstanceVariableUsage() throws ParseException {
        IdTracker res = callIt("class A { int iv; void method() { System.out.println(iv); } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(1));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(1));
    }
    @Test
    public void testLocalVariableUsage() throws ParseException {
        IdTracker res = callIt("class A { void method() { int lv = 1; System.out.println(lv); } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(1));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(1));
    }

    @Test
    public void testAssignMent() throws ParseException {
        IdTracker res = callIt("class A { void method() { int lv; lv = 10; lv++; lv += 100; } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(1));
        assertThat(res.getChanges().values(), Matchers.hasSize(3));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(1));
    }
}
