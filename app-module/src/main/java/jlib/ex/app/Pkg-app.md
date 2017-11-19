# [Package]app说明

**通过Application执行确认实验结果（及实验对象为Application执行对象）**

* 类包结构
```bat
jlib.ex.app
└─测试对象Package
  └─测试类+
  └─Package或某特殊测试对象类（功能）说明的markdown文件
```
* 测试执行方法
    * 通过ExExecutor的main方法调用AbstractEx接口定义的同意测试方法
    * 也可通过JUnit的执行来测试该实验对象的执行内容
