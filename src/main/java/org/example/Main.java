package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Set<Character> ROMAN_DIGITS = Set.of('I', 'V', 'X');

    public static void main(String[] args) throws Exception {
        System.out.println(calc("7 / 3"));
    }

    public static String calc(String input) throws Exception {
        var expression = validateAndParse(input);
        var result = doCalc(expression);
        validateResult(result, expression.isRoman);
        return expression.isRoman ? RomanNumberConverter.maybeConvertToRoman(result) : String.valueOf(result);
    }

    private static int doCalc(Expression expression) throws Exception {
        switch (expression.getSign()) {
            case "+":
                return expression.getFirstNum() + expression.getSecondNum();
            case "-":
                return expression.getFirstNum() - expression.getSecondNum();
            case "*":
                return expression.getFirstNum() * expression.getSecondNum();
            case "/":
                return expression.getFirstNum() / expression.getSecondNum();
            default:
                throw new Exception();
        }
    }

    private static void validateResult(int result, boolean isRoman) throws Exception {
        if (isRoman && (result < 1 || result > 4000)) {
            throw new Exception();
        }
    }

    private static Expression validateAndParse(String expression) throws Exception {
        if (expression == null || expression.isBlank()) {
            throw new Exception();
        }
        var correctedExpression = expression.replace(" ", "");
        var rawExpression = parseExpression(correctedExpression);
        if ((isRoman(rawExpression.getFirstNum()) && !isRoman(rawExpression.getSecondNum()))
                || (isRoman(rawExpression.getSecondNum()) && !isRoman(rawExpression.getFirstNum()))) {
            throw new Exception();
        }
        Expression parsedExpression = new Expression(
                RomanNumberConverter.maybeConvertToArabic(rawExpression.getFirstNum()),
                rawExpression.getSign(),
                RomanNumberConverter.maybeConvertToArabic(rawExpression.getSecondNum()),
                isRoman(rawExpression.getFirstNum()));
        if (parsedExpression.getFirstNum() > 10 || parsedExpression.getSecondNum() > 10) {
            throw new Exception();
        }
        return parsedExpression;
    }

    private static RawExpression parseExpression(String expression) throws Exception {
        var firstNum = new StringBuilder();
        var sign = "";
        var secondNum = new StringBuilder();
        for (char symbol : expression.toCharArray()) {
            if (Character.isDigit(symbol) || ROMAN_DIGITS.contains(symbol)) {
                if (sign.isBlank()) {
                    firstNum.append(symbol);
                } else {
                    secondNum.append(symbol);
                }
            } else {
                if (sign.isBlank()) {
                    switch (symbol) {
                        case '+', '-', '/', '*':
                            sign = String.valueOf(symbol);
                            break;
                        default:
                            throw new Exception();
                    }
                } else {
                    throw new Exception();
                }
            }
        }
        return new RawExpression(firstNum.toString(), sign, secondNum.toString());
    }

    private static boolean isRoman(String value) {
        for (Character romanDigit : ROMAN_DIGITS) {
            if (value.contains(romanDigit.toString())) {
                return true;
            }
        }
        return false;
    }

}

class RawExpression {
    private final String firstNum;
    private final String sign;
    private final String secondNum;

    public RawExpression(String firstNum, String sign, String secondNum) {
        this.firstNum = firstNum;
        this.sign = sign;
        this.secondNum = secondNum;
    }

    public String getFirstNum() {
        return firstNum;
    }

    public String getSign() {
        return sign;
    }

    public String getSecondNum() {
        return secondNum;
    }

    @Override
    public String toString() {
        return "RawExpression{" +
                "firstNum='" + firstNum + '\'' +
                ", sign='" + sign + '\'' +
                ", secondNum='" + secondNum + '\'' +
                '}';
    }
}

class Expression {
    private final int firstNum;
    private final String sign;
    private final int secondNum;
    public final boolean isRoman;

    public Expression(int firstNum, String sign, int secondNum, boolean isRoman) {
        this.firstNum = firstNum;
        this.sign = sign;
        this.secondNum = secondNum;
        this.isRoman = isRoman;
    }

    public int getFirstNum() {
        return firstNum;
    }

    public String getSign() {
        return sign;
    }

    public int getSecondNum() {
        return secondNum;
    }

    public boolean isRoman() {
        return isRoman;
    }

    @Override
    public String toString() {
        return "Expression{" +
                "firstNum=" + firstNum +
                ", sign='" + sign + '\'' +
                ", secondNum=" + secondNum +
                ", isRoman=" + isRoman +
                '}';
    }
}

enum RomanNumeral {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);

    private int value;

    RomanNumeral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static List<RomanNumeral> getReverseSortedValues() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                .collect(Collectors.toList());
    }
}

class RomanNumberConverter {
    private static final Map<String, Integer> romanToArabic = Map.of(
            "I", 1,
            "II", 2,
            "III", 3,
            "IV", 4,
            "V", 5,
            "VI", 6,
            "VII", 7,
            "VIII", 8,
            "IX", 9,
            "X", 10
    );

    public static int maybeConvertToArabic(String romanNumber) throws Exception {
        try {
            return romanToArabic.get(romanNumber);
        } catch (Exception e) {
            try {
                return Integer.parseInt(romanNumber);
            } catch (NumberFormatException ignored) {
                throw new Exception();
            }
        }
    }

    public static String maybeConvertToRoman(int number) {
        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }

        return sb.toString();
    }
}