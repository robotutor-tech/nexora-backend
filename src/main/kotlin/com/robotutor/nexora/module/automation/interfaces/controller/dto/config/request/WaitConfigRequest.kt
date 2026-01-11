package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request

data class WaitConfigRequest(val duration: Int) : ConfigRequest(ConfigType.WAIT)
