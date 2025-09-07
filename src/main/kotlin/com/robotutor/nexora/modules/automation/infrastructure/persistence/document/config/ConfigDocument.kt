package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

sealed class ConfigDocument(val type: ConfigType)

