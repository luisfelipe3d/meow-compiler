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
import br.unicap.meow.compiler.model.Variable;
import br.unicap.meow.compiler.model.TokenTypes;

import java.util.List;
import java.util.ArrayList;

public class SyntaticAnalyzer {
    private Compiler scanner;
    private Token currentToken;
    private int currentScope = 0;
    List<Variable> variablesInCode = new ArrayList<Variable>();
    private String currentType;


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

    private void insideBlockOfCode() {
        while (currentToken != null && (currentToken.getLexeme().equals("int") || currentToken.getLexeme().equals("float") || currentToken.getLexeme().equals("char"))){
            declaration_nonTerminal();
        }

        while (currentToken != null && (currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode) || currentToken.getLexeme().equals("{") || currentToken.getLexeme().equals("while") || currentToken.getLexeme().equals("if"))) {
            command_nonTerminal();
        }
    }

    private void blockOfCode_nonTerminal() {
        if (currentToken != null && currentToken.getLexeme().equals("{")) {
            currentToken = scanner.getNextToken();
            currentScope++;

            while (currentToken != null && (currentToken.getLexeme().equals("int") || currentToken.getLexeme().equals("float") ||
                currentToken.getLexeme().equals("char") || currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode) ||
                currentToken.getLexeme().equals("{") || currentToken.getLexeme().equals("while") ||
                currentToken.getLexeme().equals("if"))) {
                    insideBlockOfCode();
                }
                
        } else {
            throw new RuntimeException(badSyntaxErrorMessage() + "\t '{' is missing");
        }

        if (currentToken != null && currentToken.getLexeme().equals("}")) {
            currentToken = scanner.getNextToken();
            currentScope--;
        } else {
            throw new RuntimeException(badSyntaxErrorMessage() + "\t '}' is missing");
        }
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
        String variableName;

        if (currentToken != null && currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode)) {
            variableName = currentToken.getLexeme();
            currentToken = scanner.getNextToken();            
        } else {
            throw new RuntimeException(badSyntaxErrorMessage() + "\t token should be an identifier");     
        }

        if (currentToken != null && currentToken.getLexeme().equals("="))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t '=' is missing");

        arithmeticExpression_nonTerminal();

        Variable declaredVariable = isVariableDeclared(variableName);
        if (declaredVariable != null) {
            if (!declaredVariable.getType().equals(currentType)) {
                throw new RuntimeException(badSyntaxErrorMessage() + "\t type mismatch");
            }
        } else {
            throw new RuntimeException(badSyntaxErrorMessage() + "\t variable '" + variableName + "' was not declared");
        }

        if (currentToken != null && currentToken.getLexeme().equals(";"))
            currentToken = scanner.getNextToken();
        else
            throw new RuntimeException(badSyntaxErrorMessage() + "\t ';' is missing");
    }

    private Variable isVariableDeclared(String variableName) {
        for (Variable variable : variablesInCode) {
            if ((variable.getScope() == currentScope && variable.getName().equals(variableName)) ||
                variable.getName().equals(variableName)) {
                    return variable;
            }
        }
        return null;
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
        } else if (currentToken != null && (currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode) ||
            currentToken.getType().equals(TokenTypes.REAL.typeCode) || currentToken.getType().equals(TokenTypes.INTEGER.typeCode) ||
            currentToken.getType().equals(TokenTypes.CHAR.typeCode))) {
                if (currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode)) {
                    Variable declaredVariable = isVariableDeclared(currentToken.getLexeme());
                    if (declaredVariable != null) {
                        currentType = declaredVariable.getType();
                    } else {
                        throw new RuntimeException(badSyntaxErrorMessage() + "\t variable '" + currentToken.getLexeme() + "' was not declared");
                    }
                } else {
                    currentType = currentToken.getType();
                }
                currentToken = scanner.getNextToken();
        }
    }

    private void declaration_nonTerminal() {
        String variableName, variableType;

        if (currentToken != null && (currentToken.getLexeme().equals("int") ||
            currentToken.getLexeme().equals("float") ||
            currentToken.getLexeme().equals("char"))) {
                switch (currentToken.getLexeme()) {
                    case "int":
                        variableType = TokenTypes.INTEGER.typeCode;
                        break;
                    case "float":
                        variableType = TokenTypes.REAL.typeCode;
                        break;
                    default:
                        variableType = TokenTypes.CHAR.typeCode;
                }

            currentType = variableType;
            currentToken = scanner.getNextToken();
        } else {
            throw new RuntimeException(badSyntaxErrorMessage() + "\t token should be a type");
        }
        
        if (currentToken != null && currentToken.getType().equals(TokenTypes.IDENTIFIER.typeCode)) {
            variableName = currentToken.getLexeme();
            currentToken = scanner.getNextToken();
        } else {
            throw new RuntimeException(badSyntaxErrorMessage() + "\t token should be an identifier");
        }
        
        if (currentToken != null && currentToken.getLexeme().equals(";")) {
            currentToken = scanner.getNextToken();
        } else {
            throw new RuntimeException(badSyntaxErrorMessage() + "\t ';' is missing");
        }
        
        Variable declaredVariable = new Variable(currentScope, variableName, variableType);
        if (!isVariableAlreadyInScope(declaredVariable))
            variablesInCode.add(new Variable(currentScope, variableName, variableType));
        else 
            throw new RuntimeException(badSyntaxErrorMessage() + "\t variable '" + variableName + "' already defined");
    }

    private boolean isVariableAlreadyInScope(Variable newVariable) {
        for (Variable variableInCode : variablesInCode) {
            if (variableInCode.getScope() == newVariable.getScope() && variableInCode.getName().equals(newVariable.getName()))
                return true;
        }

        return false;
    }
}