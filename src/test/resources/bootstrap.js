function foo() {
  return "bar"
}

function identity(x) {
  return x
}

function returnsJavascriptObject(arg) {
  var x = 10;
  var y = function() { return 20 }
  return {
    "x" : x,
    "y" : y,
    "arg" : arg
  }
}