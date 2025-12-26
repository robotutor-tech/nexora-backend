package com.robotutor.nexora.context.zone.domain.vo

interface WidgetMetadata

data class ToggleWidgetMetadata(
    val icon: String,
) : WidgetMetadata

data class SliderWidgetMetadata(
    val min: Int,
    val max: Int,
    val step: Int,
) : WidgetMetadata