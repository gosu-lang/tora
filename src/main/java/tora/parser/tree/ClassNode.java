package tora.parser.tree;


import tora.parser.Tokenizer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ClassNode extends Node {
    private static final String CREATE_CLASS = "var _createClass = function () { " +
        "function defineProperties(target, props) { for (var i = 0; i < props.length; i++) " +
        "{ var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; " +
        "descriptor.configurable = true; if (\"value\" in descriptor) descriptor.writable = true; " +
        "Object.defineProperty(target, descriptor.key, descriptor); } } " +
        "return function (Constructor, protoProps, staticProps) { if (protoProps) " +
        "defineProperties(Constructor.prototype, protoProps); if (staticProps) " +
        "defineProperties(Constructor, staticProps); return Constructor; }; }();";

    private static final String CLASS_CALL_CHECK = "function _classCallCheck(instance, Constructor) { " +
        "if (!(instance instanceof Constructor)) { " +
        "throw new TypeError(\"Cannot call a class as a function\") } }";

    private static final List<Class> CLASS_GEN_ORDER = Arrays.asList(ConstructorNode.class, FunctionNode.class,
        PropertyNode.class);

    public ClassNode(String name, Tokenizer.Token start, Tokenizer.Token end) {
        super(name, start, end);
    }


    @Override
    public String genCode() {
        String code = CLASS_CALL_CHECK; //Makes sure constructor is called correctly
        if (!getChildren(PropertyNode.class).isEmpty()) code += CREATE_CLASS; //Defines getters and setters

        code += "var " + getName() + " = function() { ";

        if (getChildren(ConstructorNode.class).isEmpty()) {
            code += new ConstructorNode(getName(),null,null).genCode(); //Gen default constructor if no child found
        } else code += getChildren(ConstructorNode.class).get(0).genCode();

        for (Node node : getChildren(FunctionNode.class)) {
            code += "\n\t" + node.genCode();
        }

        class PropertyNodeWrapper {
            private String _name;
            private PropertyNode _getter = null;
            private PropertyNode _setter = null;

            public PropertyNodeWrapper(String name) {
                _name = name;
            }
            public void add(PropertyNode node) {
                if (node._isSetter) _setter = node;
                else _getter = node;
            }

            public String genCode() {
                return "{\n\t key: \"" + _name + "\"," +
                        (_setter != null?_setter.genCode()+",":"") +
                        (_getter != null?_getter.genCode():"") +
                        "}";
            }
        }

        //combines getters and setters for each property
        if (!getChildren(PropertyNode.class).isEmpty()) {
            HashMap<String, PropertyNodeWrapper> propertyNodeBucket = new HashMap();

            code += "_createClass(" + getName() + ", [";
            for (PropertyNode node : getChildren(PropertyNode.class)) {
                PropertyNodeWrapper wrapper = propertyNodeBucket.get(node.getName());
                if (wrapper == null) {
                    wrapper = new PropertyNodeWrapper(node.getName());
                    propertyNodeBucket.put(node.getName(), wrapper);
                }
                wrapper.add(node);
            }

            for (String property : propertyNodeBucket.keySet()) {
                code += propertyNodeBucket.get(property).genCode();
            }

            code += "]);";
        }

        code += "\n\t return " + getName() + ";\n}();";

        return code;
    }
}
