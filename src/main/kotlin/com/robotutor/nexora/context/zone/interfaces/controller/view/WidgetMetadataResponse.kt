package com.robotutor.nexora.context.zone.interfaces.controller.view

sealed interface WidgetMetadataResponse {
    val type: String
}

class ToggleWidgetMetadataResponse : WidgetMetadataResponse {
    override val type: String = "TOGGLE"
}

data class SliderWidgetMetadataResponse(val min: Int, val max: Int, val step: Int) : WidgetMetadataResponse {
    override val type: String = "SLIDER"
}