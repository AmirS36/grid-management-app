package expression.impl.logical;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class NotExpression implements Expression {

    private Expression value;

    public NotExpression(Expression value) {
        this.value = value;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue newValue = this.value.eval();

        if (value.getExpressionType() != CellType.BOOLEAN) {
            // throw new IllegalArgumentException("Argument must be boolean, but it's a " + value.getExpressionType());
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        if (newValue.extractValueWithExpectation(Boolean.class)) {
            return new EffectiveValueImpl(CellType.BOOLEAN, false);
        }
        return new EffectiveValueImpl(CellType.BOOLEAN, true);
    }

    @Override
    public CellType getExpressionType() {
        return CellType.NUMERIC;
    }
}