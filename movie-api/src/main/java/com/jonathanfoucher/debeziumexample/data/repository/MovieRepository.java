package com.jonathanfoucher.debeziumexample.data.repository;

import com.jonathanfoucher.debeziumexample.data.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}
