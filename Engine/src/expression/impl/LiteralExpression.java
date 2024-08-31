package expression.impl;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class LiteralExpression implements Expression {

    private final EffectiveValue value;
    private final CellType type;

    public LiteralExpression(Object literalValue, CellType type) {
        if (literalValue instanceof Double) {
            this.value = new EffectiveValueImpl(CellType.NUMERIC, literalValue);
            this.type = CellType.NUMERIC;
        } else if (literalValue instanceof Boolean) {
            this.value = new EffectiveValueImpl(CellType.BOOLEAN, literalValue);
            this.type = CellType.BOOLEAN;
        } else {
            this.value = new EffectiveValueImpl(CellType.STRING, literalValue);
            this.type = CellType.STRING;
        }
    }

    @Override
    public EffectiveValue eval() {
        return value;
    }

    @Override
    public CellType getExpressionType() {
        return type;
    }


}