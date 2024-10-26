package com.jonathanfoucher.debeziumexample.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jonathanfoucher.debeziumexample.data.dto.JobDto;
import com.jonathanfoucher.debeziumexample.data.model.Job;
import com.jonathanfoucher.debeziumexample.data.repository.JobRepository;
import com.jonathanfoucher.debeziumexample.errors.JobNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(JobService.class)
class JobServiceTest {
    @SpyBean
    @Autowired
    private JobService jobService;
    @MockBean
    private JobRepository jobRepository;

    private static final Long ID = 15L;
    private static final String NAME = "SOME_JOB";

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllJobs() {
        // GIVEN
        Job job = initJob();

        when(jobRepository.findAll())
                .thenReturn(List.of(job));

        // WHEN
        List<JobDto> results = jobService.getAllJobs();

        // THEN
        verify(jobRepository, times(1)).findAll();

        assertNotNull(results);
        assertEquals(1, results.size());
        checkJobDto(results.getFirst());
    }

    @Test
    void getAllJobsWithEmptyResult() {
        // GIVEN
        when(jobRepository.findAll())
                .thenReturn(emptyList());

        // WHEN
        List<JobDto> results = jobService.getAllJobs();

        // THEN
        verify(jobRepository, times(1)).findAll();

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    void getJobById() {
        // GIVEN
        Job job = initJob();

        when(jobRepository.findById(ID))
                .thenReturn(Optional.of(job));

        // WHEN
        JobDto result = jobService.getJobById(ID);

        // THEN
        verify(jobRepository, times(1)).findById(ID);

        checkJobDto(result);
    }

    @Test
    void getJobByIdWithJobNotFound() {
        // GIVEN
        when(jobRepository.findById(ID))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> jobService.getJobById(ID))
                .isInstanceOf(JobNotFoundException.class)
                .hasMessage("Job not found for id=" + ID);
    }

    @Test
    void createJob() {
        // GIVEN
        Job job = initJob();

        when(jobRepository.save(any()))
                .thenReturn(job);

        // WHEN
        Long result = jobService.createJob(NAME);

        // THEN
        ArgumentCaptor<Job> capturedJob = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository, times(1)).save(capturedJob.capture());

        assertEquals(ID, result);

        Job savedJob = capturedJob.getValue();
        assertNotNull(savedJob);
        assertNull(savedJob.getId());
        assertEquals(NAME, savedJob.getName());
    }

    private Job initJob() {
        Job job = new Job();
        job.setId(ID);
        job.setName(NAME);
        return job;
    }

    private void checkJobDto(JobDto job) {
        assertNotNull(job);
        assertEquals(ID, job.getId());
        assertEquals(NAME, job.getName());
    }
}
