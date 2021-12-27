package de.aschoerk.java2rust;

import java.util.*;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import com.github.javaparser.ast.Node;

/**
 * Created by aschoerk on 03.05.16.
 */
public class IdTracker {

    int tryCount;

    public IdentityHashMap<Node, Class> types = new IdentityHashMap<>();

    String packageName = null;

    private Set<String> hasThrows = new HashSet<>();

    String currentMethod = null;

    public boolean hasThrows(String name) {
        return hasThrows.contains(name);
    }

    public boolean hasThrows() {
        return currentMethod != null && hasThrows.contains(currentMethod);
    }

    public void setCurrentMethod(String name) {
        this.currentMethod = name;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    List<Import> imports = new ArrayList<>();

    public void addImport(Import i) {
        imports.add(i);
    }

    public List<Import> getImports() {
        return imports;
    }

    List<Block> blocks = new ArrayList<>();

    Stack<Block> currentBlocks = new Stack<>();

    boolean inConstructor = false;

    public void setInConstructor(final boolean inConstructor) {
        this.inConstructor = inConstructor;
    }

    public boolean isInConstructor() {
        return inConstructor;
    }

    void addChange(String name, Node n) {
        if (!currentBlocks.empty()) currentBlocks.peek().addChange(name, n);
    }

    void addDeclaration(String name, Pair<TypeDescription, Node> description) {
        if (!currentBlocks.empty()) currentBlocks.peek().addDeclaration(name, description);
    }

    void addUsage(String name, Node n) {
        if (!currentBlocks.empty())
            currentBlocks.peek().addUsage(name, n);
    }

    void pushBlock(Node n) {
        Block block;
        if (!currentBlocks.isEmpty()) {
            Block parent = currentBlocks.peek();
            if (!parent.contains(n)) throw new AssertionError();
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

    public Optional<Pair<TypeDescription, Node>> findDeclarationNodeFor(String name, Node n) {
        Optional<Block> block = findInnerMostBlock(n);
        do {
            if (block.isPresent()) {
                final Block b = block.get();
                Pair<TypeDescription, Node> descr = b.declarations.get(name);
                if (descr == null) {
                    block = b.parentBlock == null ? Optional.empty() : Optional.of(b.parentBlock);
                } else {
                    return Optional.of(descr);
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

    private HashMap<String, List<Node>> getAll(Function<Block, HashMap<String, List<Node>>> f) {
        final HashMap<String, List<Node>> res = new HashMap<>();
        blocks.stream()
                .map(f::apply)
                .forEach(u -> u.keySet().stream().forEach(
                        k -> {
                            if (res.containsKey(k)) {
                                res.get(k).addAll(u.get(k));
                            } else {
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

    private boolean isChangedInSingleBlock(String name, Block b) {
        if (b.changes.get(name) != null)
            return true;
        else
            return false;
    }

    private boolean isDeclaredInSingleBlock(String name, Block b) {
        if (b.declarations.get(name) != null)
            return true;
        else
            return false;
    }

    private boolean isChangedInChildrenOfBlock(String name, Block bP) {
        return bP.children.stream()
                .filter(child ->
                        !isDeclaredInSingleBlock(name, child)
                                && (isChangedInSingleBlock(name, child) || isChangedInChildrenOfBlock(name, child)))
                .findAny().isPresent();
    }

    public boolean isChanged(String name, Node n) {
        Optional<Block> b = findInnerMostBlock(n);
        if (b.isPresent()) {
            return isChangedInSingleBlock(name, b.get()) || isChangedInChildrenOfBlock(name, b.get());
        }
        return false;
    }

    public Map<String, List<Node>> getDeclarations() {
        final HashMap<String, List<Node>> res = new HashMap<>();
        blocks.stream()
                .map(b -> b.declarations)
                .forEach(u -> u.keySet().stream().forEach(
                        k -> {
                            if (res.containsKey(k)) {
                                res.get(k).add(u.get(k).getRight());
                            } else {
                                res.put(k, new ArrayList<>(Collections.singletonList(u.get(k).getRight())));
                            }
                        }
                ));
        return res;
    }

    public void setHasThrows(String name) {
        this.hasThrows.add(name);
    }

    public void putType(Node n, Class clazz) {
        Class existing = getType(n);
        if (existing == null)
            types.put(n, clazz);
        else {
            if (clazz.isPrimitive()) {
                if (isDiscrete(existing) && isFloat(clazz)) {  // propagate discrete to float
                    types.put(n, clazz);
                }
            }
        }
    }

    public boolean isDiscrete(final Node n) {
        if (n == null)
            return false;
        return isDiscrete(getType(n));
    }


    public boolean isFloat(final Node n) {
        if (n == null)
            return false;
        return isFloat(getType(n));
    }

    public boolean isFloat(final Class clazz) {
        if (clazz == null)
            return false;
        return clazz.equals(Float.TYPE) || clazz.equals(Double.TYPE) ||
                clazz.equals(Float.class) || clazz.equals(Double.class) || clazz.getTypeName().equals("float") || clazz.getTypeName().equals("double");
    }

    public boolean isDiscrete(final Class clazz) {
        if (clazz == null)
            return false;
        return clazz.equals(Integer.TYPE) || clazz.equals(Long.TYPE) || clazz.equals(Byte.TYPE) || clazz.equals(Short.TYPE) ||
                clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Byte.class) || clazz.equals(Short.class);
    }

    public Class getType(Node n) {
        return types.get(n);
    }

    public int incrementAndGetTryCount() {
        return ++tryCount;
    }

    public void decrementTryCount() {
        tryCount--;
    }
}
