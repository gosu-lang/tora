class DemoClass {

  // constructor definition
  constructor() {
    this.foo = 42;
  }

  // function definition
  bar() {
    return this.foo;
  }


  set doh(d) {
    this.halfDoh = d/2;
  }

  // property definition
  get doh() {
    return this.foo;
  }

  set poh(d) {
    this.doublePoh = d * 2;
  }

  get poh() {
    return this.doh;
  }


  // static function definition
  static staticFoo() {
    return 42;
  }
}