package com.mumulbo.msaplayground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class MmbMsMsaPlaygroundApplication

fun main(args: Array<String>) {
    runApplication<MmbMsMsaPlaygroundApplication>(*args)
}