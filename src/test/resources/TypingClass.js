
import java.util.HashMap;
import java.util.ArrayList;
class TypingClass () {
   constructor(a : String) {
     this.foo = a;

   }

   testTypes (x : String, y : Double, z : Double) {
      var b = y + z;
      return x + b;
   }

   doubleTest(x : Double, y : Double) {
       return x + y;
   }

   stringTest(x : String, y: String) : Double {
       return x + y ;
   }

   booleanTest(x : Boolean, y : Boolean) {
      return x && y;
   }

    javaClassTest () {
        var test = new ArrayList();
        this.usesArrayList(test);
        return test.size();
    }
   usesArrayList ( y : ArrayList) : ArrayList {
        y.add(5);
        return y;
   }

   failsWhenPassedWrongType(x : ArrayList) {

   }

   failsWhenReturningNonArrayList () : ArrayList {
        return 5.0;
   }
   returnsDouble(x : Double) : Double {
       return x;
   }

   returnsWrongType(x : String) : Double {
       return x;
   }

}
