package com.hellmund.meetingtalkdetector.data

data class Response(
    val timestamp: Long,
    val talking: List<Int>
)
