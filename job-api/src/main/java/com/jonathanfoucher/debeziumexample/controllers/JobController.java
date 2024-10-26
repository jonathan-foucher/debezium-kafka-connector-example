package com.jonathanfoucher.debeziumexample.controllers;

import com.jonathanfoucher.debeziumexample.data.dto.JobDto;
import com.jonathanfoucher.debeziumexample.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    @GetMapping
    public List<JobDto> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public JobDto getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @PostMapping
    public Long createJob(@RequestParam String name) {
        return jobService.createJob(name);
    }
}
