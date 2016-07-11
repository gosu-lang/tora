import tora.ImportingJavaClassTest.JavaClass;


class Extender extends JavaClass{

    bar (){
        return 31;
    }

}
var extender = new Extender();
print(extender.bar());

//ExtendTestClass.prototype.moo = function(){return 213};
//

//

//var JavaInterface = Java.type('tora.ImportingJavaClassTest.JavaClass');
//var Extender = Java.extend(JavaInterface, {
//    bar: function() {
//        return 31;
//    },
//    bar2: function() {
//        return 40;
//    }
//
//});

//print(extender.moo());