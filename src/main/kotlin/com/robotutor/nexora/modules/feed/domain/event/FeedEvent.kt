package com.robotutor.nexora.modules.feed.domain.event

import com.robotutor.nexora.shared.domain.DomainEvent

sealed class FeedEvent(name: String) : DomainEvent("feed.$name")