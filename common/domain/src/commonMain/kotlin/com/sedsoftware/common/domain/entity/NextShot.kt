package com.sedsoftware.common.domain.entity

import kotlinx.datetime.LocalDateTime

data class NextShot(
    val remindie: Remindie,
    val target: LocalDateTime,
    val isFired: Boolean
)
