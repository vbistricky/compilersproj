public class Symbol {
    private String name;
    private String type;
    private String value;

    public Symbol(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
        this.value = null;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        String s = "name " + name + " type " + type;
        if (value != null) {
            s += " value " + value;
        }

        return s;

    }
}
