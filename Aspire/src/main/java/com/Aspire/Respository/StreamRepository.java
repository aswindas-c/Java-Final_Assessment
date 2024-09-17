package com.Aspire.Respository;

import com.Aspire.model.Stream;

public interface StreamRepository {
    Stream findByName(String name);
    Stream save(Stream stream);
}
