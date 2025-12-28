package com.robotutor.nexora.context.zone.domain.vo

sealed interface WidgetMetadata

class ToggleWidgetMetadata : WidgetMetadata

data class SliderWidgetMetadata(
    val min: Int,
    val max: Int,
    val step: Int,
) : WidgetMetadata