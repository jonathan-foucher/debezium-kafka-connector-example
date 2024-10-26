package com.jonathanfoucher.debeziumexample.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jonathanfoucher.debeziumexample.controllers.advices.GlobalControllerExceptionHandler;
import com.jonathanfoucher.debeziumexample.data.dto.MovieDto;
import com.jonathanfoucher.debeziumexample.errors.MovieNotFoundException;
import com.jonathanfoucher.debeziumexample.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig({MovieController.class, GlobalControllerExceptionHandler.class})
class MovieControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private MovieController movieController;
    @Autowired
    private GlobalControllerExceptionHandler globalControllerExceptionHandler;
    @MockBean
    private MovieService movieService;

    private static final String MOVIES_PATH = "/movies";
    private static final String MOVIES_WITH_ID_PATH = "/movies/{movie_id}";
    private static final Long ID = 15L;
    private static final String TITLE = "Some movie";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2022, 7, 19);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void initEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(movieController)
                .setControllerAdvice(globalControllerExceptionHandler)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void saveMovie() throws Exception {
        // GIVEN
        MovieDto movie = new MovieDto();
        movie.setId(ID);
        movie.setTitle(TITLE);
        movie.setReleaseDate(RELEASE_DATE);

        // WHEN / THEN
        mockMvc.perform(post(MOVIES_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie))
                )
                .andExpect(status().isOk());

        ArgumentCaptor<MovieDto> movieCaptor = ArgumentCaptor.forClass(MovieDto.class);
        verify(movieService, times(1)).saveMovie(movieCaptor.capture());

        MovieDto savedMovie = movieCaptor.getValue();
        assertNotNull(savedMovie);
        assertEquals(ID, savedMovie.getId());
        assertEquals(TITLE, savedMovie.getTitle());
        assertEquals(RELEASE_DATE, savedMovie.getReleaseDate());
    }

    @Test
    void deleteMovie() throws Exception {
        // WHEN / THEN
        mockMvc.perform(delete(MOVIES_WITH_ID_PATH, ID))
                .andExpect(status().isOk());

        verify(movieService, times(1)).deleteMovie(ID);
    }

    @Test
    void deleteMovieWithNotFoundException() throws Exception {
        // GIVEN
        doThrow(new MovieNotFoundException(ID))
                .when(movieService).deleteMovie(ID);

        // WHEN / THEN
        mockMvc.perform(delete(MOVIES_WITH_ID_PATH, ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Movie not found for id=" + ID + "\""));

        verify(movieService, times(1)).deleteMovie(ID);
    }
}