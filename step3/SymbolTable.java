import java.util.ArrayList;

public class SymbolTable {
    private String name;
    private ArrayList<Symbol> symbols = new ArrayList<Symbol>();

    public SymbolTable(String name) {
        this.name = name;
    }

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    // public boolean checkDuplicates(String name, String[] vars) {

    // for (int i = 0; i < vars.length; i++) {
    // int incr = 0;
    // System.out.println(vars[i] + name);
    // if (vars[i].equals(name)) {
    // incr++;
    // if (incr > 1) {
    // return true;
    // }
    // }
    // }

    // return false;
    // }

    public String toString() {
        String s = "Symbol Table " + name;

        for (int i = 0; i < symbols.size(); i++) {
            s += "\n";
            s += symbols.get(i).toString();
        }

        return s;
    }
}
