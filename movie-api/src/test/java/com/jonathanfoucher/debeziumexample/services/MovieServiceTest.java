package com.jonathanfoucher.debeziumexample.services;

import com.jonathanfoucher.debeziumexample.data.dto.MovieDto;
import com.jonathanfoucher.debeziumexample.data.model.Movie;
import com.jonathanfoucher.debeziumexample.data.repository.MovieRepository;
import com.jonathanfoucher.debeziumexample.errors.MovieNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(MovieService.class)
class MovieServiceTest {
    @Autowired
    private MovieService movieService;
    @MockBean
    private MovieRepository movieRepository;

    private static final Long ID = 15L;
    private static final String TITLE = "Some movie";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2022, 7, 19);

    @Test
    void saveNewMovie() {
        // GIVEN
        MovieDto movie = initMovieDto();
        movie.setId(null);

        Movie returnedMovie = initMovie();

        when(movieRepository.save(any()))
                .thenReturn(returnedMovie);

        // WHEN
        Long result = movieService.saveMovie(movie);

        // THEN
        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository, times(1)).save(movieCaptor.capture());

        Movie savedMovie = movieCaptor.getValue();
        assertNotNull(savedMovie);
        assertNull(savedMovie.getId());
        assertEquals(TITLE, savedMovie.getTitle());
        assertEquals(RELEASE_DATE, savedMovie.getReleaseDate());

        assertEquals(ID, result);
    }

    @Test
    void saveExistingMovie() {
        // GIVEN
        MovieDto movie = initMovieDto();

        Movie returnedMovie = initMovie();

        when(movieRepository.save(any()))
                .thenReturn(returnedMovie);

        // WHEN
        Long result = movieService.saveMovie(movie);

        // THEN
        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository, times(1)).save(movieCaptor.capture());

        Movie savedMovie = movieCaptor.getValue();
        assertNotNull(savedMovie);
        assertEquals(ID, savedMovie.getId());
        assertEquals(TITLE, savedMovie.getTitle());
        assertEquals(RELEASE_DATE, savedMovie.getReleaseDate());

        assertEquals(ID, result);
    }

    @Test
    void deleteMovie() {
        // GIVEN
        Movie movie = initMovie();

        when(movieRepository.findById(ID))
                .thenReturn(Optional.of(movie));

        // WHEN
        movieService.deleteMovie(ID);

        // THEN
        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository, times(1)).findById(ID);
        verify(movieRepository, times(1)).delete(movieCaptor.capture());

        Movie deletedMovie = movieCaptor.getValue();
        assertNotNull(deletedMovie);
        assertEquals(ID, deletedMovie.getId());
        assertEquals(TITLE, deletedMovie.getTitle());
        assertEquals(RELEASE_DATE, deletedMovie.getReleaseDate());
    }

    @Test
    void deleteMovieWithNotFoundException() {
        // GIVEN
        when(movieRepository.findById(ID))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> movieService.deleteMovie(ID))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessage("Movie not found for id=" + ID);

        verify(movieRepository, times(1)).findById(ID);
        verify(movieRepository, never()).delete(any());
    }

    private Movie initMovie() {
        Movie movieValue = new Movie();
        movieValue.setId(ID);
        movieValue.setTitle(TITLE);
        movieValue.setReleaseDate(RELEASE_DATE);
        return movieValue;
    }

    private MovieDto initMovieDto() {
        MovieDto movieValue = new MovieDto();
        movieValue.setId(ID);
        movieValue.setTitle(TITLE);
        movieValue.setReleaseDate(RELEASE_DATE);
        return movieValue;
    }
}
