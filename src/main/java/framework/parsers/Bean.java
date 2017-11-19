package framework.parsers;

import java.util.ArrayList;

public class Bean {
    private String name;
    private String className;
    private ArrayList<String> constructorArg = new ArrayList<String>();
    private ArrayList<String> properties = new ArrayList<String>();
    private Scope scope = Scope.SINGLETON;

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public ArrayList<String> getConstructorArg() {
        return constructorArg;
    }

    public void setConstructorArg(ArrayList<String> constructorArg) {
        this.constructorArg = constructorArg;
    }

    public String toString() {
        return name + " : " + className.toString() + constructorArg.toString() + ", " + properties.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}
