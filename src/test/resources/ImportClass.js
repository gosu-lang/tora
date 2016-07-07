import tora.ImportingJavaClassTest.JavaClass;

class ImportClass {
  static foo() {
    return JavaClass.staticFoo();
  }

  bar() {
    var javaObject = new JavaClass();
    return javaObject.bar();
  }
}