package expression.impl.other;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class IfExpression implements Expression {

    private Expression condition;
    private Expression then;
    private Expression _else;

    public IfExpression(Expression condition, Expression then, Expression _else) {
        this.condition = condition;
        this.then = then;
        this._else = _else;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue conditionValue = condition.eval();
        EffectiveValue thenValue = then.eval();
        EffectiveValue elseValue = _else.eval();

        if (condition.getExpressionType() != CellType.BOOLEAN || then.getExpressionType() != _else.getExpressionType()) {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        // Extracting the values
        // DISASTER POTENTIAL
        if (conditionValue.extractValueWithExpectation(Boolean.class)) {
            return thenValue;
        }

        return elseValue;
    }

    @Override
    public CellType getExpressionType() {
        return eval().getCellType();
    }
}


