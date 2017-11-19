package jlib.ex.app;

import jlib.ex.app.classloader.ClassLoaderEx;

/**
 * 实验对象方法执行确认用main class
 *
 */
public class ExExecutor {
    public static void main(String[] args) {
        AbstractEx ex = new ClassLoaderEx();
        ex.doEx();
    }
}
