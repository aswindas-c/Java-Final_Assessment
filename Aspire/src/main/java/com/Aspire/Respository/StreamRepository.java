package com.Aspire.Respository;

import java.util.List;

import com.Aspire.model.Stream;

public interface StreamRepository {
    Stream findByName(String name);
    Stream save(Stream stream);
    List<Stream> findAll();
}
