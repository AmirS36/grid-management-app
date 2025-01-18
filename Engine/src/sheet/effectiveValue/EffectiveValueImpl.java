package sheet.effectiveValue;

import expression.api.Expression;
import sheet.base.api.Sheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static expression.impl.ExpressionFactory.createExpression;

public class EffectiveValueImpl implements EffectiveValue, Serializable {

    private CellType cellType;
    private Object value;

    public EffectiveValueImpl(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }
    public EffectiveValueImpl(String value, Sheet sheet) {
        //Check if the value is a Function
        if (isFunction(value)) {
            handleFunction(value, sheet);
        }
        //Check if the value is numeric
        else if(isNumeric(value)) {
            this.cellType = CellType.NUMERIC;
            this.value = Double.parseDouble(value);
        }
        //Check if the value is boolean
        else if (value.equalsIgnoreCase("true")) {
            this.cellType = CellType.BOOLEAN;
            this.value = true;
        }
        else if (value.equalsIgnoreCase("false")) {
            this.cellType = CellType.BOOLEAN;
            this.value = false;
        }
        //Check if the value is an empty string
        else if (value.isEmpty()) {
            this.cellType = CellType.EMPTY;
            this.value = "";
        }
        //Check if the value is a range
        else if (sheet.getRange(value) != null) {
            this.cellType = CellType.RANGE;
            this.value = sheet.getRange(value).toString();
        }

        //Otherwise, consider it as a string
        else {
            this.cellType = CellType.STRING;
            this.value = value;
        }
    }

    @Override
    public CellType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        }
        return null;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isFunction(String str) {
        return str.startsWith("{") && str.endsWith("}");
    }

    private void handleFunction(String function, Sheet sheet){
        String functionName = extractFunctionName(function);  // Extract the function name
        List<String> arguments = extractFunctionArguments(function);  // Extract function arguments
        arguments.removeFirst();

        Expression expression = createExpression(functionName, arguments, sheet);
        EffectiveValue evaluatedValue = expression.eval();  // Evaluate the function

        this.cellType = evaluatedValue.getCellType();  // Assign the evaluated type and value
        this.value = evaluatedValue.getValue();

    }

    public static String extractFunctionName(String value) {
        // Strip the outer {} from the function
        String innerValue = value.substring(1, value.length() - 1);

        // Split the value by commas to get the parts
        String[] parts = innerValue.split(",", 2);  // Split into function name and the rest

        String functionName = parts[0].trim();  // Get the first part as the function name

        return functionName.toUpperCase();
    }

    public static List<String> extractFunctionArguments(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        boolean insideBraces = false;

        // Iterate over the input, starting after the first '{' and ending before the last '}'
        for (int i = 1; i < input.length() - 1; i++) {
            char c = input.charAt(i);

            if (c == '{') {
                stack.push(c);
                insideBraces = true;
            } else if (c == '}') {
                stack.pop();
                insideBraces = !stack.isEmpty();
            }

            if (c == ',' && !insideBraces) {
                // We've reached a top-level comma (i.e., not within nested braces)
                parts.add(buffer.toString().trim());
                buffer.setLength(0); // Reset the buffer for the next argument
            } else {
                buffer.append(c); // Add the current character to the buffer
            }
        }

        // Add the last argument after the loop ends
        if (!buffer.isEmpty()) {
            parts.add(buffer.toString().trim());
        }

        return parts;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectiveValueImpl that = (EffectiveValueImpl) o;
        return cellType == that.cellType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellType, value);
    }
}
