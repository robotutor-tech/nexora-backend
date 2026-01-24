package com.robotutor.nexora.module.automation.domain.vo.component

import com.robotutor.nexora.shared.domain.vo.FeedId

data class FeedControl(
    val feedId: FeedId,
    val operator: ComparisonOperator,
    val value: Int
) : Trigger, ConditionSpecification<FeedControl> {
    override val type: ComponentType = ComponentType.FEED_CONTROL
    override fun isSatisfiedBy(candidate: FeedControl): Boolean {
        return feedId == candidate.feedId && operator == candidate.operator && value == candidate.value
    }
}

enum class ComparisonOperator {
    GREATER_THAN, LESS_THAN, EQUAL, NOT_EQUAL, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL
}