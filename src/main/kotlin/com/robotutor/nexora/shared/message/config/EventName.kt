package com.robotutor.nexora.shared.message.config

enum class EventName(val topic: String) {
    AUDITORY("event.auditory"),
    AUTOMATION_REGISTERED("automation.registered"),
    USER_REGISTERED("user.registered"),
    USER_ACTIVATED("user.activated"),
    USER_COMPENSATED("user.compensated"),
    ZONE_CREATED("zone.created"),
    PREMISES_OWNER_REGISTRATION_FAILED("premes.consumer.registered"),
    PREMISES_OWNER_REGISTERED("premes.consumer.registered"),
    PREMISES_REGISTERED("premes.registered"),
    IDENTITY_ACCOUNT_CREDENTIAL_UPDATED("identity.account.credential.updated"),
    IDENTITY_ACCOUNT_AUTHENTICATED("identity.account.authenticated"),
    IDENTITY_ACCOUNT_REGISTERED_DEVICE("identity.account.registered.device"),
    IDENTITY_ACCOUNT_REGISTERED_USER("identity.account.registered.user"),
    IDENTITY_ACCOUNT_REGISTRATION_FAILED_DEVICE("identity.account.registration.failed.device"),
    IDENTITY_ACCOUNT_REGISTRATION_FAILED_USER("identity.account.registration.failed.user"),
    IDENTITY_ACTOR_AUTHENTICATED("identity.actor.authenticated"),
    IDENTITY_PREMISES_OWNER_REGISTERED("identity.premises.owner.registered"),
    IDENTITY_PREMISES_OWNER_REGISTRATION_FAILED("identity.premises.owner.registration.failed"),
    ACCOUNT_REGISTRATION_FAILED("account.registration.failed"),
    ACCOUNT_CREATED("account.created"),
    REGISTRATION_COMPLETED("registration.completed"),
    ;

    override fun toString(): String {
        return topic
    }

    companion object {
        fun from(topic: String): EventName {
            return entries.firstOrNull { it.topic == topic }
                ?: throw IllegalArgumentException("Unknown event topic: $topic")
        }
    }
}
