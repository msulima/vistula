const examples = {};
examples.modules = {};
examples.modules.App = function App() {
    ;
};
examples.modules.App.prototype.main = function main() {
    const bar = new examples.modules.Bar();
    return bar.bar();
};
examples.modules.Bar = function Bar() {
    ;
};
examples.modules.Bar.prototype.bar = function bar() {
    return 1;
};
examples.modules.staticBar = function staticBar(x) {
    return x;
};
examples.modules.nesting = {};
examples.modules.nesting.othernesting = {};
examples.modules.nesting.othernesting.Foo = function Foo() {
    ;
};
examples.modules.nesting.othernesting.Foo.prototype.foo = function foo() {
    return 1;
};
