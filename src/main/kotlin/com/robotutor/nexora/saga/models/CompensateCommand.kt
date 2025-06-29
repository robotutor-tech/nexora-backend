package com.robotutor.nexora.saga.models

data class CompensateCommand(val sagaId: String, val resourceId: String, var error: String? = null)