package tora.parser.tree;

import gw.config.CommonServices;
import gw.lang.reflect.ParameterInfoBuilder;
import gw.lang.reflect.TypeSystem;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by lmeyer-teruel on 7/26/2016.
 */

public class ParameterNode extends Node {
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

    public ArrayList<String> getTypes() {
        return _types;
    }
    public ParameterInfoBuilder[] toParamList () {
        ParameterInfoBuilder[] parameterInfoBuilders = new ParameterInfoBuilder[_params.size()];
        for (int i = 0; i < _params.size(); i++) {
            try {
                parameterInfoBuilders[i] = new ParameterInfoBuilder().withName(_params.get(i))
                        .withDefValue(CommonServices.getGosuIndustrialPark().getNullExpressionInstance())
                        .withType(TypeSystem.getByRelativeName(_types.get(i)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return parameterInfoBuilders;
    }

    @Override
    public String genCode()
    {
        return _params.stream().collect(Collectors.joining(","));
    }
}
