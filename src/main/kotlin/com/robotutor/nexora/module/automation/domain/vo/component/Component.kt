package com.robotutor.nexora.module.automation.domain.vo.component

sealed interface Component
sealed interface Action : Component
sealed interface Trigger : Component
sealed interface Condition : Component
