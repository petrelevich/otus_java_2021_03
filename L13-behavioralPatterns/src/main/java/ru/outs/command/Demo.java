package ru.outs.command;

public class Demo {
  public static void main(String[] args) {
    var executor = new Executor();

    // нужную операцию выделяем в отдельный класс
    executor.addCommand(new AdderABC());
    executor.addCommand(new Echo());
    executor.addCommand(new AdderABC());

    executor.addCommand(String::toUpperCase);

    // при необходимости можно выполнить позднее
    executor.executeCommands();
  }
}
