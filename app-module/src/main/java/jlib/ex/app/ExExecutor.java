package jlib.ex.app;

import jlib.ex.app.classloader.ClassLoaderEx;

public class ExExecutor {
    public static void main(String[] args) {
        AbstractEx ex = new ClassLoaderEx();
        ex.doEx();
    }
}
