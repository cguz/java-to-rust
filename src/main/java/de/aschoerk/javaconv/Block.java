package de.aschoerk.javaconv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

import com.github.javaparser.ast.Node;

/**
 * Created by aschoerk on 16.05.16.
 */
public class Block {
    public static final long FICTIONAL_LINE_SIZE = 10000000L;
    AtomicInteger blockCount = new AtomicInteger(0);
    Block parentBlock;
    List<Block> children = new ArrayList<>();
    int id;
    Node n;
    HashMap<String, List<Node>> changes = new HashMap<>();
    HashMap<String, Pair<TypeDescription,Node>> declarations = new HashMap<>();
    HashMap<String, List<Node>> usages = new HashMap<>();



    public Block(IdTracker idTracker, Node n) {
        if (!idTracker.currentBlocks.empty()) {
            this.parentBlock = idTracker.currentBlocks.peek();
            this.parentBlock.children.add(this);
        }
        this.n = n;
        this.id = blockCount.incrementAndGet();
    }

    public Block(Block parent, Node n) {
        this(n);
        this.parentBlock = parent;
        parent.children.add(this);
    }

    public Block(Node n) {
        this.n = n;
        this.id = blockCount.incrementAndGet();
    }

    private void add(String name, Node node, HashMap<String, List<Node>> map) {
        List<Node> value = map.get(name);
        if (value == null) {
            map.put(name, new ArrayList<Node>() {{ add(node); }} );
        } else {
            value.add(node);
        }
    }

    public void addChange(String name, Node node) {
        add(name, node, changes);
    }

    public void addUsage(String name, Node node) {
        add(name, node, usages);
    }

    public void addDeclaration(String name, Pair<TypeDescription,Node> description) {
        if (declarations.get(name) != null) {
            throw new RuntimeException("expected declarations to be added only once: " + description.getRight() + " at " + description.getRight().getRange()
                    + ", already in " + declarations.get(name).getRight().getRange());
        }
        declarations.put(name, description);
    }

    boolean contains(Block b) {
        return contains(b.n);
    }

    boolean contains(Node nP) {
        if (nP.getBeginLine() < this.n.getBeginLine()) {
            return false;
        }
        if (nP.getBeginLine() == this.n.getBeginLine()) {
            if (nP.getBeginColumn() < this.n.getBeginColumn()) {
                return false;
            }
        }
        if (nP.getEndLine() > this.n.getEndLine()) {
            return false;
        }
        if (nP.getEndLine() == this.n.getEndLine()) {
            return nP.getEndColumn() <= this.n.getEndColumn();
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public long size() {
        return (n.getEndLine() - n.getBeginLine()) * FICTIONAL_LINE_SIZE
                + (n.getEndColumn() + FICTIONAL_LINE_SIZE - n.getBeginColumn());
    }

    public boolean disjunctChildren() {
        return !IntStream.range(0, children.size())
                // find blocks whose children overlap.
                .filter(i1 ->
                        // following children overlap with this one ??
                        IntStream.range(i1 + 1, children.size())
                                .filter(i2 -> children.get(i1).contains(children.get(i2))).findAny().isPresent()
                )
                .findAny().isPresent();
    }

    @Override
    public String toString() {
        return "Block[id=" + id + ',' + n + ']';
    }

}
