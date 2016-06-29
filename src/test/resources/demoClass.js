class DemoClass {

  // constructor definition
  constructor() {
    this.foo = 42;
    this._doh = 21;
    this._poh = 84;
  }

  // function definition
  bar() {
    return this.foo;
  }

  sum(a,b) {
    return a + b;
  }

  set doh(d) {
    this._doh = d;
  }

  // property definition
  get doh() {
    return this._doh;
  }

  set poh(d) {
    this._poh = d;
  }

  get poh() {
    return this._poh;
  }


  // static function definition
  static staticFoo() {
    return 42;
  }
}