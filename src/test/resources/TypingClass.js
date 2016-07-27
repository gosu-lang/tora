

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

   stringTest(x : String, y: String) {
       return x + y;
   }

   booleanTest(x : Boolean, y : Boolean) {
      return x && y;
   }

//   hashmapTest(x : HashMap, y : ArrayList) {
//
//   }


}
