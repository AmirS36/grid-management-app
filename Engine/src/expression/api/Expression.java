package expression.api;

import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;

public interface Expression {
    EffectiveValue eval();
    CellType getExpressionType();
}
