package expression.impl;

import expression.api.Expression;
import expression.impl.logical.*;
import expression.impl.numerical.*;
import expression.impl.other.IfExpression;
import expression.impl.other.RefExpression;
import expression.impl.ranges.AverageExpression;
import expression.impl.ranges.SumExpression;
import expression.impl.string.ConcatExpression;
import expression.impl.string.SubExpression;
import sheet.effectiveValue.CellType;
import sheet.base.api.Sheet;

import java.util.List;

import static sheet.effectiveValue.EffectiveValueImpl.*;

public class ExpressionFactory {

    public static Expression createExpression(String functionName, List<String> arguments, Sheet sheet) {
        switch (functionName) {
            case "PLUS":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the PLUS function, got " + arguments.size() + ". \nPlus function example: {PLUS, X, Y}");
                }
                return new PlusExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1),sheet));

            case "MINUS":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the MINUS function, got " + arguments.size() + ". \nMinus function example: {MINUS, X, Y}");
                }
                return new MinusExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "TIMES":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the TIMES function, got " + arguments.size() + ". \nTimes function example: {TIMES, X, Y}");
                }
                return new TimesExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "DIVIDE":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the DIVIDE function, got " + arguments.size() + ". \nDivide function example: {DIVIDE, X, Y}");
                }
                return new DivideExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "MOD":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the MOD function, got " + arguments.size() + ". \nMod function example: {MOD, X, Y}");
                }
                return new ModExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "PERCENT":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the PERCENT function, got " + arguments.size() + ". \nPERCENT function example: {MOD, X, Y}");
                }
                return new PercentExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "POW":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the POW function, got " + arguments.size() + ". \nPow function example: {POW, X, Y}");
                }
                return new PowExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "ABS":
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 arguments in the ABS function, got " + arguments.size() + ". \nAbs function example: {ABS, X}");
                }
                return new AbsExpression(parseArgument(arguments.getFirst(), sheet));

            case "CONCAT":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the CONCAT function, got " + arguments.size() + ". \nConcat function example: {CONCAT, X, Y}");
                }
                return new ConcatExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "SUB":
                if (arguments.size() != 3) {
                    throw new IllegalArgumentException("Expected 3 arguments in the SUB function, got " + arguments.size() + ". \nSub function example: {SUB, X, start, end}");
                }
                return new SubExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet), parseArgument(arguments.get(2), sheet));

            case "REF":
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 argument in the REF function, got " + arguments.size() + ". \nRef function example: {REF, A5}");
                }
                return new RefExpression(parseArgument(arguments.getFirst(), sheet), sheet);

            case "EQUAL":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the EQUAL function, got " + arguments.size() + ". \nEQUAL function example: {EQUAL, X, Y}");
                }
                return new EqualExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "NOT":
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 argument in the NOT function, got " + arguments.size() + ". \nNOT function example: {NOT, X}");
                }
                return new NotExpression(parseArgument(arguments.getFirst(), sheet));

            case "BIGGER":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the BIGGER function, got " + arguments.size() + ". \nBIGGER function example: {BIGGER, X, Y}");
                }
                return new BiggerExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "LESS":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the LESS function, got " + arguments.size() + ". \nLESS function example: {LESS, X, Y}");
                }
                return new LessExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "OR":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the OR function, got " + arguments.size() + ". \nOR function example: {OR, X, Y}");
                }
                return new OrExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "AND":
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 arguments in the AND function, got " + arguments.size() + ". \nAND function example: {AND, X, Y}");
                }
                return new AndExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet));

            case "SUM":
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 arguments in the SUM function, got " + arguments.size() + ". \nSUM function example: {SUM, Range_Name}");
                }
                return new SumExpression(parseArgument(arguments.getFirst(), sheet));

            case "AVERAGE":
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("Expected 1 arguments in the AVERAGE function, got " + arguments.size() + ". \nAVERAGE function example: {SUM, Range_Name}");
                }
                return new AverageExpression(parseArgument(arguments.getFirst(), sheet));

            case "IF":
                if (arguments.size() != 3) {
                    throw new IllegalArgumentException("Expected 3 arguments in the IF function, got " + arguments.size() + ". \nIF function example: {IF, condition, then, else}");
                }
                return new IfExpression(parseArgument(arguments.get(0), sheet), parseArgument(arguments.get(1), sheet), parseArgument(arguments.get(2), sheet));

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
        else if (isNumeric(argument)) {
            return new LiteralExpression(Double.parseDouble(argument), CellType.NUMERIC);
        }
        // If it's boolean
        else if (argument.equalsIgnoreCase("true") || argument.equalsIgnoreCase("false")) {
            return new LiteralExpression(argument.toUpperCase(), CellType.BOOLEAN);
        }
        // If it's a range
        else if (sheet.getRange(argument) != null) {
            return new LiteralExpression(sheet.getRange(argument), CellType.RANGE);
        }
        // Otherwise, treat it as a string literal
        return new LiteralExpression(argument, CellType.STRING);
    }
}
