package expression.impl.ranges;

import expression.api.Expression;
import sheet.cell.Cell;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;
import sheet.ranges.Range;

public class AverageExpression implements Expression {
    private Expression value;

    public AverageExpression(Expression value) {
        this.value = value;
    }

    @Override
    public EffectiveValue eval() {
        Range currentRange = value.eval().extractValueWithExpectation(Range.class);

        if (currentRange == null) {
            return new EffectiveValueImpl(CellType.ERROR, "NaN");
        }

        double sum = 0;
        int counter = 0;

        for (Cell cell : currentRange.getCells()) {
            EffectiveValue effectiveValue = cell.getEffectiveValue();
            if (effectiveValue.getCellType() == CellType.NUMERIC) {
                sum = sum + effectiveValue.extractValueWithExpectation(Double.class);
                counter++;
            }
        }
        if (counter >= 0) {
            return new EffectiveValueImpl(CellType.NUMERIC, sum/counter);
        }
        return new EffectiveValueImpl(CellType.ERROR, "NaN");
    }


    @Override
    public CellType getExpressionType() {
        return CellType.NUMERIC;
    }
}