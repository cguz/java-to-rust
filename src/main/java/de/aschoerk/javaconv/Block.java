package de.aschoerk.javaconv;

import com.github.javaparser.ast.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by aschoerk on 16.05.16.
 */
public class Block {
    static AtomicInteger blockCount = new AtomicInteger(0);
    public static final int FICTIONAL_LINE_SIZE = 10000000;
    Block parentBlock;
    List<Block> children = new ArrayList<>();
    int id;
    Node n;
    HashMap<String,Node> changes = new HashMap<>();
    HashMap<String,Node> declarations = new HashMap<>();
    HashMap<String,Node> usages = new HashMap<>();

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

    boolean contains(Node n) {
        if (n.getBeginLine() < this.n.getBeginLine()) {
            return false;
        }
        if (n.getBeginLine() == this.n.getBeginLine()) {
            if (n.getBeginColumn() < this.n.getBeginColumn()) {
                return false;
            }
        }
        if (n.getEndLine() > this.n.getEndLine()) {
            return false;
        }
        if (n.getEndLine() == this.n.getEndLine()) {
            return n.getEndColumn() > this.n.getEndColumn();
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public int size() {
        return (n.getEndLine() - n.getBeginLine()) * FICTIONAL_LINE_SIZE
                + (n.getEndColumn() + FICTIONAL_LINE_SIZE - n.getBeginColumn());
    }


}
