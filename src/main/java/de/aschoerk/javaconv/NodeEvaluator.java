package de.aschoerk.javaconv;


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
        if (n instanceof VariableDeclaratorId) {
            if(n.getParentNode().getParentNode() instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration)n.getParentNode().getParentNode();
                if (!ModifierSet.isStatic(fd.getModifiers())) {
                    return true;
                }
            };
        }
        return false;
    }

    static boolean isNonStaticMethodDeclaration(Node n) {
       if (n instanceof MethodDeclaration) {
           if (!ModifierSet.isStatic(((MethodDeclaration)n).getModifiers()))
               return true;
       }
        return false;
    }

}
