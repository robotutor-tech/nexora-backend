package com.robotutor.nexora.device.controllers

import com.robotutor.nexora.device.controllers.view.DeviceRequest
import com.robotutor.nexora.security.models.UserPremisesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/devices")
class DeviceController {

    @PostMapping
    fun addDevice(@RequestBody @Validated deviceRequest: DeviceRequest, userPremisesData: UserPremisesData) {

    }
}