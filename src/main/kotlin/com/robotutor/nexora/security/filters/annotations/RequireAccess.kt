package com.robotutor.nexora.security.filters.annotations


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
}

enum class ActionType {
    READ,
    UPDATE,
    DELETE,
    LIST,
    CREATE,
    CONTROL,
}
