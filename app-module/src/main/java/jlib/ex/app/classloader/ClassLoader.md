# Classloader
**《Spring 3.x 企业应用开发实战》系统学习**

* 确认内容：三个类装载器的父子层级关系
* 确认结果：
    1. **AppClassLoader**继承自ExtClassLoader。
    2. **ExtClassLoader**继承自根装载器。
    3. **根装载器**采用C++编写，不能被Java得到。
