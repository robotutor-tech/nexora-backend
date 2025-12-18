package com.robotutor.nexora.shared.domain.vo

enum class ResourceType {
    AUTOMATION,
    AUTOMATION_RULE,
    DEVICE,
    FEED,
    INVITATION,
    PREMISES,
    WIDGET,
    ZONE,
}

enum class ActionType {
    READ,
    CONTROL,
    WRITE,
    DELETE,
}