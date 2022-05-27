package com.test.task;

public interface ParserTask<T> {
    public T parse(String line);
}
