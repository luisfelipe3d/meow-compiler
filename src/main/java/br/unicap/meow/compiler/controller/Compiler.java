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
    private final int RESERVED_WORD_STATE = 11;
    private final int ASSIGNMENT_OPERATOR_STATE = 7;
    private final int INVALID_DESTINATION_STATE = -1;
    private final int BASIC_RELATIONAL_OPERATOR_STATE = 8;
    private final char NULL_CHAR = Character.MIN_VALUE;

    private char[] fileContent;
    private int currentFileIndex;

    private int rowCounter;
    private int columnCounter;

    public Compiler(String path) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(path)));
            this.fileContent = fileContent.toCharArray();
            this.currentFileIndex = 0;

            this.rowCounter = 1;
            this.columnCounter = 0;                  
        } catch (IOException ex) {
            ex.printStackTrace();
        }  
    }

    private boolean isEndOfFile() {
        return currentFileIndex > fileContent.length;
    }

    private boolean hasNextChar() {
        return currentFileIndex < fileContent.length;
    }

    private char getNextChar() {
        return fileContent[currentFileIndex];
    }

    private void goToNextIndex() {
        currentFileIndex++;
        columnCounter++;
    } 

    private void goBackAnIndex() {
        currentFileIndex--;
        columnCounter--;
    }

    private void increaseRow() {
        rowCounter++;
        columnCounter = 0;
    }

    public Token getNextToken() {
        char currentCharacter = NULL_CHAR;
        int currentAutomatonState = 0;
        StringBuffer lexeme = new StringBuffer();
        boolean hasReadNewCharacter;

        while (!isEndOfFile()) {
            if (hasNextChar()) {
                currentCharacter = getNextChar();
                hasReadNewCharacter = true;
            } else {                
                if(currentCharacter == NULL_CHAR) {
                    break;
                }
                
                hasReadNewCharacter = false;
            }
            
            goToNextIndex();

            switch (currentAutomatonState) {
                case 0:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOnInitialState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    if (currentAutomatonState != INITIAL_STATE) {
                        lexeme.append(currentCharacter);
                    } else {
                        if (currentCharacter == '\n') {
                            increaseRow();
                        }
                    }

                } else {
                    throw new RuntimeException("ERROR! Invalid character on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }


                break;

            case 1:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn1stState(currentCharacter)) != INVALID_DESTINATION_STATE) {
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
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn2ndState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;                        
                } else {
                    goBackAnIndex();
                    return new Token(TokenTypes.INTEGER.typeCode, lexeme.toString());
                }

            case 3:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn3rdState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;
                } else {
                    throw new RuntimeException("ERROR! Invalid float number on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }

            case 4:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn4thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
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
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn7thState(currentCharacter)) == ASSIGNMENT_OPERATOR_STATE) {
                    goBackAnIndex();
                    return new Token(TokenTypes.ASSIGNMENT_OPERATOR.typeCode, lexeme.toString());
                }

                lexeme.append(currentCharacter);
                break;                    

            case 8:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn8thState(currentCharacter)) == BASIC_RELATIONAL_OPERATOR_STATE) {
                    goBackAnIndex();
                    return new Token(TokenTypes.RELATIONAL_OPERATOR.typeCode, lexeme.toString());                        
                }

                lexeme.append(currentCharacter);
                break;                    
                
            case 9:
                goBackAnIndex();
                return new Token(TokenTypes.RELATIONAL_OPERATOR.typeCode, lexeme.toString());
                
            case 10:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn10thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;
                } else {
                    throw new RuntimeException("ERROR! Invalid relational operator on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }

            case 11:
                goBackAnIndex();
                return new Token(TokenTypes.RESERVED_WORD.typeCode, lexeme.toString());

            case 12:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn12thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;
                } else {
                    throw new RuntimeException("ERROR! Invalid special operator on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }

            case 13:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn13thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;
                } else {
                    throw new RuntimeException("ERROR! Invalid special operator on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }

            case 14:
                goBackAnIndex();
                return new Token(TokenTypes.SPECIAL_OPERATOR.typeCode, lexeme.toString());

            case 15:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn15thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;
                } else {
                    throw new RuntimeException("ERROR! Invalid char token on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }

            case 16:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn16thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;
                } else {
                    throw new RuntimeException("ERROR: invalid char token on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }

            case 17:
                goBackAnIndex();
                return new Token(TokenTypes.CHAR.typeCode, lexeme.toString());

            case 18:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn18thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;
                } else {
                    throw new RuntimeException("ERROR: invalid special operator on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }

            case 19:
                if (hasReadNewCharacter && (currentAutomatonState = LexicalAnalyzer.whenOn19thState(currentCharacter)) != INVALID_DESTINATION_STATE) {
                    lexeme.append(currentCharacter);
                    break;
                } else {
                    throw new RuntimeException("ERROR: invalid special operator on row " + rowCounter + " and column " + columnCounter + "\t"
                        + badTokenErrorMessage(lexeme));
                }

            case 99:
                goBackAnIndex();
                return new Token(TokenTypes.CODE_END.typeCode, lexeme.toString());

            }
        }

        return null;
    }

    private String badTokenErrorMessage(StringBuffer lexeme) {
        return "Bad Token: " + lexeme.toString() + fileContent[currentFileIndex - 1];
    }
}