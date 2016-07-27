package tora.parser.tree;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by lmeyer-teruel on 7/26/2016.
 */

public class ParameterNode extends Node {
    private boolean _isExpression; //can be expression (implicit return) or statement (has curlies)
    private ArrayList<String> _params;
    private ArrayList<String> _types;

    public ParameterNode()
    {
        super(null);
        _params = new ArrayList<>();
        _types = new ArrayList<>();
    }

    //Takes in parameter and type in string form
    public void addParam(String param, String type) {
        _params.add(param);
        String paramType = (type != null && !type.isEmpty()) ? type :"dynamic.Dynamic";
        _types.add(paramType);
    }

    public ArrayList<String> getParams() {
        return _params;
    }

    public ArrayList<String> getTypes() {
        return _types;
    }


    @Override
    public String genCode()
    {
        return _params.stream().collect(Collectors.joining(","));
    }
}
