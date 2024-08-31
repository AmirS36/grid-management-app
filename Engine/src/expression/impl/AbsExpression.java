package expression.impl;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class AbsExpression implements Expression {

    private Expression value;

    public AbsExpression(Expression value) {
        this.value = value;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue newValue = this.value.eval();
        if (value.getExpressionType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Argument must be numeric, but it's a " + value.getExpressionType());
        }
        double result = Math.abs(newValue.extractValueWithExpectation(Double.class));
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getExpressionType() {
        return CellType.NUMERIC;
    }
}
