package com.robotutor.nexora.module.automation.domain.vo.component

import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.shared.domain.utility.validation
import java.util.concurrent.TimeUnit

data class Wait(val duration: Long, val unit: TimeUnit) : Action, ComponentData<Wait> {
    override val type: ComponentType = ComponentType.WAIT

    init {
        val seconds = this.toSeconds().duration
        validation(seconds <= 0) { "Wait duration must be greater than 0 seconds" }
        validation(duration > 7200) { "Wait duration must be less than 2 hours" }
    }


    fun toSeconds(): Wait {
        if(unit == TimeUnit.SECONDS) return this
        return Wait(unit.convert(duration, TimeUnit.SECONDS), TimeUnit.SECONDS)
    }
}