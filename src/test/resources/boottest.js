var Runnable = Java.type('java.lang.Runnable');
var Printer = Java.extend(Runnable, {
    run: function() {
        print('printed from a separate thread');
    }
});

var Thread = Java.type('java.lang.Thread');
new Thread(new Printer()).start();

var th = new Thread(function() {
    print('printed from another thread');
}).start();

var hello = function() {
    return 42;
}