package expression.impl.ranges;

import expression.api.Expression;
import sheet.cell.Cell;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;
import sheet.ranges.Range;

public class SumExpression implements Expression {
    private Expression value;

    public SumExpression(Expression value) {
        this.value = value;
    }


    @Override
    public EffectiveValue eval() {
        Range currentRange = value.eval().extractValueWithExpectation(Range.class);

        if (currentRange == null) {
            return new EffectiveValueImpl(CellType.ERROR,"NaN");
        }

        double sum = 0;
        for (Cell cell : currentRange.getCells()) {
            if (cell.getEffectiveValue().getCellType() == CellType.NUMERIC) {
                sum = sum + cell.getEffectiveValue().extractValueWithExpectation(Double.class);
            }
        }
        return new EffectiveValueImpl(CellType.NUMERIC, sum);
    }

    @Override
    public CellType getExpressionType() {
        return CellType.NUMERIC;
    }
}
