package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.services.IAMService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/iam")
class IAMController(private val iamService: IAMService) {

}