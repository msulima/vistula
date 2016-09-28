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
