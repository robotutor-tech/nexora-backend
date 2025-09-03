package com.robotutor.nexora.modules.widget.domain.entity

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int): IdSequenceType {
    WIDGET_ID(12),
}