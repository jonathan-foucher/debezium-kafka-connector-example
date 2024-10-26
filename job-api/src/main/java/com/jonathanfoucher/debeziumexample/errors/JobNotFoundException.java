package com.jonathanfoucher.debeziumexample.errors;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long id) {
        super("Job not found for id=" + id);
    }
}
