package expression.impl.string;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class ConcatExpression implements Expression {

    private Expression left;
    private Expression right;

    public ConcatExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = this.left.eval();
        EffectiveValue rightValue = this.right.eval();
        if (left.getExpressionType() != CellType.STRING || right.getExpressionType() != CellType.STRING) {
            // throw new IllegalArgumentException("Arguments must be strings, but they are " + left.getExpressionType() + " and " + right.getExpressionType());
            return new EffectiveValueImpl(CellType.ERROR, "!UNDEFINED!");
        }

        String result = leftValue.extractValueWithExpectation(String.class) + rightValue.extractValueWithExpectation(String.class);
        return new EffectiveValueImpl(CellType.STRING, result);
    }

    @Override
    public CellType getExpressionType() {
        return CellType.STRING;
    }
}

