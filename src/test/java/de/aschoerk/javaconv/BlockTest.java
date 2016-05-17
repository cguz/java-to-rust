package de.aschoerk.javaconv;

import com.github.javaparser.ast.expr.NameExpr;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;

/**
 * Created by aschoerk on 16.05.16.
 */
@RunWith(JUnit4.class)
public class BlockTest {
    class NodeBuilder<T extends NodeBuilder> {
        protected int beginLine = 0;
        protected int beginColumn = 0;
        protected int endLine = 0;
        protected int endColumn = 0;
        public T bL(int line) {
            beginLine = line;
            return (T)this;
        }
        public T bC(int column) {
            beginColumn = column;
            return (T)this;
        }
        public T eL(int line) {
            endLine = line;
            return (T)this;
        }
        public T eC(int column) {
            endColumn= column;
            return (T)this;
        }

    }

    class NameExprBuilder extends NodeBuilder<NameExprBuilder> {
        private String name = "default";

        NameExprBuilder n(String name) {
            this.name = name;
            return this;
        }

        NameExpr build() {
            return new NameExpr(beginLine, beginColumn, endLine, endColumn, name);
        }
    }


    private NameExprBuilder getNameExprBuilderL1C1L10C120() {
        return new NameExprBuilder()
                .bL(1)
                .bC(1)
                .eL(10)
                .eC(120);
    }
    
    @Test
    public void selfContainsSelf() {
        NameExpr testNode = getNameExprBuilderL1C1L10C120().build();
        Block testBlock = new Block(testNode);
        assertTrue(testBlock.contains(testNode));
        assertTrue(testBlock.contains(getNameExprBuilderL1C1L10C120().bC(testNode.getBeginColumn()+1).build()));
        assertTrue(!testBlock.contains(getNameExprBuilderL1C1L10C120().bC(testNode.getBeginColumn()-1).build()));
        assertTrue(!testBlock.contains(getNameExprBuilderL1C1L10C120().bL(testNode.getBeginLine()-1).build()));
        assertTrue(testBlock.contains(getNameExprBuilderL1C1L10C120().bL(testNode.getBeginLine()+1).build()));
        assertTrue(testBlock.contains(getNameExprBuilderL1C1L10C120().eC(testNode.getEndColumn()-1).build()));
        assertTrue(!testBlock.contains(getNameExprBuilderL1C1L10C120().eC(testNode.getEndColumn()+1).build()));
        assertTrue(!testBlock.contains(getNameExprBuilderL1C1L10C120().eL(testNode.getEndLine()+1).build()));
        assertTrue(testBlock.contains(getNameExprBuilderL1C1L10C120().eL(testNode.getEndLine()-1).build()));
    }
}
