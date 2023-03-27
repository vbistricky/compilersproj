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

    public void enterId_list(LittleParser.Id_listContext ctx, String type) {
        String idList = ctx.getText();
        String[] ids = idList.split(",");
        for (String id : ids) {
            Symbol symbol = new Symbol(id, type);
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

        // add the parameters to the function scope
        LittleParser.Param_decl_listContext paramList = ctx.param_decl_list();

    }

    @Override
    public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        scopeStack.pop();
        currentScope = scopeStack.peek();
    }

    public static void main(String[] args) {

    }

    public void prettyPrint() {
        for (SymbolTable st : symbolTables) {
            System.out.println(st);
        }
    }

}
