package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class WaitConfigDocument(val duration: Int) : ConfigDocument(ConfigType.WAIT)
