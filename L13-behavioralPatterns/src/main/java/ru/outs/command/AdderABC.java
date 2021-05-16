package ru.outs.command;

public class AdderABC implements Command {
    @Override
    public String execute(String data) {
        return data + "ABC";
    }
}
