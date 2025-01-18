package range;

import cell.CellConverter;
import coordinate.CoordinateConverter;
import sheet.ranges.Range;
import sheet.ranges.RangeImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RangeConverter {

    public static RangeDTO toDTO(Range range) {
        Set<String> cellIds = range.getCells().stream()
                .map(influencingCell -> CoordinateConverter.toDTO(influencingCell.getCoordinate()).toString()) // or use a unique identifier
                .collect(Collectors.toSet());

        List<String> influencingOnIds = range.getInfluencedCells().stream()
                .map(influencingCell -> CoordinateConverter.toDTO(influencingCell.getCoordinate()).toString()) // or use a unique identifier
                .collect(Collectors.toList());

        return new RangeDTO(
                range.getName(),
                cellIds,
                influencingOnIds
        );
    }
}