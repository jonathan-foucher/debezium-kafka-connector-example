package com.jonathanfoucher.debeziumexample.services;

import com.jonathanfoucher.debeziumexample.data.dto.JobDto;
import com.jonathanfoucher.debeziumexample.data.model.Job;
import com.jonathanfoucher.debeziumexample.data.repository.JobRepository;
import com.jonathanfoucher.debeziumexample.errors.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobService {
    private final JobRepository jobRepository;

    public List<JobDto> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::convertEntityToDto)
                .toList();
    }

    public JobDto getJobById(Long id) {
        return jobRepository.findById(id)
                .map(this::convertEntityToDto)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    public Long createJob(String name) {
        Job job = createJobEntity(name);
        return jobRepository.save(job)
                .getId();
    }

    private JobDto convertEntityToDto(Job entity) {
        JobDto dto = new JobDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    private Job createJobEntity(String name) {
        Job entity = new Job();
        entity.setName(name);
        return entity;
    }
}
