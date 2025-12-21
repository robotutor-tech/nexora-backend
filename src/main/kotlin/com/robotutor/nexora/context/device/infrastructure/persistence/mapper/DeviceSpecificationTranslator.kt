package com.robotutor.nexora.context.device.infrastructure.persistence.mapper

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.specification.DeviceByPremisesSpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceByRegisteredBySpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceByStateSpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceSpecification
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.BaseSpecificationTranslator
import org.springframework.data.mongodb.core.query.Criteria

object DeviceSpecificationTranslator :
    BaseSpecificationTranslator<DeviceAggregate, DeviceSpecification>("deviceId") {
    override fun translateLeaf(specification: DeviceSpecification): Criteria {
        return when (specification) {
            is DeviceByPremisesSpecification -> Criteria.where("premisesId").`is`(specification.premisesId.value)
            is DeviceByRegisteredBySpecification -> Criteria.where("registeredBy").`is`(specification.actorId.value)
            is DeviceByStateSpecification -> Criteria.where("state").`is`(specification.state)
        }
    }
}