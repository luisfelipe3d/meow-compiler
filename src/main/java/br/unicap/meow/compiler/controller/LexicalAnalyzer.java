/*
 * Copyright (C) 2022 liraline
 * Copyright (C) 2022 luisfelipe3d
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package br.unicap.meow.compiler.controller;

import java.util.Arrays;
import java.util.List;

public class LexicalAnalyzer {
    private static final char DOLLAR_SIGN = '$';
    private static final char POINT = '.';
    private static final char UNDERLINE = '_';
    private static final char CIRCUMFLEX = '^'; 
    private static final char MINUS_SIGN = '-';
    private static final char SINGLE_QUOTE = '\'';
    private static final char NEGATION_OPERATOR = '!';
    private static final char ASSIGNMENT_OPERATOR = '=';
    private static final List<Character> RELATIONAL_OPERATORS = Arrays.asList('<', '>');
    private static final List<Character> ARITHMETIC_OPERATORS = Arrays.asList('+', '-', '/', '*');
    private static final List<Character> EMPTY_SPACE_CHARACTERS = Arrays.asList(' ', '\t', '\n', '\r');
    private static final List<Character> SPECIAL_CHARACTERS = Arrays.asList('(', ')', '{', '}', ',', ';');
    private static final List<String> RESERVED_WORDS = Arrays.asList("main", "if", "else", "while", "do", "for", "int", "float", "char");

    public static int whenOnInitialState(char currentCharacter) {
        if (isWhiteSpace(currentCharacter)) {
            return 0;
        } else if (isLetter(currentCharacter) || isUnderline(currentCharacter)) {
            return 1;
        } else if (isDigit(currentCharacter)) {
            return 2;
        } else if (isSpecialCharacter(currentCharacter)) {
            return 5;
        } else if (isArithmeticOperator(currentCharacter)) {
            return 6;
        } else if (isAssignOperator(currentCharacter)) {
            return 7;
        } else if (isRelationalOperator(currentCharacter)) {
            return 8;
        } else if (isNegationOperator(currentCharacter)) {
            return 10;
        } else if (isCircumflex(currentCharacter)) {
            return 12;
        } else if (isSingleQuotes(currentCharacter)) {
            return 15;
        } else if (isDollarSign(currentCharacter)) {
            return 99;        
        } else {
            return -1;
        }
    }

    public static int whenOn1stState(char currentCharacter) {
        if (isLetter(currentCharacter) || isDigit(currentCharacter) || isUnderline(currentCharacter)) {
            return 1;
        } else {
            return -1;
        }
    }

    public static int whenOn2ndState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 2;
        } else if (isPoint(currentCharacter)) {
            return 3;
        } else {
            return -1;
        }
    }

    public static int whenOn3rdState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 4;
        } else {
            return -1;
        }
    }

    public static int whenOn4thState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 4;
        } else {
            return -1;
        }
    }

    public static int whenOn7thState(char currentCharacter) {
        if (isAssignOperator(currentCharacter)) {
            return 9;
        } else {
            return 7;
        }
    }

    public static int whenOn8thState(char currentCharacter) {
        if (isAssignOperator(currentCharacter)) {
            return 9;
        } else {
            return 8;
        }
    }

    public static int whenOn10thState(char currentCharacter) {
        if (isAssignOperator(currentCharacter)) {
            return 9;
        } else {
            return -1;
        }
    }

    public static int whenOn12thState(char currentCharacter) {
        if (isMinusSign(currentCharacter)) {
            return 13;
        } else if (isAssignOperator(currentCharacter)) {
            return 18;
        } else {
            return -1;
        }
    }

    public static int whenOn13thState(char currentCharacter) {
        if (isCircumflex(currentCharacter)) {
            return 14;
        } else {
            return -1;
        }
    }

    public static int whenOn15thState(char currentCharacter) {
        if (isLetter(currentCharacter) || isDigit(currentCharacter)) {
            return 16;
        } else {
            return -1;
        }
    }

    public static int whenOn16thState(char currentCharacter) {
        if (isSingleQuotes(currentCharacter)) {
            return 17;
        } else {
            return -1;
        }
    }

    public static int whenOn18thState(char currentCharacter) {
        if (isAssignOperator(currentCharacter)) {
            return 19;
        } else {
            return -1;
        }
    }

    public static int whenOn19thState(char currentCharacter) {
        if (isCircumflex(currentCharacter)) {
            return 14;
        } else {
            return -1;
        }
    }
    
    public static boolean isReservedWord(String charSequence) {
        return RESERVED_WORDS.contains(charSequence.toLowerCase());
    }

    private static boolean isWhiteSpace(char currentCharacter) {
        return EMPTY_SPACE_CHARACTERS.contains(Character.valueOf(currentCharacter));
    }

    private static boolean isLetter(char currentCharacter) {
        return (Character.isLetter(currentCharacter));
    }

    private static boolean isUnderline(char currentCharacter) {
        return currentCharacter == UNDERLINE;
    }

    private static boolean isDigit(char currentCharacter) {
        return Character.isDigit(currentCharacter);
    }

    private static boolean isPoint(char currentCharacter) {
        return currentCharacter == POINT;
    }

    private static boolean isSpecialCharacter(char currentCharacter) {
        return SPECIAL_CHARACTERS.contains(Character.valueOf(currentCharacter));
    }

    private static boolean isArithmeticOperator(char currentCharacter) {
        return ARITHMETIC_OPERATORS.contains(Character.valueOf(currentCharacter));
    }

    private static boolean isAssignOperator(char currentCharacter) {
        return currentCharacter == ASSIGNMENT_OPERATOR;
    }

    private static boolean isRelationalOperator(char currentCharacter) {
        return RELATIONAL_OPERATORS.contains(Character.valueOf(currentCharacter));
    }

    private static boolean isNegationOperator(char currentCharacter) {
        return currentCharacter == NEGATION_OPERATOR;
    }

    private static boolean isSingleQuotes(char currentCharacter) {
        return currentCharacter == SINGLE_QUOTE;
    }

    private static boolean isCircumflex(char currentCharacter) {
        return currentCharacter == CIRCUMFLEX;
    }

    private static boolean isMinusSign(char currentCharacter) {
        return currentCharacter == MINUS_SIGN;
    }

    private static boolean isDollarSign(char currentCharacter) {
        return currentCharacter == DOLLAR_SIGN;
    }
}
