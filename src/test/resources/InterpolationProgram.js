function basic() {
  return `5`;
}
function oneLine() {
  return `5 + 5 is ${5 + 5}`;
}
function multiLine(foo) {
  return `this is a
  multiline
  example
  of template strings with the argument ${foo}`;
}

function curlyInExpression() {
  return `making sure we don't exit expressions early ${ {a:"is good practice"}.a }`;
}