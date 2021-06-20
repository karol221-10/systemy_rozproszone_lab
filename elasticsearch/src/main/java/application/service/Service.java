package application.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

interface Service<T> {

    T read(String id) throws IOException;

    Iterable<T> readAll() throws IOException;

    void delete(String id) throws IOException;

    void deleteAll();

    void createOrUpdate(T object, String id) throws IOException;
}