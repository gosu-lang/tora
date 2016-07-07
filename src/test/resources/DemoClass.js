class DemoClass {

  // constructor definition
  constructor(a) {
    this.foo = 42;
    this.baz = a;
    this._doh = 21;
    this._poh = 84;
  }

  // function definition


  bar() {
    return this.foo;
  }

  fcal() {
    return this.poh + this.doh
  }


  sum(a,b) {
    return a + b;
  }

  get cons() {
    return this.baz;
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


  static set staticPoh(d) {
    this._poh = d;
  }

  static get staticPoh() {
    return this._poh;
  }


  // static function definition
  static staticFoo() {
    return 42;
  }
}