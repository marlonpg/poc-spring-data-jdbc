package com.gambadeveloper.poc_spring_data_jdbc.repository;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    // Spring Data JDBC will implement the CRUD methods:
    // save(), findById(), findAll(), deleteById(), etc.
}