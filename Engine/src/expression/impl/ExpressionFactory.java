package expression.impl;

import expression.api.Expression;
import sheet.effectiveValue.CellType;
import sheet.base.api.Sheet;

import java.util.List;

import static sheet.effectiveValue.EffectiveValueImpl.*;

public class ExpressionFactory {

    public static Expression createExpression(String functionName, List<String> arguments, Sheet sheet) {
        switch (functionName) {
            case "PLUS":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the PLUS function, got " + arguments.size() + ". Plus function example: {PLUS, X, Y}");
                }
                return new PlusExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1),sheet));

            case "MINUS":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the MINUS function, got " + arguments.size() + ". Minus function example: {MINUS, X, Y}");
                }
                return new MinusExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "TIMES":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the TIMES function, got " + arguments.size() + ". Times function example: {TIMES, X, Y}");
                }
                return new TimesExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "DIVIDE":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the DIVIDE function, got " + arguments.size() + ". Divide function example: {DIVIDE, X, Y}");
                }
                return new DivideExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "MOD":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the MOD function, got " + arguments.size() + ". Mod function example: {MOD, X, Y}");
                }
                return new ModExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "POW":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the POW function, got " + arguments.size() + ". Pow function example: {POW, X, Y}");
                }
                return new PowExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "ABS":
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 arguments in the ABS function, got " + arguments.size() + ". Abs function example: {ABS, X}");
                }
                return new AbsExpression(parseArgument(arguments.getFirst(), sheet));

            case "CONCAT":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the CONCAT function, got " + arguments.size() + ". Concat function example: {CONCAT, X, Y}");
                }
                return new ConcatExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "SUB":
                if (arguments.size() != 3) {
                    throw new IllegalArgumentException("Expected 3 arguments in the SUB function, got " + arguments.size() + ". Sub function example: {SUB, X, start, end}");
                }
                return new SubExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet), parseArgument(arguments.get(2), sheet));

            case "REF":
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 argument in the REF function, got " + arguments.size() + ". Ref function example: {REF, A5}");
                }
                return new RefExpression(parseArgument(arguments.getFirst(), sheet), sheet);

            default:
                throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }

    private static Expression parseArgument(String argument, Sheet sheet) {
        // If the argument is a function, recursively create the corresponding Expression
        if (isFunction(argument)) {
            String functionName = extractFunctionName(argument);
            List<String> innerArguments = extractFunctionArguments(argument);
            innerArguments.removeFirst();
            return createExpression(functionName, innerArguments, sheet);
        }

        // If it's numeric
        if (isNumeric(argument)) {
            return new LiteralExpression(Double.parseDouble(argument), CellType.NUMERIC);
        }

        // If it's boolean
        if (argument.equalsIgnoreCase("true") || argument.equalsIgnoreCase("false")) {
            return new LiteralExpression(Boolean.parseBoolean(argument), CellType.BOOLEAN);
        }

        // Otherwise, treat it as a string literal
        return new LiteralExpression(argument, CellType.STRING);
    }
}
