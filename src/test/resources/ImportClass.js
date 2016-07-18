import tora.ImportingJavaClassTest.JavaClass;
import tora.GosuDemoClass;

class ImportClass {
  static javaFoo() {
    return JavaClass.staticFoo();
  }

  javaBar() {
    var javaObject = new JavaClass();
    return javaObject.returnHello();
  }

  static gosuFoo() {
    return GosuDemoClass.staticFoo();
  }

  gosuDouble(x) {
    var gosuObject = new GosuDemoClass();
    return gosuObject.doubleInput(x);
  }

}