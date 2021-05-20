package ru.otus.factorymethod;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author sergey
 * created on 19.09.18.
 *
 * @author spv
 * edited 28.08.20.
 */
public class DemoSimpleFactory {
  public static void main(String[] args) {
    // Пример:
    // У нас есть какая-то конфигурация
    // и мы хотим читать ее их разных мест (БД, файл и тд)

    // Простая фабрика (не совсем фабричный метод)

    // из файла
    Configuration config1 = ConfigurationFactory.getConfiguration("file");
    System.out.println(config1.params());

    // или из БД
    Configuration config2 = ConfigurationFactory.getConfiguration("db");
    System.out.println(config2.params());

    // или еще откуда-то ...
    // ...
  }
}
