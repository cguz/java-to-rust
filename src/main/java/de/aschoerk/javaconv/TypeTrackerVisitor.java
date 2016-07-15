package de.aschoerk.javaconv;

import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralMinValueExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralMinValueExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * @author aschoerk
 */
public class TypeTrackerVisitor extends VoidVisitorAdapter<Object> {

    IdTracker idTracker;


    private void visitComment(final Comment n, final Object arg) {
        if (n != null) {
            n.accept(this, arg);
        }
    }

    public TypeTrackerVisitor(final IdTracker idTracker) {
        this.idTracker = idTracker;
    }

    @Override
    public void visit(final AssignExpr n, final Object arg) {
        int i = 5;
        super.visit(n, arg);
    }

    private void propagateIntBool(final BinaryExpr destination, final Expression left, final Expression right) {
        Class leftClazz = idTracker.getType(left);
        Class rightClazz = idTracker.getType(right);
        if (Boolean.TYPE.equals(leftClazz) || Boolean.TYPE.equals(rightClazz))
            idTracker.putType(destination, Boolean.TYPE);
        else
            idTracker.putType(destination, Integer.TYPE);
    }



    public void propagateTypes(Node destination, Node left, Node right) {
        Class leftClazz = idTracker.getType(left);
        Class rightClazz = idTracker.getType(right);
        if (String.class.equals(leftClazz) || String.class.equals(rightClazz))
            idTracker.putType(destination, String.class);
        else
        if (Double.TYPE.equals(leftClazz) || Double.TYPE.equals(rightClazz) || Double.class.equals(leftClazz) || Double.class.equals(rightClazz)
                || Float.TYPE.equals(leftClazz) || Float.TYPE.equals(rightClazz) || Float.class.equals(leftClazz) || Float.class.equals(rightClazz)
                )
            idTracker.putType(destination, Double.TYPE);
        else
        if (Boolean.TYPE.equals(leftClazz) || Boolean.TYPE.equals(rightClazz))
            idTracker.putType(destination, Boolean.TYPE);
        else if (Integer.TYPE.equals(leftClazz) || Integer.TYPE.equals(rightClazz)
                || Short.TYPE.equals(leftClazz) || Short.TYPE.equals(rightClazz)
                || Long.TYPE.equals(leftClazz) || Long.TYPE.equals(rightClazz)
                || Byte.TYPE.equals(leftClazz) || Byte.TYPE.equals(rightClazz)
                || Integer.class.equals(leftClazz) || Integer.class.equals(rightClazz)
                 || Short.class.equals(leftClazz) || Short.class.equals(rightClazz)
                 || Long.class.equals(leftClazz) || Long.class.equals(rightClazz)
                 || Byte.class.equals(leftClazz) || Byte.class.equals(rightClazz))
            idTracker.putType(destination, Integer.TYPE);
    }

    @Override
    public void visit(final BinaryExpr n, final Object arg) {
        visitComment(n.getComment(), arg);
        n.getLeft().accept(this, arg);
        n.getRight().accept(this, arg);
        switch (n.getOperator()) {
            case equals:
            case notEquals:
            case and:
            case or:
            case less:
            case greater:
            case lessEquals:
            case greaterEquals:
                idTracker.putType(n,Boolean.TYPE);
                break;
            case binOr:
            case binAnd:
            case xor:
                propagateIntBool(n, n.getLeft(), n.getRight());
                break;
            case lShift:
            case rSignedShift:
            case rUnsignedShift:
                idTracker.putType(n,Integer.TYPE);
                break;
            case plus:
            case minus:
            case times:
            case divide:
            case remainder:
                propagateTypes(n, n.getLeft(), n.getRight());
                break;
        }
    }

    @Override
    public void visit(final IntegerLiteralExpr n, final Object arg) {
        idTracker.putType(n,Integer.TYPE);
        super.visit(n, arg);
    }

    @Override
    public void visit(final IntegerLiteralMinValueExpr n, final Object arg) {
        idTracker.putType(n,Integer.TYPE);
        super.visit(n, arg);
    }

    @Override
    public void visit(final LongLiteralExpr n, final Object arg) {
        idTracker.putType(n,Integer.TYPE);
        super.visit(n, arg);
    }

    @Override
    public void visit(final LongLiteralMinValueExpr n, final Object arg) {
        idTracker.putType(n,Integer.TYPE);
        super.visit(n, arg);
    }


    @Override
    public void visit(final NameExpr n, final Object arg) {
        Optional<Pair<TypeDescription, Node>> b = idTracker.findDeclarationNodeFor(n.getName(), n);
        if (b.isPresent()) {
            if (b.get().getLeft() != null)
                idTracker.putType(n, b.get().getLeft().getClazz());
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(final NullLiteralExpr n, final Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(final QualifiedNameExpr n, final Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(final SuperExpr n, final Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(final StringLiteralExpr n, final Object arg) {
        idTracker.putType(n,String.class);
        super.visit(n, arg);
    }

    @Override
    public void visit(final UnaryExpr n, final Object arg) {
        switch (n.getOperator()) {
            case positive:
            case negative:
                propagateTypes(n, n.getExpr(), n.getExpr());
                break;
            case not:
                idTracker.putType(n, Boolean.TYPE);
                break;
            case inverse:
            case posIncrement:
            case posDecrement:
            case preIncrement:
            case preDecrement:
                idTracker.putType(n, Integer.TYPE);
                break;
            default:
                ;
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(final ThisExpr n, final Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(final VariableDeclarationExpr n, final Object arg) {

        super.visit(n, arg);
    }

    @Override
    public void visit(final BooleanLiteralExpr n, final Object arg) {
        idTracker.putType(n,Boolean.TYPE);
        super.visit(n, arg);
    }

    @Override
    public void visit(final ArrayAccessExpr n, final Object arg) {
        if (n.getName() instanceof NameExpr) {
            NameExpr ne = (NameExpr)n.getName();
            Optional<Pair<TypeDescription, Node>> b = idTracker.findDeclarationNodeFor(ne.getName(), ne);
            if (b.isPresent()) {
                if (b.get().getLeft() != null)
                    idTracker.putType(n, b.get().getLeft().getClazz());
            }
        }
        idTracker.putType(n.getIndex(), Integer.TYPE);
        if (n.getIndex() instanceof NameExpr) {
            NameExpr ne = (NameExpr)n.getIndex();
            Optional<Pair<TypeDescription, Node>> b = idTracker.findDeclarationNodeFor(ne.getName(), ne);
            if (b.isPresent()) {
                if (b.get().getLeft() != null)
                    b.get().getLeft().clazz = Integer.TYPE;
            }
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(final ArrayInitializerExpr n, final Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(final CharLiteralExpr n, final Object arg) {
        idTracker.putType(n,Character.TYPE);
        super.visit(n, arg);
    }

    @Override
    public void visit(final ConditionalExpr n, final Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(final DoubleLiteralExpr n, final Object arg) {
        idTracker.putType(n,Double.TYPE);
        super.visit(n, arg);
    }

    @Override
    public void visit(final MethodCallExpr n, final Object arg) {
        visitComment(n.getComment(), arg);
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
        }
        if (n.getTypeArgs() != null) {
            for (final Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
        }
        if (n.getArgs() != null) {
            for (final Expression e : n.getArgs()) {
                e.accept(this, arg);
            }
        }
    }
}
