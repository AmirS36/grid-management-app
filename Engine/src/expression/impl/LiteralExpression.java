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
            this.type = type;
        }
        else if (literalValue.equals("TRUE")) {
            this.value = new EffectiveValueImpl(CellType.BOOLEAN, true);
            this.type = type;
        }
        else if (literalValue.equals("FALSE")) {
            this.value = new EffectiveValueImpl(CellType.BOOLEAN, false);
            this.type = type;
        }
        else {
            this.value = new EffectiveValueImpl(type, literalValue);
            this.type = type;
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