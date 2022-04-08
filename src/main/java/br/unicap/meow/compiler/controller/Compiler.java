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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import br.unicap.meow.compiler.model.Token;
import br.unicap.meow.compiler.model.TokenTypes;

public class Compiler {
    private final int INITIAL_STATE = 0;
    private final int END_OF_CODE_STATE = 99;
    private final int RESERVED_WORD_STATE = 11;
    private final int ASSIGNMENT_OPERATOR_STATE = 7;
    private final int INVALID_DESTINATION_STATE = -1;
    private final int BASIC_RELATIONAL_OPERATOR_STATE = 8;

    private char[] fileContent;
    private int currentFileIndex;
    private int currentFileRow;
    private int currentFileColumn;

    public Compiler(String path) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(path)));
            this.fileContent = fileContent.toCharArray();
            this.currentFileIndex = 0;
            this.currentFileRow = 1;
            this.currentFileColumn = 0;                  
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
        currentFileColumn--;
    }

    public Token getNextToken() {
        char currentCharacter;
        int currentAutomatonState = 0;
        StringBuffer lexeme = new StringBuffer();

        while (hasNextChar()) {
            currentCharacter = getNextChar();
            currentFileColumn++;

            switch (currentAutomatonState) {
                case 0:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOnInitialState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        if (currentAutomatonState != INITIAL_STATE) {
                            lexeme.append(currentCharacter);
                        } else {
                            if (currentCharacter == '\n') {
                                currentFileRow++;
                                currentFileColumn = 0;
                            }
                        }

                        if ((currentAutomatonState == END_OF_CODE_STATE)) {
                            goBackAnIndex();
                        }

                    } else {
                        throw new RuntimeException("ERROR! Invalid character on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }


                    break;

                case 1:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn1stState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);

                        if (LexicalAnalyzer.isReservedWord(lexeme.toString())) {
                            currentAutomatonState = RESERVED_WORD_STATE;
                        }     

                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(TokenTypes.IDENTIFIER.typeCode, lexeme.toString());
                    }

                case 2:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn2ndState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;                        
                    } else {
                        goBackAnIndex();
                        return new Token(TokenTypes.INTEGER.typeCode, lexeme.toString());
                    }

                case 3:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn3rdState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("ERROR! Invalid float number on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }

                case 4:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn4thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        goBackAnIndex();
                        return new Token(TokenTypes.REAL.typeCode, lexeme.toString());
                    }

                case 5:
                    goBackAnIndex();
                    return new Token(TokenTypes.SPECIAL_CHARACTER.typeCode, lexeme.toString());

                case 6:
                    goBackAnIndex();
                    return new Token(TokenTypes.ARITHMETIC_OPERATOR.typeCode, lexeme.toString());

                case 7:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn7thState(currentCharacter)) == ASSIGNMENT_OPERATOR_STATE) {
                        goBackAnIndex();
                        return new Token(TokenTypes.ASSIGNMENT_OPERATOR.typeCode, lexeme.toString());
                    }

                    lexeme.append(currentCharacter);
                    break;                    

                case 8:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn8thState(currentCharacter)) == BASIC_RELATIONAL_OPERATOR_STATE) {
                        goBackAnIndex();
                        return new Token(TokenTypes.RELATIONAL_OPERATOR.typeCode, lexeme.toString());                        
                    }

                    lexeme.append(currentCharacter);
                    break;                    
                    
                case 9:
                    goBackAnIndex();
                    return new Token(TokenTypes.RELATIONAL_OPERATOR.typeCode, lexeme.toString());
                    
                case 10:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn10thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("ERROR! Invalid relational operator on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }

                case 11:
                    goBackAnIndex();
                    return new Token(TokenTypes.RESERVED_WORD.typeCode, lexeme.toString());

                case 12:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn12thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("ERROR! Invalid special operator on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }

                case 13:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn13thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("ERROR! Invalid special operator on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }

                case 14:
                    goBackAnIndex();
                    return new Token(TokenTypes.SPECIAL_OPERATOR.typeCode, lexeme.toString());

                case 15:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn15thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("ERROR! Invalid char token on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }

                case 16:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn16thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("ERROR: invalid char token on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }

                case 17:
                    goBackAnIndex();
                    return new Token(TokenTypes.CHAR.typeCode, lexeme.toString());

                case 18:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn18thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("ERROR: invalid special operator on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }

                case 19:
                    if ((currentAutomatonState = LexicalAnalyzer.whenOn19thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                        lexeme.append(currentCharacter);
                        break;
                    } else {
                        throw new RuntimeException("ERROR: invalid special operator on row " + currentFileRow + " and column " + currentFileColumn + "\t"
                            + badTokenErrorMessage(lexeme));
                    }

                case 99:
                    return new Token(TokenTypes.CODE_END.typeCode, lexeme.toString());
            }
        }

        return null;
    }

    private String badTokenErrorMessage(StringBuffer lexeme) {
        return "Bad Token: " + lexeme.toString() + fileContent[currentFileIndex - 1];
    }
}