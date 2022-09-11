package ru.practicum.shareit.utils;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // каждый раз создаём новый объект, чтобы проходили тесты
public class IdGenerator {
    private int nextId = 1;

    public int getNextId() {
        return nextId++;
    }
}
