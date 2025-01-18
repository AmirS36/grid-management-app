package expression.impl.string;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class SubExpression implements Expression {

    private Expression source;
    private Expression startIndex;
    private Expression endIndex;

    public SubExpression(Expression source, Expression startIndex, Expression endIndex) {
        this.source = source;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue sourceValue = source.eval();
        EffectiveValue startIndexValue = startIndex.eval();
        EffectiveValue endIndexValue = endIndex.eval();

        if (source.getExpressionType() != CellType.STRING || startIndex.getExpressionType() != CellType.NUMERIC || endIndex.getExpressionType() != CellType.NUMERIC) {
           // throw new IllegalArgumentException("Arguments types must be in this order: STRING, NUMERIC, NUMERIC. " +
                   // "Instead they are " + source.getExpressionType() + ", " + startIndex.getExpressionType()+ ", " + endIndex.getExpressionType());
            return new EffectiveValueImpl(CellType.ERROR, "!UNDEFINED!");
        }

        // Extracting the values
        String originalString = sourceValue.extractValueWithExpectation(String.class);
        int start = startIndexValue.extractValueWithExpectation(Double.class).intValue();
        int end = endIndexValue.extractValueWithExpectation(Double.class).intValue();

        // Boundary checks for substring operation
        if (start < 0 || end > originalString.length() || start > end) {
            return new EffectiveValueImpl(CellType.ERROR, "!UNDEFINED!");
        }

        // Perform substring operation
        String result = originalString.substring(start, end);

        return new EffectiveValueImpl(CellType.STRING, result);
    }

    @Override
    public CellType getExpressionType() {
        return CellType.STRING;
    }
}
