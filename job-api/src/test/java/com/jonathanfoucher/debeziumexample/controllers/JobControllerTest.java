package com.jonathanfoucher.debeziumexample.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonathanfoucher.debeziumexample.controllers.advices.GlobalControllerExceptionHandler;
import com.jonathanfoucher.debeziumexample.data.dto.JobDto;
import com.jonathanfoucher.debeziumexample.errors.JobNotFoundException;
import com.jonathanfoucher.debeziumexample.services.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig({JobController.class, GlobalControllerExceptionHandler.class})
class JobControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private JobController jobController;
    @Autowired
    private GlobalControllerExceptionHandler globalControllerExceptionHandler;
    @MockBean
    private JobService jobService;

    private static final String JOB_PATH = "/jobs";
    private static final String JOB_ID_PATH = "/jobs/{id}";
    private static final Long ID = 15L;
    private static final String NAME = "SOME_JOB";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void initEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController)
                .setControllerAdvice(globalControllerExceptionHandler)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void getAllJobs() throws Exception {
        // GIVEN
        JobDto job = initJobDto();

        when(jobService.getAllJobs())
                .thenReturn(List.of(job));

        // WHEN / THEN
        mockMvc.perform(get(JOB_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(job))));

        verify(jobService, times(1)).getAllJobs();
    }

    @Test
    void getAllJobsWithEmptyResult() throws Exception {
        // GIVEN
        when(jobService.getAllJobs())
                .thenReturn(emptyList());

        // WHEN / THEN
        mockMvc.perform(get(JOB_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList())));

        verify(jobService, times(1)).getAllJobs();
    }

    @Test
    void getJobById() throws Exception {
        // GIVEN
        JobDto job = initJobDto();

        when(jobService.getJobById(ID))
                .thenReturn(job);

        // WHEN / THEN
        mockMvc.perform(get(JOB_ID_PATH, ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(job)));

        verify(jobService, times(1)).getJobById(ID);
    }

    @Test
    void getJobByIdWithJobNotFound() throws Exception {
        // GIVEN
        when(jobService.getJobById(ID))
                .thenThrow(new JobNotFoundException(ID));

        // WHEN / THEN
        mockMvc.perform(get(JOB_ID_PATH, ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string("\"Job not found for id=" + ID + "\""));

        verify(jobService, times(1)).getJobById(ID);
    }

    @Test
    void createJob() throws Exception {
        // GIVEN
        when(jobService.createJob(NAME))
                .thenReturn(ID);

        // WHEN / THEN
        mockMvc.perform(post(JOB_PATH)
                        .queryParam("name", NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(ID)));

        verify(jobService, times(1)).createJob(NAME);
    }

    private JobDto initJobDto() {
        JobDto job = new JobDto();
        job.setId(ID);
        job.setName(NAME);
        return job;
    }
}
