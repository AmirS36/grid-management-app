package effectiveValue;

import java.io.Serializable;

public class EffectiveValueDTO implements Serializable {
    private String cellType;
    private String value;

    public EffectiveValueDTO(String cellType, String value) {
        this.cellType = cellType;
        this.value = value;
    }

    public String getCellType() {
        return cellType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "EffectiveValueDTO{cellType=" + cellType + ", value=" + value + "}";
    }
}