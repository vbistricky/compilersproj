import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ErrorNode;

public class SymbolTableBuilder extends LittleBaseListener {

    private Stack<SymbolTable> scopeStack = new Stack<SymbolTable>();
    private List<SymbolTable> symbolTables = new ArrayList<>();
    private SymbolTable currentScope;
    private static int blockNum = 0;

    private List<String> IRArray = new ArrayList<>();
    private int iterate = 1;
    private List<String> tinyArray = new ArrayList<>();
    private int rIterate = 0;

    @Override
    public void enterProgram(LittleParser.ProgramContext ctx) {
        // 1. Make a new symbol table or "Global"
        currentScope = new SymbolTable("GLOBAL");
        // 2. Add it to the list of symbol tables
        symbolTables.add(currentScope);
        // 3. Push it to the "scope stack"
        scopeStack.push(currentScope);

        IRArray.add("IR code");
    }

    @Override
    public void exitProgram(LittleParser.ProgramContext ctx) {
        // 1. Pop the scope stack
        scopeStack.pop();
        blockNum = 0;

        IRArray.add("tiny code");
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx) {
        // 1. extract the name, type, value
        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText();
        // System.out.println("name " + name + " type " + type + " value " + value);

        // 2. create a new symbol table entry using the above info and insert to the
        // table at the top of the stack (i.e. current table)
        Symbol symbol = new Symbol(name, type, value);
        currentScope.addSymbol(symbol);

    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type = ctx.var_type().getText();
        enterId_list(ctx.id_list(), type);
    }

    @Override
    public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
        // TODO: After the split, check if either of the sides of the exprArray are in
        // the symbol table.
        // TODO: If they are not, then we need to store it into a temporary because they
        // are therefore a literal and not a variable.
        String id = ctx.id().getText();
        String expr = ctx.expr().getText();
        String typeChar = "";

        if (expr.contains("(")) {
            expr = expr.substring(1, expr.length() - 1);
        }

        String[] exprArray = expr.split("[-+*/]");
        for (int i = 0; i < symbolTables.get(0).symbols.size(); i++) {
            if ((symbolTables.get(0).symbols.get(i).getName().compareTo(exprArray[0]) == 0)) {
                typeChar = symbolTables.get(0).symbols.get(i).getType();
                break;
            }
        }

        if (typeChar.compareTo("INT") == 0) {
            typeChar = "I";
        } else {
            typeChar = "F";
        }

        if (expr.contains("*")) {
            IRArray.add("MULT" + typeChar + " " + exprArray[0] + " " + exprArray[1] + " " + "$T" + iterate);
            IRArray.add("STORE" + typeChar + " " + "$T" + iterate + " " + id);
            iterate++;
        } else if (expr.contains("/")) {
            IRArray.add("DIV" + typeChar + " " + exprArray[0] + " " + exprArray[1] + " " + "$T" + iterate);
            IRArray.add("STORE" + typeChar + " " + "$T" + iterate + " " + id);
            iterate++;
        } else if (expr.contains("+")) {
            IRArray.add("ADD" + typeChar + " " + exprArray[0] + " " + exprArray[1] + " " + "$T" + iterate);
            IRArray.add("STORE" + typeChar + " " + "$T" + iterate + " " + id);
            iterate++;
        } else if (expr.contains("-")) {
            IRArray.add("SUB" + typeChar + " " + exprArray[0] + " " + exprArray[1] + " " + "$T" + iterate);
            IRArray.add("STORE" + typeChar + " " + "$T" + iterate + " " + id);
            iterate++;
        }

