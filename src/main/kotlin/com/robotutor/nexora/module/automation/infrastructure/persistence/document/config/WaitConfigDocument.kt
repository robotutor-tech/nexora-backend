package com.robotutor.nexora.module.automation.infrastructure.persistence.document.config

data class WaitConfigDocument(val duration: Int) : ConfigDocument(ConfigType.WAIT)
