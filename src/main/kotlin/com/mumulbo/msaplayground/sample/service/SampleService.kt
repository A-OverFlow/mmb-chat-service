package com.mumulbo.msaplayground.sample.service

import com.mumulbo.msaplayground.sample.dto.SampleTimeResponse
import com.mumulbo.msaplayground.sample.entity.SampleTime
import com.mumulbo.msaplayground.sample.repository.SampleTimeRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SampleService(
    private val sampleTimeRepository: SampleTimeRepository
) {

    fun getCurrentTime(): SampleTimeResponse {
        val now = LocalDateTime.now()
        sampleTimeRepository.save(SampleTime(currentTime = now))
        return SampleTimeResponse(currentTime = now)
    }

    fun getAllTimes(): List<SampleTimeResponse> {
        return sampleTimeRepository.findAll()
            .map { SampleTimeResponse(currentTime = it.currentTime) }
    }

    fun deleteAllTimes() {
        sampleTimeRepository.deleteAll()
    }
}
