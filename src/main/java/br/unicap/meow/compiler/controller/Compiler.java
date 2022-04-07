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

    private static final char UNDERLINE = '_';
    private static final char POINT = '.';
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
                    if ((currentAutomatonState = whenOnFirstState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(Type.IDENTIFIER.typeCode, lexeme.toString());
                    }

                case 2:
                    if ((currentAutomatonState = whenOnSecondState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(Type.INTEGER.typeCode, lexeme.toString());
                    }

                case 3:
                    if ((currentAutomatonState = whenOnThirdState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("Erro: número float inválido");
                    }

                case 4:
                    if ((currentAutomatonState = whenOnFourthState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(Type.REAL.typeCode, lexeme.toString());
                    }

                case 5:
                    if ((currentAutomatonState = whenOnFithState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(Type.SPECIAL_CHARACTER.typeCode, lexeme.toString());
                    }

            }
        }
        return null;
    }

    private int whenOnInitialState(char currentCharacter) {
        if (isWhiteSpace(currentCharacter)) {
            return 0;
        } else if (isLetterOrUnderline(currentCharacter)) {
            return 1;
        } else if (isDigit(currentCharacter)) {
            return 2;
        } else if (isSpecialCharacter(currentCharacter)) {
            return 5;
        } else {
            throw new RuntimeException("Error: invalid token");
        }
    }

    private int whenOnFirstState(char currentCharacter) {
        if (isLetterOrUnderline(currentCharacter) || isDigit(currentCharacter)) {
            return 1;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private int whenOnSecondState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 2;
        } else if (isPoint(currentCharacter)) {
            return 3;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private int whenOnThirdState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 4;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private int whenOnFourthState(char currentCharacter) {
        if (isDigit(currentCharacter)) {
            return 4;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private int whenOnFithState(char currentCharacter) {
        if (isSpecialCharacter(currentCharacter)) {
            return 5;
        } else {
            return INVALID_DESTINATION_STATE;
        }
    }

    private boolean isWhiteSpace(char currentCharacter) {
        return EMPTY_SPACE_CHARACTERS.contains(Character.valueOf(currentCharacter));
    }

    private boolean isLetterOrUnderline(char currentCharacter) {
        return (Character.isLetter(currentCharacter) || currentCharacter == UNDERLINE);
    }

    private boolean isDigit(char currentCharacter) {
        return Character.isDigit(currentCharacter);
    }

    private boolean isPoint(char currentCharacter) {
        return (currentCharacter == POINT);
    }

    private boolean isSpecialCharacter(char currentCharacter) {
        return SPECIAL_CHARACTERS.contains(Character.valueOf(currentCharacter));
    }

    private boolean isArithmeticOperator(char currentCharacter) {
        return ARITHMETIC_OPERATORS.contains(Character.valueOf(currentCharacter));
    }
}
