package com.robotutor.nexora.shared.message.config

enum class EventName(val topic: String) {
    AUDITORY("auditory"),
    AUTOMATION_REGISTERED("automation.registered"),
    USER_REGISTERED("user.registered"),
    USER_ACTIVATED("user.activated"),
    USER_COMPENSATED("user.compensated"),
    ZONE_CREATED("zone.created"),
    PREMISES_OWNER_REGISTRATION_FAILED("premes.consumer.registered"),
    PREMISES_OWNER_REGISTERED("premes.consumer.registered"),
    PREMISES_REGISTERED("premes.registered"),
    IDENTITY_CREDENTIAL_UPDATED("identity.credential.updated"),
    ACCOUNT_AUTHENTICATED("account.authenticated"),
    ACTOR_AUTHENTICATED("actor.authenticated"),
    ACCOUNT_REGISTRATION_FAILED("account.registration.failed"),
    ACCOUNT_CREATED("account.created"),
    REGISTRATION_COMPLETED("registration.completed"),
}
