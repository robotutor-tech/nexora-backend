package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.services.RoleService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/iam/roles")
class RoleController(private val roleService: RoleService) {

}