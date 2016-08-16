
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.Integer
import java.lang.Double


function testTypes (x : String, y : Double, z : Double) {
  var b = y + z;
  return x + b;
}

function doubleTest(x : Double, y : Double) {
   return x + y;
}

function intTest(x : Double) : Integer {
    return x;
}

function stringTest(x : String, y: String) : Double {
   return x + y ;
}

function booleanTest(x : Boolean, y : Boolean) {
  return x && y;
}

function javaClassTest () {
    var test = new ArrayList();
    this.usesArrayList(test);
    return test.size();
}
function usesArrayList ( y : ArrayList) : ArrayList {
    y.add(5);
    return y;
}

function failsWhenPassedWrongType(x : ArrayList) {

}

function doubleToStringCoercionTest(x : Double, y: Double) : String {
   return x + y ;
}
function failsWhenReturningNonArrayList () : ArrayList {
    return 5.0;
}
function returnsDouble(x : Double) : Double {
   return x;
}

function returnsWrongType(x : String) : Double {
   return x;
}

function takesInDoubleAndReturns(x : Double) {
   return x;
}

