

![Tora](http://i.imgur.com/ndYNdk4.jpg)

Tora is a [Gosu](http://gosu-lang.github.io/) extension library that allows for seamless interaction with javascript resources, leveraging Gosu's Open Type System and the [Java Nashorn](http://openjdk.java.net/projects/nashorn/) project.

The library supports the use of javascript programs from Gosu, the use of ES6-flavored javascript classes from Gosu, the use of Gosu (and Java) classes from javascript, as well as the creation of type-safe javascript expressions for use in Java or Gosu, as a scripting layer.

This library is sponsored and supported by [Guidewire Software](http://www.guidewire.com)
## Javascript Program Support

Variables

Functions

    var hello = function() {
        print("Hello");
    }


## Javascript Class Support

Javascript classes are exposed as regular classes in Gosu. They have the same functionallity as Java classes,
including constructors, methods, static methods, and properties.

Javascript: foo.js

    class Foo {

        //Constructor
        constructor(a) {
            this.foo = a;
            this._bars = 5;
        }

        //Methods
        bar() {
            return this.foo * 2;
        }

        baz(a,b) {
            return a+b + this.foo;
        }

        //Static Methods
        static hello() {
            return "hello";
        }

        //Properties
        get bars() {
            return this._bars*2;
        }

        set bars(a) {
            this._bars = a;
        }

    }




TODO: Describe basic class syntax support

### Accessing Javascript Classes from Gosu

Javascript classes can be accessed using the same syntax as Gosu classes.

Gosu:

    var foo = new Foo(10);
    print(foo.bar()); // 20
    print(foo.bars); // 5

    foo.bars = 20;
    print(foo.bars) // 40
    print(Foo.hello()) // Hello



### Accessing Gosu & Java Classes from Javascript

The Import statement is used to extend gosu and java classes with javascript methods.

Javascript: hello.js


    import java.util.ArrayList;

    function hello() {
        var arrlist = new ArrayList();
        arrlist.add(1);
        arrlist.add(2);
        arrlist.add(3);
        print(arrlist.toArray(new Integer[arrlist.size()]));
    }


Gosu:

    hello.hello(); //prints [1,2,3]




TODO import statement examples

### Extending Gosu & Java Classes from Javascript


Javascript


    import java.util.ArrayList;

    class sizePrints extends ArrayList {
        constructor() {
            super();
        }

        size () {
            print(super.size());
        }
    }

Gosu:

    var sizePrintsArrayList = new sizePrints();
    sizePrintsArrayList.add(1);
    sizePrintsArrayList.add(2);
    sizePrintsArrayList.add(3);
    sizePrintsArrayList.size(); // Prints 3


## Javascript Expression Support