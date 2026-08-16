package com.robotutor.nexora.shared.message.message

import com.robotutor.nexora.shared.message.persistence.document.KafkaHeader

data class Message(val topic: String, val value: String, val headers: List<KafkaHeader>)
