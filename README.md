

![Tora](http://i.imgur.com/ndYNdk4.jpg)

Tora is a [Gosu](http://gosu-lang.github.io/) extension library that allows for seamless interaction with javascript resources, leveraging Gosu's Open Type System and the [Java Nashorn](http://openjdk.java.net/projects/nashorn/) project.

The library supports the use of javascript programs from Gosu, the use of ES6-flavored javascript classes from Gosu, the use of Gosu (and Java) classes from javascript, as well as the creation of type-safe javascript expressions for use in Java or Gosu, as a scripting layer.

This library is sponsored and supported by [Guidewire Software](http://www.guidewire.com)

## Javascript Program Support

Tora makes standard ES5-style Javascript programs available as types in Gosu.

The javascript program is evaluated when the type is first accessed.  Top level functions 
are accessible as static methods on the program type.

### Functions

Here is an example top-level function found in `ExampleProgram.js`:

```javascript
    function hello(name) {
        return "Hello " + name;
    }
```
    
This function could be invoked from Gosu like so:

    print( ExampleProgram.hello("Gosu") )

#### Parameter & Return Types

Parameters and the return type of javascript functions are all of type `dynamic.Dynamic` which is a [special Gosu type](https://gosu-lang.github.io/2014/07/10/dynamic-language-features-in-gosu.html) that allows for dynamic type behavior.

### Variables

Top level variables in javascript programs are treated as global variables and will retain their values
between evaluation.  Given this function:

```javascript
    var i = 0;
    
    function nextNum() {
        return i++;
    }
```

The following code

```javascript
    print( ExampleProgram.nextNum() )
    print( ExampleProgram.nextNum() )
```

will print

    0.0
    1.0

## Javascript Class Support

Javascript classes are exposed as regular classes in Gosu. They have the same functionallity as Java classes,
including constructors, methods, static methods, and properties.

Javascript: foo.js

```javascript
    class Foo {

        //Constructor
        constructor(a) {
            this.foo = a;
            this._bars = 5;
        }

        //Methods
        function bar() {
            return this.foo * 2;
        }

        function baz(a,b) {
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
```

####  Constructor 
The constructor is called when a new object is created. It initializes the properties within the object.

Javascript

```javascript
    class Foo {
        constructor(a) {
            this.bar = a;
        }
    }
```

Gosu
    
    var foo = new Foo(5); // Creates new Foo object and sets this.bar to a
    
#### Methods
Methods are functions that are assigned to classes. They can interact with properties of the class and call other
internal methods. 
Javascript

```javascript
    class Foo {

        constructor(a) {
            this.foo = a;
        }

        function bar() {
            return this.foo * 2;
        }
    }
```

Gosu

```javascript
    var foo = new Foo(21);
    print(foo.bar); // returns 42 
```
#### Static Methods

Javascript

```javascript
    class Foo {
        constructor(a) {
            this.foo = a;
            this._bars = 5;
        }

        static function staticFoo() {
            return 42;
        }
    }
```
    
Gosu

```javascript
    print(Foo.staticFoo()); // Prints  42
```

#### Properties
Classes can have getter and setter properties which abstract the properties held within the class.

```javascript
    class Foo {
        constructor(a) {
            this.foo = a;
            this._bars = 5;
        }
        get bars() {
            return this._bars*2;
        }

        set bars(a) {
            this._bars = a;
        }
    }
```

Gosu 

```javascript
    var foo = new Foo();
    foo.bars = 21;
    print(foo.bars) // Prints 42 
```

## Javascript Template Support

Javascript templates are supported as first class citizens. A Javascript String Template is a file that ends
in the .jst extension.

Javascript Template: SampleJSTemplate.jst

    <%@ params(names) %>

    All Names: <% '''javascript for (var i = 0; i < names.length; i++) { '''%>
        ${names[i]}
    <% } %>

The template declares the parameters using the `<%@ params() %>` directive, and can import Java/Gosu classes using
the <%@ import %> directive.

Javascript statements can be added between the `<%` and `%>` punctuators, which are evaluated as Javascript but
added directly to the generated string.

Javascript expressions can be added either between the `${` and `}` punctuators or the `<%=` and `%>` punctuators,
and are evaluated and added to the generated string.

Javascript templates can then be rendered from Gosu as so:

Gosu:

```javascript

    var str = SampleJSTemplate.renderToString({"Carson", "Kyle", "Lucca"});
    print(str)

```

## Accessing Javascript Classes from Gosu

Javascript classes can be accessed using the same syntax as Gosu classes.

Gosu:

```javascript
    var foo = new Foo(10);
    print(foo.bar()); // 20
    print(foo.bars); // 5

    foo.bars = 20;
    print(foo.bars) // 40
    print(Foo.hello()) // Hello
```

### Accessing Gosu & Java Classes from Javascript

The (non-standard javascript) import statement is used to extend gosu and java classes with javascript methods.

Here is some example javascript: hello.js

```javascript
    import java.util.ArrayList;

    function hello() {
        var arrlist = new ArrayList();
        arrlist.add(1);
        arrlist.add(2);
        arrlist.add(3);
        print(arrlist.toArray(new Integer[arrlist.size()]));
    }
```

This can be invoked from Gosu like so:

```javascript
    hello.hello(); //prints [1,2,3]
```

The import statement in tora acts like the java import statement, not the (unsupported) javascript version.

### Extending Gosu & Java Classes from Javascript

Java classes can be extended using javascript, allowing for the creation of modified classes. One
known limitation is that the constructor of the superclass cannot be overwritten.

Javascript

```javascript
    import java.util.ArrayList;

    class sizePrints extends ArrayList {
        size () {
            print(super.size());
        }
    }
```

Gosu:

```javascript
    var sizePrintsArrayList = new sizePrints();
    sizePrintsArrayList.add(1);
    sizePrintsArrayList.add(2);
    sizePrintsArrayList.add(3);
    sizePrintsArrayList.size(); // Prints 3
```

## Multi-threading Support

The current implementation of Nashorn is not yet stable for multi-threading. Although it
allows for multi-threading, the implementation is very slow when it comes to creating
and deleting objects. 

Users should not attempt to access any Nashorn objects from multiple threads.

## Javascript Extensions

### Typescript Style Typing

In order to allow for greater control and readability, tora allows for specifying the types of variables that can
be parameters and return types. The supported types include any in the standard java.lang.* package, however those found
in the java.util.* packages must be imported.

Javascript

```javascript
    import java.util.ArrayList;
    class Sample {
        constructor(a : String) {
            this.foo = a;
        }

        foo (bar: String, baz : Integer) : ArrayList {
           var arrlist = new ArrayList();
           for(var i = 0 ; i < baz ; i ++) {
               arrlist.add(bar);
           }
           return arrlist;
        }
    }
```

```javascript
Gosu
    var sample = new Sample();
    print(foo("Hello", 5)) // ["Hello","Hello","Hello","Hello","Hello"]
```

### ES6 Arrow Functions

Tora supports the use of ES6 Arrow Functions inside any Javascript program or class.

Javascript:

```javascript
    //Arrow function expression
    function filterEvens(list) {
        return list.filter( a => a % 2 == 0);
    }

    //Arrow function statements
    function incrementList(list) {
        return list.map( a => {return a + 1});
    }
```



