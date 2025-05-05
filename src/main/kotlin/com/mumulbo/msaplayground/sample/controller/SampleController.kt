package com.mumulbo.msaplayground.sample.controller

import com.mumulbo.msaplayground.sample.dto.SampleTimeResponse
import com.mumulbo.msaplayground.sample.service.SampleService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sample")
class SampleController(
    private val sampleService: SampleService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/time")
    fun getCurrentTime(): SampleTimeResponse {
        log.info("🕒 [GET] /api/sample/time called")
        return sampleService.getCurrentTime()
    }

    @GetMapping("/list")
    fun getAllTimes(): List<SampleTimeResponse> {
        log.info("📄 [GET] /api/sample/list called")
        return sampleService.getAllTimes()
    }

    @DeleteMapping
    fun deleteAllTimes() {
        log.info("❌ [DELETE] /api/sample called")
        sampleService.deleteAllTimes()
    }
}
