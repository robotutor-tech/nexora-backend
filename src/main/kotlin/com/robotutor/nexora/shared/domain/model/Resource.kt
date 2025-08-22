package com.robotutor.nexora.shared.domain.model

enum class ResourceType {
    DEVICE,
    FEED,
    PREMISES,
    WIDGET,
    ZONE,
    AUTOMATION_RULE,
    AUTOMATION_TRIGGER,
    AUTOMATION_CONDITION,
    AUTOMATION_ACTION,
}

enum class ActionType {
    READ,
    UPDATE,
    DELETE,
    LIST,
    CREATE,
    CONTROL,
}

enum class RoleType {
    DEVICE,
    CUSTOM,
    GUEST,
    USER,
    ADMIN,
    OWNER,
}