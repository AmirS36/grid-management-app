package expression.impl;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class ModExpression implements Expression {

    private Expression left;
    private Expression right;

    public ModExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = this.left.eval();
        EffectiveValue rightValue = this.right.eval();
        if (left.getExpressionType() != CellType.NUMERIC || right.getExpressionType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Arguments must be numeric, but they are " + left.getExpressionType() + " and " + right.getExpressionType());
        }

        double result = leftValue.extractValueWithExpectation(Double.class) % rightValue.extractValueWithExpectation(Double.class);
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getExpressionType() {
        return CellType.NUMERIC;
    }
}
