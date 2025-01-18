package sheet.effectiveValue;

import sheet.ranges.Range;

public enum CellType {
    NUMERIC(Double.class) ,
    STRING(String.class) ,
    BOOLEAN(Boolean.class),
    UNKNOWN(Object.class),
    EMPTY(Object.class),
    RANGE(Range.class),
    ERROR(Error.class);

    private Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }
}
