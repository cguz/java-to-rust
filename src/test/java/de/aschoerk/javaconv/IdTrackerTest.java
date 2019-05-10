package de.aschoerk.javaconv;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

/**
 * Created by aschoerk on 03.05.16.
 */
@Ignore
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
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3));  // A, method, lv
    }

    @Test
    public void testInstanceVariableDeclaration() throws ParseException {
        IdTracker res = callIt("class A { int iv; void method() { } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(0));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3)); // A, iv, method
    }

    @Test
    public void testParameterDeclaration() throws ParseException {
        IdTracker res = callIt("class A { void method(int pi) { } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(0));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3));
    }

    @Test
    public void testParameterUsage() throws ParseException {
        IdTracker res = callIt("class A { void method(int pi) { System.out.println(pi); } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(3));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3));  // A, method, pi
        checkUsages(res);
    }

    @Test
    public void testInstanceVariableUsage() throws ParseException {
        IdTracker res = callIt("class A { int iv; void method() { System.out.println(iv); } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(3));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3));  // A, iv, method
        checkUsages(res);
    }

    @Test
    public void test() throws ParseException {
        String testString = "String.format(\"IR Seeker: %3.0f%% signal at %6.1f degrees\", "
                            + "getStrength() * 100.0d, getAngle())";
        IdTracker res = callIt(testString);
        assertThat(res.getUsages().values(), Matchers.hasSize(3));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3));  // A, iv, method
        checkUsages(res);
    }

    private void checkUsages(IdTracker res) {
        res.getUsages().entrySet().stream().forEach(e -> {
            e.getValue().forEach(n ->
                    res.findDeclarationNodeFor(e.getKey(), n)
            );

        }
        );
    }

    @Test
    public void testLocalVariableUsage() throws ParseException {
        IdTracker res = callIt("class A { void method() { int lv = 1; System.out.println(lv); } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(3));  // lv, System, println
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3)); //A, method, lv
        checkUsages(res);
    }


    @Ignore
    @Test
    public void testAssignment() throws ParseException {
        IdTracker res = callIt("class A { void method() { int lv; lv = 10; lv++; lv += 100; } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(1));
        assertThat(res.getChanges().values(), Matchers.hasSize(1));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3)); // A,method, lv
        checkUsages(res);
    }

    @Test
    public void testMultMethods() throws ParseException {
        IdTracker res = callIt("class A { void method() { int lv; } void method(int i) { int lv; } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(0));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(4)); // A, method, lv, i, lv
        checkUsages(res);
    }

    @Test
    public void testMultConstructors() throws ParseException {
        IdTracker res = callIt("class A { A() { int lv; } A(int i) { int lv; } }");
        assertThat(res.getUsages().values(), Matchers.hasSize(0));
        assertThat(res.getChanges().values(), Matchers.hasSize(0));
        assertThat(res.getDeclarations().values(), Matchers.hasSize(3));
        checkUsages(res);
    }
}
