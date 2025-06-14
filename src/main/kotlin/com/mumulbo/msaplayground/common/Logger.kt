package com.mumulbo.msaplayground.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 클래스별 Logger 자동 바인딩
 */
inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)
