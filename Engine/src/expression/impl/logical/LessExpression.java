package expression.impl.logical;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class LessExpression implements Expression {

    private Expression left;
    private Expression right;

    public LessExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = this.left.eval();
        EffectiveValue rightValue = this.right.eval();

        if (leftValue.getCellType() == rightValue.getCellType() &&
                leftValue.extractValueWithExpectation(Double.class) >= rightValue.extractValueWithExpectation(Double.class)) {
            return new EffectiveValueImpl(CellType.BOOLEAN, false);
        } else if (leftValue.getCellType() == rightValue.getCellType() &&
                leftValue.extractValueWithExpectation(Double.class) < rightValue.extractValueWithExpectation(Double.class)) {
            return new EffectiveValueImpl(CellType.BOOLEAN, true);
        }
        return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
    }

    @Override
    public CellType getExpressionType() {
        return CellType.BOOLEAN;
    }
}


