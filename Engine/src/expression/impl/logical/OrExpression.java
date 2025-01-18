package expression.impl.logical;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class OrExpression implements Expression {

    private final Expression left;
    private final Expression right;

    public OrExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = this.left.eval();
        EffectiveValue rightValue = this.right.eval();

        if(leftValue.getCellType() != CellType.BOOLEAN || rightValue.getCellType() != CellType.BOOLEAN) {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }
        else if (leftValue.extractValueWithExpectation(Boolean.class) || rightValue.extractValueWithExpectation(Boolean.class)) {
            return new EffectiveValueImpl(CellType.BOOLEAN, true);
        }
        return new EffectiveValueImpl(CellType.BOOLEAN, false);

    }

    @Override
    public CellType getExpressionType() {
        return CellType.BOOLEAN;
    }
}