        for (int i = 0; i < symbolTables.get(0).symbols.size(); i++) {
            if ((symbolTables.get(0).symbols.get(i).getName().compareTo(id) == 0)
                    && !(expr.contains("*") || expr.contains("/") || expr.contains("+") || expr.contains("-"))) {
                String type = symbolTables.get(0).symbols.get(i).getType();
                if (type.compareTo("INT") == 0) {
                    IRArray.add("STOREI " + expr + " " + "$T" + iterate);
                    IRArray.add("STOREI " + "$T" + iterate + " " + id);
                    iterate++;
                } else if (type.compareTo("FLOAT") == 0) {
                    IRArray.add("STOREF " + expr + " " + "$T" + iterate);
                    IRArray.add("STOREF " + "$T" + iterate + " " + id);
                    iterate++;
                }
            }
        }

    }

    @Override
    public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
        String idList = ctx.id_list().getText();
        String[] ids = idList.split(",");

        for (int i = 0; i < symbolTables.get(0).symbols.size(); i++) {
            for (int j = 0; j < ids.length; j++) {
                if (symbolTables.get(0).symbols.get(i).getName().compareTo(ids[j]) == 0) {
                    if (symbolTables.get(0).symbols.get(i).getType().compareTo("INT") == 0) {
                        IRArray.add("READI " + ids[j]);
                    } else if (symbolTables.get(0).symbols.get(i).getType().compareTo("FLOAT") == 0) {
                        IRArray.add("READF " + ids[j]);
                    }
                }
            }
        }
    }

    @Override
    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        String idList = ctx.id_list().getText();
        String[] ids = idList.split(",");

        for (int i = 0; i < ids.length; i++) {
            for (int j = 0; j < symbolTables.get(0).symbols.size(); j++) {
                if (symbolTables.get(0).symbols.get(j).getName().compareTo(ids[i]) == 0) {
                    if (symbolTables.get(0).symbols.get(j).getType().compareTo("INT") == 0) {
                        IRArray.add("WRITEI " + ids[i]);
                    } else if (symbolTables.get(0).symbols.get(j).getType().compareTo("FLOAT") == 0) {
                        IRArray.add("WRITEF " + ids[i]);
                    } else if (symbolTables.get(0).symbols.get(j).getType().compareTo("STRING") == 0) {
                        IRArray.add("WRITES " + ids[i]);
                    }
                }
            }
        }
    }

    public void enterId_list(LittleParser.Id_listContext ctx, String type) {
        String idList = ctx.getText();
        String[] ids = idList.split(",");
        for (String id : ids) {
            Symbol symbol = new Symbol(id, type);
            currentScope.checkDuplicates(id);
            currentScope.addSymbol(symbol);
        }
    }

    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        String funcName = ctx.id().getText();
        SymbolTable funcScope = new SymbolTable(funcName);
        symbolTables.add(funcScope);
        scopeStack.push(funcScope);
        currentScope = funcScope;

        IRArray.add("LABEL " + funcName);
        IRArray.add("LINK");
    }

    @Override
    public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        scopeStack.pop();

        IRArray.add("RET");
    }

    @Override
    public void enterParam_decl(LittleParser.Param_declContext ctx) {
        String type = ctx.var_type().getText();
        String name = ctx.id().getText();
        Symbol symbol = new Symbol(name, type);
        currentScope.addSymbol(symbol);
    }

    @Override
    public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        blockNum++;
        String ifName = "BLOCK " + blockNum;
        SymbolTable ifScope = new SymbolTable(ifName);
        symbolTables.add(ifScope);
        scopeStack.push(ifScope);
        currentScope = ifScope;
    }

    @Override
    public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
        scopeStack.pop();
    }

    @Override
    public void enterElse_part(LittleParser.Else_partContext ctx) {
        if (ctx.getText().equals("")) {
            return;
        }

        blockNum++;
        String elseName = "BLOCK " + blockNum;
        SymbolTable elseScope = new SymbolTable(elseName);
        symbolTables.add(elseScope);
        scopeStack.push(elseScope);
        currentScope = elseScope;
    }

    @Override
    public void exitElse_part(LittleParser.Else_partContext ctx) {
        if (ctx.getText().equals("")) {
            return;
        }
        scopeStack.pop();
    }

    @Override
    public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        blockNum++;
        String whileName = "BLOCK " + blockNum;
        SymbolTable whileScope = new SymbolTable(whileName);
        symbolTables.add(whileScope);
        scopeStack.push(whileScope);
        currentScope = whileScope;
    }

    @Override
    public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
        scopeStack.pop();
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        System.out.println("Error: " + node.getText());
    }

    public void prettyPrint() {
        for (SymbolTable st : symbolTables) {
            System.out.println(st.toString());
            System.out.println();
        }
    }

    public void printIR() {
        for (int x = 0; x < IRArray.size(); x++) {
            System.out.println(";" + IRArray.get(x));
        }

        for (int i = 0; i < symbolTables.get(0).symbols.size(); i++) {
            if (symbolTables.get(0).symbols.get(i).getType().compareTo("STRING") != 0) {
                System.out.println("var " + symbolTables.get(0).symbols.get(i).getName());
            } else {
                System.out.println("str " + symbolTables.get(0).symbols.get(i).getName() + " "
                        + symbolTables.get(0).symbols.get(i).getValue());
            }
        }

        for (int i = 0; i < IRArray.size(); i++) {
            String[] tempArray = IRArray.get(i).split(" ");
            if (tempArray[0].compareTo("STOREI") == 0 || tempArray[0].compareTo("STOREF") == 0) {
                System.out.print("move ");
                if (tempArray[1].contains("$T")) {
                    System.out.println("r" + rIterate + " " + tempArray[2]);
                    rIterate++;
                } else {
                    System.out.println(tempArray[1] + " r" + rIterate);
                }
            } else if (tempArray[0].compareTo("READI") == 0 || tempArray[0].compareTo("READF") == 0) {
                System.out.println("sys " + tempArray[0].toLowerCase() + " " + tempArray[1]);
            } else if (tempArray[0].compareTo("MULTI") == 0) {
                System.out.println("move " + tempArray[1] + " r" + rIterate);
                System.out.println("muli " + tempArray[2] + " " + "r" + rIterate);
            } else if (tempArray[0].compareTo("MULTF") == 0) {
                System.out.println("move " + tempArray[1] + " r" + rIterate);
                System.out.println("mulf " + tempArray[2] + " " + "r" + rIterate);
            } else if (tempArray[0].compareTo("DIVI") == 0) {
                System.out.println("move " + tempArray[1] + " r" + rIterate);
                System.out.println("divi " + tempArray[2] + " " + "r" + rIterate);
            } else if (tempArray[0].compareTo("DIVF") == 0) {
                System.out.println("move " + tempArray[1] + " r" + rIterate);
                System.out.println("divf " + tempArray[2] + " " + "r" + rIterate);
            } else if (tempArray[0].compareTo("WRITEI") == 0 || tempArray[0].compareTo("WRITEF") == 0) {
                System.out.println("sys writer " + tempArray[1]);
            } else if (tempArray[0].compareTo("WRITES") == 0) {
                System.out.println("sys " + tempArray[0].toLowerCase() + " " + tempArray[1]);
            } else if (tempArray[0].compareTo("ADDI") == 0) {
                System.out.println("move " + tempArray[1] + " r" + rIterate);
                System.out.println("addi " + tempArray[2] + " " + "r" + rIterate);
            } else if (tempArray[0].compareTo("ADDF") == 0) {
                System.out.println("move " + tempArray[1] + " r" + rIterate);
                System.out.println("addf " + tempArray[2] + " " + "r" + rIterate);
            } else if (tempArray[0].compareTo("SUBI") == 0) {
                System.out.println("move " + tempArray[1] + " r" + rIterate);
                System.out.println("subi " + tempArray[2] + " " + "r" + rIterate);
            } else if (tempArray[0].compareTo("SUBF") == 0) {
                System.out.println("move " + tempArray[1] + " r" + rIterate);
            }
        }
        System.out.println("sys halt");
    }
}
