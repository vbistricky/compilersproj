import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SymbolTableBuilder extends LittleBaseListener {

    private Stack<SymbolTable> scopeStack = new Stack<SymbolTable>();
    private List<SymbolTable> symbolTables = new ArrayList<>();
    private SymbolTable currentScope;

    @Override
    public void enterProgram(LittleParser.ProgramContext ctx) {
        // 1. Make a new symbol table or "Global"
        currentScope = new SymbolTable("GLOBAL");
        // 2. Add it to the list of symbol tables
        symbolTables.add(currentScope);
        // 3. Push it to the "scope stack"
        scopeStack.push(currentScope);

    }

    @Override
    public void exitProgram(LittleParser.ProgramContext ctx) {
        // 1. Pop the scope stack
        scopeStack.pop();
        // 2. Set the current scope to the top of the stack
        currentScope = scopeStack.peek();
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx) {

        // 1. extract the name, type, value
        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText();
        System.out.println(name + ", " + type + ", " + value);

        // 2. create a new symbol table entry using the above info and insert to the
        // table at the top of the stack (i.e. current table)
        Symbol symbol = new Symbol(name, type, value);
        currentScope.addSymbol(symbol);

    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {

        String type = ctx.var_type().getText();
        String name = ctx.id_list().getText();
        System.out.println("name " + name + " type " + type);

        Symbol symbol = new Symbol(name, type);
        currentScope.addSymbol(symbol);

    }

    @Override
    public void enterVar_type(LittleParser.Var_typeContext ctx) {
        String type = ctx.getText();
        System.out.println("type " + type);
    }

    @Override
    public void enterId_list(LittleParser.Id_listContext ctx) {

        String name = ctx.id().getText();
        String type = "INT";
        System.out.println("name " + name + " type " + type);

        Symbol symbol = new Symbol(name, type);
        currentScope.addSymbol(symbol);
    }

    @Override
    public void enterId_tail(LittleParser.Id_tailContext ctx) {
        String name = ctx.id().getText();
        String type = "INT";
        System.out.println("name " + name + " type " + type);

        Symbol symbol = new Symbol(name, type);
        currentScope.addSymbol(symbol);
    }

    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        String funcName = ctx.id().getText();
        SymbolTable funcScope = new SymbolTable(funcName);
        symbolTables.add(funcScope);
        scopeStack.push(funcScope);
        currentScope = funcScope;

        // add the parameters to the function scope
        LittleParser.Param_decl_listContext paramList = ctx.param_decl_list();
        if (paramList != null) {
            List<LittleParser.Param_declContext> params = paramList.param_decl();
            for (int i = 0; i < params.size(); i++) {
                LittleParser.Param_declContext param = params.get(i);
                String name = param.id().getText();
                String type = param.var_type().getText();
                Symbol symbol = new Symbol(name, type);
                funcScope.addSymbol(symbol);
            }
        }
    }

    @Override
    public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        scopeStack.pop();
        currentScope = scopeStack.peek();
    }

}
