package com.robotutor.nexora.module.feed.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class FeedValueRange(val mode: FeedMode = FeedMode.DIGITAL, val min: Int = 0, val max: Int = 1) : ValueObject {
    init {
        validation(min >= max) { "Min value should be less than max value" }
        validation(mode == FeedMode.DIGITAL && (min != 0 || max != 1)) { "Range should be 0 to 1 for Digital mode" }
    }

}

enum class FeedMode {
    ANALOG,
    DIGITAL
}

