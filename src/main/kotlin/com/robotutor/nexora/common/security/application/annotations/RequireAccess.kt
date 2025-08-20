package com.robotutor.nexora.common.security.application.annotations


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireAccess(
    val action: ActionType,
    val resource: ResourceType,
    val idParam: String = ""
)

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
