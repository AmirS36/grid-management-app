package expression.impl.other;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.base.api.Sheet;
import sheet.cell.Cell;

public class RefExpression implements Expression {

    private final Expression cellID;
    private final Sheet sheet;

    public RefExpression(Expression cellID, Sheet sheet) {
        this.cellID = cellID;
        this.sheet = sheet;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue value = this.cellID.eval();
        if (cellID.getExpressionType() != CellType.STRING) {
            throw new IllegalArgumentException("Argument must be a cell ID, but is a " + cellID.getExpressionType());
        }
        String cellString = value.extractValueWithExpectation(String.class);

        // Get the cell from the sheet based on the coordinate
        Cell referencedCell = sheet.getCell(cellString);

        if (referencedCell == null || referencedCell.getEffectiveValue().getCellType() == CellType.EMPTY) {
            //throw new IllegalArgumentException("Referenced cell " + cellString + " does not exist.");
            return referencedCell.getEffectiveValue();
        }

        // Get the effective value of the referenced cell
        return referencedCell.getEffectiveValue();
    }

    @Override
    public CellType getExpressionType() {
        return eval().getCellType();
    }
}


