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

import br.unicap.meow.compiler.model.Token;
import br.unicap.meow.compiler.model.TokenTypes;

public class SyntaticAnalyzer {
    private Compiler scanner;
    private Token currentToken;

    public SyntaticAnalyzer(Compiler compiler) {
        this.scanner = compiler;
        this.currentToken = compiler.getNextToken();
    }

    private String badSyntaxErrorMessage() {
        if (currentToken != null)
            return "ERROR on row " + scanner.getErrorRow() + " and column " + scanner.getErrorColumn() + " before " + currentToken.getLexeme();

        return "ERROR on row " + scanner.getErrorRow() + " and column " + scanner.getErrorColumn();
    }

    public void startingPoint_nonTerminal() {
        if (currentToken != null && currentToken.getLexeme().equals("int"))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException("ERROR on row 1 and column 1"  + "\t 'int' is missing");

        if (currentToken != null && currentToken.getLexeme().equals("main"))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t 'main' is missing");        

        if (currentToken != null && currentToken.getLexeme().equals("("))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t '(' is missing");
        
        if (currentToken != null && currentToken.getLexeme().equals(")"))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t ')' is missing");        

        blockOfCode_nonTerminal();

        if (currentToken != null)
            throw new RuntimeException(badSyntaxErrorMessage() + "\t code out of block scope");
        else
            System.out.println("Your code is AMAZING! ;)");
    }

    private void blockOfCode_nonTerminal() {
        if (currentToken != null && currentToken.getLexeme().equals("{")) {
            currentToken = scanner.getNextToken();

            while (currentToken != null && (currentToken.getLexeme().equals("int") || currentToken.getLexeme().equals("float") || currentToken.getLexeme().equals("char"))){
                declaration_nonTerminal();
            }
    
            while (currentToken != null && (currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode) || currentToken.getLexeme().equals("{") || currentToken.getLexeme().equals("while") || currentToken.getLexeme().equals("if"))) {
                command_nonTerminal();
            }
        } else {
            throw new RuntimeException(badSyntaxErrorMessage() + "\t '{' is missing");
        }

        if (currentToken != null && currentToken.getLexeme().equals("}"))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t '}' is missing");        
    }

    private void command_nonTerminal() {        
        if (currentToken != null && currentToken.getLexeme().equals("while"))
            iteration_nonTerminal();
        else if (currentToken != null && currentToken.getLexeme().equals("if")) {
            currentToken = scanner.getNextToken();

            if (currentToken.getLexeme().equals("(")) {
                currentToken = scanner.getNextToken();
            } else {
                throw new RuntimeException(badSyntaxErrorMessage() + "\t '(' is missing");
            }

            relationalExpression_nonTerminal();

            if (currentToken.getLexeme().equals(")")) {
                currentToken = scanner.getNextToken();
            } else {
                throw new RuntimeException(badSyntaxErrorMessage() + "\t ')' is missing");
            }

            command_nonTerminal();

            if (currentToken.getLexeme().equals("else")) {
                currentToken = scanner.getNextToken();
                command_nonTerminal();
            }                     
        } else
            basicCommand_nonTerminal();
    }

    private void basicCommand_nonTerminal() {
        if (currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode))
            assignment_nonTerminal();
        else
            blockOfCode_nonTerminal();
    }

    private void iteration_nonTerminal() {
        if (currentToken != null && currentToken.getLexeme().equals("while"))
            currentToken = scanner.getNextToken();
        else 
            throw new RuntimeException(badSyntaxErrorMessage() + "\t 'while' is missing"); 

        if (currentToken != null && currentToken.getLexeme().equals("("))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t '(' is missing");

        relationalExpression_nonTerminal();

        if (currentToken != null && currentToken.getLexeme().equals(")"))
            currentToken = scanner.getNextToken();
        else 
            throw new RuntimeException(badSyntaxErrorMessage() + "\t ')' is missing");

        command_nonTerminal();
    }

    private void assignment_nonTerminal() {
        if (currentToken != null && currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode))
            currentToken = scanner.getNextToken();            
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t token should be an identifier");        

        if (currentToken != null && currentToken.getLexeme().equals("="))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t '=' is missing");

        arithmeticExpression_nonTerminal();

        if (currentToken != null && currentToken.getLexeme().equals(";"))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t ';' is missing");
    }

    private void relationalExpression_nonTerminal() {
        arithmeticExpression_nonTerminal();
        if (currentToken != null && currentToken.getType().equals(TokenTypes.RELATIONAL_OPERATOR.typeCode)) {
            currentToken = scanner.getNextToken();
        }
        arithmeticExpression_nonTerminal();
    }

    private void arithmeticExpression_nonTerminal() {
        term_nonTerminal();
        arithmeticExpression_line_nonTerminal();
    }

    private void arithmeticExpression_line_nonTerminal() {
        if (currentToken.getLexeme().equals("+") || currentToken.getLexeme().equals("-")) {
            currentToken = scanner.getNextToken();
            term_nonTerminal();
        }
    }

    private void term_nonTerminal() {
        factor_terminal();
        term_line_nonTerminal();
    }

    private void term_line_nonTerminal() {
        if (currentToken.getLexeme().equals("*") || currentToken.getLexeme().equals("/")) {
            currentToken = scanner.getNextToken();
            factor_terminal();
        }
    }

    private void factor_terminal() {
        if (currentToken != null && currentToken.getLexeme().equals("(")) { 
            currentToken = scanner.getNextToken();
            arithmeticExpression_nonTerminal();

            if (!currentToken.getLexeme().equals(")")) {
                throw new RuntimeException(badSyntaxErrorMessage() + "\t ')' is missing");
            }            
        } else if (currentToken != null && (currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode) || currentToken.getType().equals(TokenTypes.REAL.typeCode) ||
            currentToken.getType().equals(TokenTypes.INTEGER.typeCode) || currentToken.getType().equals(TokenTypes.CHAR.typeCode))) {
            currentToken = scanner.getNextToken();
        }
    }

    private void declaration_nonTerminal() {
        if (currentToken != null && (currentToken.getLexeme().equals("int") || currentToken.getLexeme().equals("float") || currentToken.getLexeme().equals("char")))
                currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t token should be a type");
        
        if (currentToken != null && currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t token should be an identifier");
        
        if (currentToken != null && currentToken.getLexeme().equals(";"))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t ';' is missing");
        
    }
}
