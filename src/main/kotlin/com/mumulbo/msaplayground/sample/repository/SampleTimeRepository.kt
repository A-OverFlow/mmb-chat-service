package com.mumulbo.msaplayground.sample.repository

import com.mumulbo.msaplayground.sample.entity.SampleTime
import org.springframework.data.jpa.repository.JpaRepository

interface SampleTimeRepository : JpaRepository<SampleTime, Long>
