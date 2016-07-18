import java.util.ArrayList


class ExtendsClass extends ArrayList
{
    function foo() {
        return 42;
    }

    function size() {
        return super.size()+1;
    }

    function anotherAdd(x) {
        return super.add(x);
    }
}
