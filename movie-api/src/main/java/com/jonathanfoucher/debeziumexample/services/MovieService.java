package com.jonathanfoucher.debeziumexample.services;

import com.jonathanfoucher.debeziumexample.data.dto.MovieDto;
import com.jonathanfoucher.debeziumexample.data.model.Movie;
import com.jonathanfoucher.debeziumexample.data.repository.MovieRepository;
import com.jonathanfoucher.debeziumexample.errors.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public Long saveMovie(MovieDto dto) {
        Movie movie = convertDtoToEntity(dto);
        return movieRepository.save(movie)
                .getId();
    }

    public void deleteMovie(Long movieId) {
        movieRepository.findById(movieId)
                .ifPresentOrElse(movieRepository::delete, () -> {
                    throw new MovieNotFoundException(movieId);
                });
    }

    private Movie convertDtoToEntity(MovieDto dto) {
        Movie entity = new Movie();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setReleaseDate(dto.getReleaseDate());
        return entity;
    }
}
