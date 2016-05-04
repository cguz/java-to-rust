package de.aschoerk.javaconv;

import java.util.HashMap;
import java.util.Stack;

import com.github.javaparser.ast.Node;

/**
 * Created by aschoerk on 03.05.16.
 */
public class IdTracker {

    HashMap<Integer,Block> blocks = new HashMap<>();

    static int blockCount = 0;

    class Block {
        Block parentBlock;
        int id;
        Node n;
        HashMap<String,Node> changes = new HashMap<>();
        HashMap<String,Node> declarations = new HashMap<>();
        HashMap<String,Node> usages = new HashMap<>();

        public Block(Node n) {
            if (!currentBlocks.empty())
                this.parentBlock = currentBlocks.peek();
            this.n = n;
            this.id = blockCount++;
        }

        public int getId() {
            return id;
        }


    }

    Stack<Block> currentBlocks = new Stack<>();

    void addChange(String name, Node n) {
        currentBlocks.peek().changes.put(name, n);
    }

    void addDeclaration(String name, Node n) {
        currentBlocks.peek().declarations.put(name, n);
    }

    void addUsage(String name, Node n) {
        currentBlocks.peek().usages.put(name, n);
    }

    void pushBlock(Node n) {
        Block block = new Block(n);
        currentBlocks.push(block);
        blocks.put(block.getId(), block);
    }

    void popBlock() {
        currentBlocks.pop();
    }
}
