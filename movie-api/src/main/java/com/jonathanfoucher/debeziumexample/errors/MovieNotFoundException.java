package com.jonathanfoucher.debeziumexample.errors;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(Long movieId) {
        super("Movie not found for id=" + movieId);
    }
}
