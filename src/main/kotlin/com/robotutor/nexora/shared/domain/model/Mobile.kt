package com.robotutor.nexora.shared.domain.model

@JvmInline
value class Mobile(val value: String) {
  init {
    require(value.matches(Regex("^[0-9]{10}$"))) {
      "Invalid mobile number format: must be exactly 10 digits (Indian mobile)"
    }
  }
}
