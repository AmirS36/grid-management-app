package sheet;

import cell.CellConverter;
import coordinate.CoordinateConverter;
import range.RangeConverter;
import sheet.base.api.Sheet;

import java.util.Map;
import java.util.stream.Collectors;

public class SheetConverter {

    public static SheetDTO toDTO(Sheet sheet) {
        SheetDTO sheetDTO = new SheetDTO(
                sheet.getName(),
                sheet.getRowsLength(),
                sheet.getColsLength(),
                sheet.getRowHeight(),
                sheet.getColWidth(),
                sheet.getVersion(),
                sheet.getCellsChanged(),
                sheet.getActiveCells().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().toString(),
                                entry -> CellConverter.toDTO(entry.getValue())
                        )),
                sheet.getPreviousActiveCells().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().toString(),
                                entry -> CellConverter.toDTO(entry.getValue())
                        )),
                sheet.getRanges().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> RangeConverter.toDTO(entry.getValue())
                        ))
        );

        return sheetDTO;
    }
}
