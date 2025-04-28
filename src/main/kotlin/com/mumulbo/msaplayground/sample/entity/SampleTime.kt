package com.mumulbo.msaplayground.sample.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "sample_time")
class SampleTime(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "current_time_value", nullable = false) // <-- 컬럼명 변경
    val currentTime: LocalDateTime

) {
    protected constructor() : this(currentTime = LocalDateTime.now())
}
