package ru.otus.proxy.security;


public class Demo {
    public static void main(String[] args) {
        security();
    }

    private static void security() {
        var proxy = new SecurityProxy(new SecurityAccessImpl());
        proxy.access();
    }
}
