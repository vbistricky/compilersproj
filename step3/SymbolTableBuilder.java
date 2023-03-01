public class SymbolTableBuilder extends LittleBaseListener {

    @Override
    public void enterProgram(LittleParser.ProgramContext ctx) {
        // 1. Make a new symbol table or "Global"

        // 2. Add it to the list of symbol tables
        // 3. Push it to the "scope stack"
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
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {

        String type = ctx.var_type().getText();
        String name = ctx.id_list().getText();
        System.out.println("name " + name + " type " + type);
    }

}
