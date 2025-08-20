package com.robotutor.nexora.modules.widget.models

import com.robotutor.nexora.common.security.service.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    WIDGET_ID(12),
}
