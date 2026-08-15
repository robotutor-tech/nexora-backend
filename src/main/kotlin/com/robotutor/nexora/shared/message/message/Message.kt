package com.robotutor.nexora.shared.message.message

import com.robotutor.nexora.shared.message.config.EventName

data class Message(val topic: EventName, val value: String)
