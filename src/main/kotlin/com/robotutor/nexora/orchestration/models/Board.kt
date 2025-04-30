package com.robotutor.nexora.orchestration.models

import com.robotutor.nexora.orchestration.gateway.view.FeedView

data class Board(
    val modelNo: String,
    val feeds: List<Feed>,
    val widgets: List<Widget>,
    val rules: List<Rule>
)

data class Feed(val name: String, val type: FeedType, var feedView: FeedView? = null) {
    fun updateFeedView(feedView: FeedView): Feed {
        this.feedView = feedView
        return this
    }
}

enum class FeedType {
    SENSOR,
    ACTUATOR
}

data class Widget(val name: String, val type: WidgetType, val feed: String, val data: Map<String, Any>)
enum class WidgetType {
    TOGGLE
}

data class Rule(val triggers: List<Trigger>, val conditions: List<Condition>, val actions: List<Action>)

data class Trigger(val type: TriggerType, val data: Map<String, Any>)
enum class TriggerType {
    FEED
}

data class Condition(val operator: String)
data class Action(
    val type: ActionType, val data: Map<String, Any>
)

enum class ActionType {
    FEED
}
