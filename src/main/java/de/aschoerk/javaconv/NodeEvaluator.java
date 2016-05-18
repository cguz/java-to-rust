package de.aschoerk.javaconv;


import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclaratorId;

/**
 * Created by aschoerk on 17.05.16.
 */
public class NodeEvaluator {

    static boolean isFieldDeclaration(Node n) {
        return n instanceof VariableDeclaratorId && n.getParentNode().getParentNode() instanceof FieldDeclaration;
    }

    static boolean isMethodDeclaration(Node n) {
        return n instanceof MethodDeclaration;
    }

}
