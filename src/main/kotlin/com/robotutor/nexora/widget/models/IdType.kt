package com.robotutor.nexora.widget.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    WIDGET_ID(12),
}
