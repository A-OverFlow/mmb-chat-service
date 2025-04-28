package com.mumulbo.msaplayground.sample.controller

import com.mumulbo.msaplayground.sample.dto.SampleTimeResponse
import com.mumulbo.msaplayground.sample.service.SampleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sample")
class SampleController(
    private val sampleService: SampleService
) {

    @GetMapping("/time")
    fun getCurrentTime(): SampleTimeResponse {
        return sampleService.getCurrentTime()
    }

    @GetMapping("/list")
    fun getAllTimes(): List<SampleTimeResponse> {
        return sampleService.getAllTimes()
    }

    @DeleteMapping
    fun deleteAllTimes() {
        sampleService.deleteAllTimes()
    }
}
