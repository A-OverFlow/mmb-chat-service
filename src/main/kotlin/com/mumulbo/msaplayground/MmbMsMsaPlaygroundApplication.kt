package com.mumulbo.msaplayground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["com.mumulbo.msaplayground.service"])
open class MmbMsMsaPlaygroundApplication

fun main(args: Array<String>) {
    runApplication<MmbMsMsaPlaygroundApplication>(*args)
}