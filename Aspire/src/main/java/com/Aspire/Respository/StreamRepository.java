package com.Aspire.Respository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.Aspire.model.Stream;

public interface StreamRepository extends JpaRepository<Stream,String>{
    Stream findByName(String name);
}
