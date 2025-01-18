package expression.impl.numerical;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class PercentExpression implements Expression {

    private Expression left;
    private Expression right;

    public PercentExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = this.left.eval();
        EffectiveValue rightValue = this.right.eval();

        if (left.getExpressionType() != CellType.NUMERIC || right.getExpressionType() != CellType.NUMERIC) {
            // throw new IllegalArgumentException("Arguments must be numeric, but they are " + left.getExpressionType() + " and " + right.getExpressionType());
            return new EffectiveValueImpl(CellType.ERROR, "NaN");
        }

        double result = leftValue.extractValueWithExpectation(Double.class) * rightValue.extractValueWithExpectation(Double.class) / 100;
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getExpressionType() {
        return CellType.NUMERIC;
    }
}
