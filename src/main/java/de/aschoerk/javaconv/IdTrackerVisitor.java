package de.aschoerk.javaconv;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Created by aschoerk on 03.05.16.
 */
public class IdTrackerVisitor extends VoidVisitorAdapter<IdTracker> {

    private boolean inAssignTarget = false;

    @Override
    public void visit(ClassOrInterfaceType n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, IdTracker arg) {
        arg.pushBlock(n);
        super.visit(n, arg);
        arg.popBlock();
    }

    private void visitComment(final Comment n, final IdTracker arg) {
        if (n != null) {
            n.accept(this, arg);
        }
    }

    @Override
    public void visit(AssignExpr n, IdTracker arg) {

        visitComment(n.getComment(), arg);
        inAssignTarget = true;
        try {
            n.getTarget().accept(this, arg);
        }
        finally {
            inAssignTarget = false;
        }
        n.getValue().accept(this, arg);

    }

    @Override
    public void visit(UnaryExpr n, IdTracker arg) {
        try {
            switch (n.getOperator()) {
                case preIncrement:
                case preDecrement:
                    inAssignTarget = true;
                    break;
                default:
                    inAssignTarget = false;
            }
            n.getExpr().accept(this, arg);
        }
        finally {
            inAssignTarget = false;
        }
    }

    @Override
    public void visit(BlockStmt n, IdTracker arg) {
        arg.pushBlock(n);
        super.visit(n, arg);
        arg.popBlock();
    }

    @Override
    public void visit(ForStmt n, IdTracker arg) {
        arg.pushBlock(n);
        super.visit(n, arg);
        arg.popBlock();
    }

    @Override
    public void visit(MethodCallExpr n, IdTracker arg) {
        arg.addUsage(n.getName(),n);
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, IdTracker arg) {
        arg.pushBlock(n);
        arg.addDeclaration(n.getName(), n);
        super.visit(n, arg);
        arg.popBlock();
    }

    @Override
    public void visit(NameExpr n, IdTracker arg) {
        if (inAssignTarget) {
            arg.addChange(n.getName(), n);
        } else {
            arg.addUsage(n.getName(), n);
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(QualifiedNameExpr n, IdTracker arg) {
        if (inAssignTarget) {
            arg.addChange(n.getName(), n);
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ReferenceType n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ReturnStmt n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(SwitchStmt n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ThisExpr n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarationExpr n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclaratorId n, IdTracker arg) {
        boolean isField = n.getParentNode().getParentNode() instanceof FieldDeclaration;
        arg.addDeclaration(n.getName(), n);
        super.visit(n, arg);
    }

    @Override
    public void visit(LambdaExpr n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(CatchClause n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ArrayAccessExpr n, IdTracker arg) {

        super.visit(n, arg);
    }

    @Override
    public void visit(FieldAccessExpr n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(FieldDeclaration n, IdTracker arg) {

        super.visit(n, arg);
    }
}
