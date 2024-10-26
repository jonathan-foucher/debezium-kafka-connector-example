package com.jonathanfoucher.debeziumexample.controllers;

import com.jonathanfoucher.debeziumexample.data.dto.MovieDto;
import com.jonathanfoucher.debeziumexample.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public Long saveMovie(@RequestBody MovieDto movieValue) {
        return movieService.saveMovie(movieValue);
    }

    @DeleteMapping("/{movie_id}")
    public void deleteMovie(@PathVariable("movie_id") Long movieId) {
        movieService.deleteMovie(movieId);
    }
}