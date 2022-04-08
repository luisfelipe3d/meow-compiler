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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.RuntimeErrorException;

import java.util.List;
import java.util.Arrays;

import br.unicap.meow.compiler.model.Token;
import br.unicap.meow.compiler.model.Type;

public class Compiler {
    private final int INITIAL_STATE = 0;
    private final int INVALID_DESTINATION_STATE = -1;
    private final int ASSIGNMENT_OPERATOR_STATE = 7;
    private final int BASIC_RELATIONAL_OPERATOR_STATE = 8;

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

    private char[] fileContent;
    private int currentFileIndex;

    public Compiler(String path) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(path)));
            this.fileContent = fileContent.toCharArray();
            this.currentFileIndex = 0;                        
        } catch (IOException ex) {
            ex.printStackTrace();
        }  
    }

    private boolean hasNextChar() {
        return currentFileIndex < fileContent.length;
    }

    private char getNextChar() {
        return fileContent[currentFileIndex++];
    }

    private void goBackAnIndex() {
        currentFileIndex--;
    }

    public Token getToken() {
        char currentCharacter;
        int currentAutomatonState = 0;
        StringBuffer lexeme = new StringBuffer();

        while (hasNextChar()) {
            currentCharacter = getNextChar();

            switch (currentAutomatonState) {
                case 0:
                    if ((currentAutomatonState = whenOnInitialState(currentCharacter)) != INITIAL_STATE) {
                        lexeme.append(currentCharacter);
                    }
                    break;

                case 1:
                    if ((currentAutomatonState = whenOn1stState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);

                        if (isReservedWord(lexeme.toString())) {
                            currentAutomatonState = 11;
                        }

                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(Type.IDENTIFIER.typeCode, lexeme.toString());
                    }

                case 2:
                    if ((currentAutomatonState = whenOn2ndState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(Type.INTEGER.typeCode, lexeme.toString());
                    }

                case 3:
                    if ((currentAutomatonState = whenOn3rdState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: número float inválido");
                    }

                case 4:
                    if ((currentAutomatonState = whenOn4thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(Type.REAL.typeCode, lexeme.toString());
                    }

                case 5:
                    goBackAnIndex();
                    return new Token(Type.SPECIAL_CHARACTER.typeCode, lexeme.toString());

                case 6:
                    goBackAnIndex();
                    return new Token(Type.ARITHMETIC_OPERATOR.typeCode, lexeme.toString());

                case 7:
                    if ((currentAutomatonState = whenOn7thState(currentCharacter)) == ASSIGNMENT_OPERATOR_STATE) {
                        goBackAnIndex();
                        return new Token(Type.ASSIGNMENT_OPERATOR.typeCode, lexeme.toString());
                    }

                    lexeme.append(currentCharacter);
                    break;                    

                case 8:
                    if ((currentAutomatonState = whenOn8thState(currentCharacter)) == BASIC_RELATIONAL_OPERATOR_STATE) {
                        goBackAnIndex();
                        return new Token(Type.RELATIONAL_OPERATOR.typeCode, lexeme.toString());                        
                    }

                    lexeme.append(currentCharacter);
                    break;                    
                    
                case 9:
                    goBackAnIndex();
                    return new Token(Type.RELATIONAL_OPERATOR.typeCode, lexeme.toString());
                    
                case 10:
                    if ((currentAutomatonState = whenOn10thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: operador inválido");
                    }

                case 11:
                    goBackAnIndex();
                    return new Token(Type.RESERVED_WORD.typeCode, lexeme.toString());

                case 12:
                    if ((currentAutomatonState = whenOn12thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: operador especial inválido");
                    }

                case 13:
                    if ((currentAutomatonState = whenOn13thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: operador especial inválido");
                    }

                case 14:
                    goBackAnIndex();
                    return new Token(Type.SPECIAL_OPERATOR.typeCode, lexeme.toString());

                case 15:
                    if ((currentAutomatonState = whenOn15thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: char inválido");
                    }

                case 16:
                    if ((currentAutomatonState = whenOn16thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: char inválido");
                    }

                case 17:
                    goBackAnIndex();
                    return new Token(Type.CHAR.typeCode, lexeme.toString());

                case 18:
                    if ((currentAutomatonState = whenOn18thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: operador especial inválido");
                    }

                case 19:
                    if ((currentAutomatonState = whenOn19thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: operador especial inválido");
                    }

                case 99:
                    return new Token(Type.CODE_END.typeCode, lexeme.toString());
            }
        }
        return null;
    }

    private int whenOnInitialState(char currentCharacter) {
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
            goBackAnIndex();
            return 99;        
        } else {
            throw new RuntimeException("Error: invalid token");
        }
    }

    private int whenOn1stState(char currentCharacter) {
        if (isLetter(currentCharacter) || isDigit(currentCharacter) || isUnderline(currentCharacter)) {
            return 1;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private int whenOn2ndState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 2;
        } else if (isPoint(currentCharacter)) {
            return 3;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private int whenOn3rdState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 4;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private int whenOn4thState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 4;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private int whenOn7thState(char currentCharacter) {
        if (isAssignOperator(currentCharacter)) {
            return 9;
        } else {
            return 7;
        }
    }

    private int whenOn8thState(char currentCharacter) {
        if (isAssignOperator(currentCharacter)) {
            return 9;
        } else {
            return 8;
        }
    }

    private int whenOn10thState(char currentCharacter) {
        if (isAssignOperator(currentCharacter)) {
            return 9;
        } else {
            return -1;
        }
    }

    private int whenOn12thState(char currentCharacter) {
        if (isMinusSign(currentCharacter)) {
            return 13;
        } else if (isAssignOperator(currentCharacter)) {
            return 18;
        } else {
            return -1;
        }
    }

    private int whenOn13thState(char currentCharacter) {
        if (isCircumflex(currentCharacter)) {
            return 14;
        } else {
            return -1;
        }
    }

    private int whenOn15thState(char currentCharacter) {
        if (isLetter(currentCharacter) || isDigit(currentCharacter)) {
            return 16;
        } else {
            return -1;
        }
    }

    private int whenOn16thState(char currentCharacter) {
        if (isSingleQuotes(currentCharacter)) {
            return 17;
        } else {
            return -1;
        }
    }

    private int whenOn18thState(char currentCharacter) {
        if (isAssignOperator(currentCharacter)) {
            return 19;
        } else {
            return -1;
        }
    }

    private int whenOn19thState(char currentCharacter) {
        if (isCircumflex(currentCharacter)) {
            return 14;
        } else {
            return -1;
        }
    }

    private boolean isWhiteSpace(char currentCharacter) {
        return EMPTY_SPACE_CHARACTERS.contains(Character.valueOf(currentCharacter));
    }

    private boolean isLetter(char currentCharacter) {
        return (Character.isLetter(currentCharacter));
    }

    private boolean isUnderline(char currentCharacter) {
        return currentCharacter == UNDERLINE;
    }

    private boolean isDigit(char currentCharacter) {
        return Character.isDigit(currentCharacter);
    }

    private boolean isPoint(char currentCharacter) {
        return currentCharacter == POINT;
    }

    private boolean isSpecialCharacter(char currentCharacter) {
        return SPECIAL_CHARACTERS.contains(Character.valueOf(currentCharacter));
    }

    private boolean isArithmeticOperator(char currentCharacter) {
        return ARITHMETIC_OPERATORS.contains(Character.valueOf(currentCharacter));
    }

    private boolean isAssignOperator(char currentCharacter) {
        return currentCharacter == ASSIGNMENT_OPERATOR;
    }

    private boolean isRelationalOperator(char currentCharacter) {
        return RELATIONAL_OPERATORS.contains(Character.valueOf(currentCharacter));
    }

    private boolean isNegationOperator(char currentCharacter) {
        return currentCharacter == NEGATION_OPERATOR;
    }

    private boolean isReservedWord(String charSequence) {
        return RESERVED_WORDS.contains(charSequence.toLowerCase());
    }

    private boolean isSingleQuotes(char currentCharacter) {
        return currentCharacter == SINGLE_QUOTE;
    }

    private boolean isCircumflex(char currentCharacter) {
        return currentCharacter == CIRCUMFLEX;
    }

    private boolean isMinusSign(char currentCharacter) {
        return currentCharacter == MINUS_SIGN;
    }

    private boolean isDollarSign(char currentCharacter) {
        return currentCharacter == DOLLAR_SIGN;
    }
}
