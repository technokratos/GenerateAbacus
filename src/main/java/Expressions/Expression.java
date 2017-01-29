package Expressions;

import org.jaxen.expr.Expr;

/**
 * Created by dbk on 04-Oct-16.
 */
public class Expression {

    public static Expression or(Expression... e){
        return e[0];
    }
    public static Expression and(Expression e) {
        return e;
    }
    public static Expression equal(Expression e) {
        return e;
    }
    public static Expression constant(Boolean var) {
        return new Expression();
    }
    public static Expression False() {
        return new Expression();
    }
    public static Expression eval() {
        return or(and(or(False(), False(), and(False()))));
    }
}
