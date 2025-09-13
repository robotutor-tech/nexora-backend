package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.shared.domain.validation

data class Time(val hour: Int, val minute: Int) {
    init {
        validation(hour in 0..23 && minute in 0..59) { "Time must be between 00:00 and 23:59" }
    }

    companion object {
        fun from(time: String): Time {
            val hourAndMinute = time.split(":")
            return Time(hour = hourAndMinute[0].toInt(), minute = hourAndMinute[1].toInt())
        }
    }

    fun toTimeString(): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

data class VoiceCommand(val command: String) {
    init {
        validation(command.isNotEmpty()) { "Voice command cannot be empty" }
    }
}

data class VoiceCommands(val commands: List<VoiceCommand>) {
    init {
        validation(commands.isNotEmpty()) { "Voice commands cannot be empty" }
    }
}
