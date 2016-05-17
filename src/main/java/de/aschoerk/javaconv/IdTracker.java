package de.aschoerk.javaconv;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

/**
 * Created by aschoerk on 03.05.16.
 */
public class IdTracker {

    HashMap<Integer,Block> blocks = new HashMap<>();

    int blockCount = 0;

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
        Block block = currentBlocks.size() > 0 ? new Block(currentBlocks.peek(), n) : new Block(n);
        currentBlocks.push(block);
        blocks.put(block.getId(), block);
    }

    void popBlock() {
        currentBlocks.pop();
    }



    Optional<Block> findInnerMostBlock(Node n) {
        return blocks.values().stream()
                .filter(block -> block.contains(n))
                .sorted((block1, block2) ->
                        ((Integer)block2.size()).compareTo(block1.size()))
                .findFirst();
    }

    boolean willBeChanged(String name, Node n) {

        throw new RuntimeException("not implemented");
    }

    boolean isLocalTo(String name, Node n) {

        throw new RuntimeException("not implemented");
    }
}
