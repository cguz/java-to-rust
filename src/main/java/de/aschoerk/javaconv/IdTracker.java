package de.aschoerk.javaconv;

import java.util.*;
import java.util.function.Function;

import com.github.javaparser.ast.Node;

/**
 * Created by aschoerk on 03.05.16.
 */
public class IdTracker {

    List<Block> blocks = new ArrayList<>();

    Stack<Block> currentBlocks = new Stack<>();

    void addChange(String name, Node n) {
        currentBlocks.peek().addChange(name, n);
    }

    void addDeclaration(String name, Node n) {
        currentBlocks.peek().addDeclaration(name, n);
    }

    void addUsage(String name, Node n) {
        currentBlocks.peek().addUsage(name, n);
    }

    void pushBlock(Node n) {
        Block block;
        if (!currentBlocks.isEmpty()) {
            Block parent = currentBlocks.peek();
            if(!parent.contains(n)) throw new AssertionError();
            block = new Block(parent, n);
        } else {
            block = new Block(n);
        }
        currentBlocks.push(block);
        blocks.add(block);
    }

    void popBlock() {
        currentBlocks.pop();
        if (currentBlocks.isEmpty()) {
            checkBlockStructure();
        }
    }

    Optional<Block> findRoot() {
        return blocks.stream()
                .sorted((block1, block2) ->
                        ((Long) block2.size()).compareTo(block1.size()))
                .findFirst();
    }


    Optional<Block> findInnerMostBlock(Node n) {
        return blocks.stream()
                .filter(block -> block.contains(n))
                .sorted((block1, block2) ->
                        ((Long) block1.size()).compareTo(block2.size()))
                .findFirst();
    }

    boolean willBeChanged(String name, Node n) {


        throw new RuntimeException("not implemented");
    }

    boolean isLocalTo(String name, Node n) {

        throw new RuntimeException("not implemented");
    }

    Optional<Node> findDeclarationBlockFor(String name, Node n) {
        Optional<Block> block = findInnerMostBlock(n);
        do {
            if (block.isPresent()) {
                final Block b = block.get();
                Node node = b.declarations.get(name);
                if (node == null) {
                    block = b.parentBlock == null ? Optional.empty() : Optional.of(b.parentBlock);
                } else {
                    return Optional.of(node);
                }
            } else {
                return Optional.empty();
            }
        } while (true);
    }

    String checkBlockStructure() {
        StringBuilder sb = new StringBuilder();
        if (!currentBlocks.isEmpty()) {
            sb.append("Blockstack is not empty\n");
        }
        Optional<Block> root = findRoot();
        if (!root.isPresent()) {
            sb.append("No Blockroot descernable\n");
        } else {
            if (root.get().getId() != 1) {
                sb.append("Expected Blockroot to have Id 1\n");
            }
        }
        if (blocks.stream().filter(b -> !b.disjunctChildren()).findAny().isPresent()) {
            sb.append("Found children which are not disjunct\n");
        }
        return sb.toString();
    }

    private HashMap<String, List<Node>>  getAll(Function<Block, HashMap<String, List<Node>> > f) {
        final HashMap<String, List<Node>> res = new HashMap<>();
        blocks.stream()
                .map(f::apply)
                .forEach(u -> u.keySet().stream().forEach(
                        k -> {
                            if (res.containsKey(k)) {
                                res.get(k).addAll(u.get(k));
                            } else  {
                                res.put(k, u.get(k));
                            }
                        }
                ));
        return res;
    }

    public Map<String, List<Node>> getUsages() {
        return getAll(b -> b.usages);
    }

    public Map<String, List<Node>> getChanges() {
        return getAll(b -> b.changes);
    }

    public Map<String, List<Node>> getDeclarations() {
        final HashMap<String, List<Node>> res = new HashMap<>();
        blocks.stream()
                .map(b -> b.declarations)
                .forEach(u -> u.keySet().stream().forEach(
                        k -> {
                            if (res.containsKey(k)) {
                                res.get(k).add(u.get(k));
                            } else  {
                                res.put(k, Collections.singletonList(u.get(k)));
                            }
                        }
                ));
        return res;
    }

}
