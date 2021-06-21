package ru.otus.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Демо работы с hibernate (без абстракций) должно ")
class DemoTest  {

    @DisplayName(" показывать, что изменение persistent объекта внутри транзакции приводит к его изменению в БД")
    @Test
    void shouldUpdatePersistentEntityInDBWhenChangedFieldsInTransaction() {
    }
}
