package com.robotutor.nexora.modules.widget.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent

sealed class WidgetEvent(name: String) : DomainEvent("widget.$name")