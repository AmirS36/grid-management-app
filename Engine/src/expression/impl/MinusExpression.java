package expression.impl;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class MinusExpression implements Expression {

    private Expression left;
    private Expression right;

    public MinusExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval() {
        if (left.getExpressionType() != CellType.NUMERIC || right.getExpressionType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Arguments must be numeric, but they are " + left.getExpressionType() + " and " + right.getExpressionType());
        }
        EffectiveValue leftValue = this.left.eval();
        EffectiveValue rightValue = this.right.eval();
        double result = leftValue.extractValueWithExpectation(Double.class) - rightValue.extractValueWithExpectation(Double.class);
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getExpressionType() {
        return CellType.NUMERIC;
    }

}
