package de.aschoerk.java2rust.codegen;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.VariableDeclaratorId;

/**
 * Created by aschoerk on 17.05.16.
 */
public class NodeEvaluator {

    static boolean isNonStaticFieldDeclaration(Node n) {
        if (n instanceof VariableDeclaratorId && n.getParentNode().getParentNode() instanceof FieldDeclaration) {
            FieldDeclaration fd = (FieldDeclaration) n.getParentNode().getParentNode();
            return !ModifierSet.isStatic(fd.getModifiers());
        }
        return false;
    }

    static boolean isNonStaticMethodDeclaration(Node n) {
        return n instanceof MethodDeclaration && !ModifierSet.isStatic(((MethodDeclaration) n).getModifiers());
    }
}
