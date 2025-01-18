package cell;

import coordinate.CoordinateConverter;
import effectiveValue.EffectiveValueConverter;
import sheet.cell.Cell;
import sheet.cell.CellImpl;
import sheet.effectiveValue.EffectiveValue;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;

public class CellConverter {

    public static CellDTO toDTO(Cell cell) {
        // Create a DTO without full references to CellDTO to prevent circular dependencies
        List<String> dependsOnIds = cell.getDependsOn().stream()
                .map(dependentCell -> CoordinateConverter.toDTO(dependentCell.getCoordinate()).toString()) // or use a unique identifier
                .collect(Collectors.toList());

        List<String> influencingOnIds = cell.getInfluencingOn().stream()
                .map(influencingCell -> CoordinateConverter.toDTO(influencingCell.getCoordinate()).toString()) // or use a unique identifier
                .collect(Collectors.toList());

        return new CellDTO(
               cell.getCoordinate().toString(),
                cell.getOriginalValue(),
                EffectiveValueConverter.toDTO(cell.getEffectiveValue()),
                cell.getVersion(),
                cell.getLastUpdatedBy(),
                dependsOnIds,  // Changed to a list of strings or identifiers
                influencingOnIds, // Changed to a list of strings or identifiers
                toHexString(cell.getTextColor()),
                toHexString(cell.getBackgroundColor())
        );
    }

    // Utility methods for color conversions
    public static String toHexString(Color color) {
        // Convert JavaFX Color to hex string with alpha channel
        return String.format("#%02x%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255));
    }

    private static Color fromHexString(String hexColor) {
        // Handle both 6-character and 8-character hex codes (with alpha)
        if (hexColor.length() == 7) {
            hexColor += "ff";  // Add full opacity if not specified
        }
        return Color.web(hexColor);
    }
}
