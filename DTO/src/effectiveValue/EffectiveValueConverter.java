package effectiveValue;

import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

public class EffectiveValueConverter {

    public static EffectiveValueDTO toDTO(EffectiveValue effectiveValue) {
        if (effectiveValue == null) {
            return null;
        }
        return new EffectiveValueDTO(
                effectiveValue.getCellType().name(),
                effectiveValue.getValue().toString()
        );
    }

    public static EffectiveValue toEntity(EffectiveValueDTO dto) {
        if (dto == null) {
            return null;
        }
        return new EffectiveValueImpl(
                CellType.valueOf(dto.getCellType()),
                dto.getValue()
        );
    }
}
