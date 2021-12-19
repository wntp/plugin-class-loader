# plugin-class-loader
基于ServiceLoader 简单的实现了插件间的隔离

plugin-source-test-1和plugin-source-test-2这两个插件实现了SourcePlugin的接口方法，但是在具体实现的时候共同引用了plugin-common-test包，这两个包在1.0和2.0的方法是冲突的。

采用ServiceLoader 进行隔离解决了钻石依赖的问题
