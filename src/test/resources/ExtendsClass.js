import java.util.ArrayList


class ExtendsClass extends ArrayList
{
    foo() {
        return 42;
    }

    size() {
        return super.size()+1;
    }

    anotherAdd(x) {
        return super.add(x);
    }
}
