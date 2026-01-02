package com.robotutor.nexora.module.device.infrastructure.persistence.mapper

import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.module.device.domain.specification.DeviceByPremisesIdSpecification
import com.robotutor.nexora.module.device.domain.specification.DeviceByRegisteredBySpecification
import com.robotutor.nexora.module.device.domain.specification.DeviceByStateSpecification
import com.robotutor.nexora.module.device.domain.specification.DeviceSpecification
import com.robotutor.nexora.common.persistence.mapper.BaseSpecificationTranslator
import com.robotutor.nexora.module.device.domain.specification.DeviceByDeviceIdSpecification
import org.springframework.data.mongodb.core.query.Criteria

object DeviceSpecificationTranslator : BaseSpecificationTranslator<DeviceAggregate, DeviceSpecification>("deviceId") {
    override fun translateLeaf(specification: DeviceSpecification): Criteria {
        return when (specification) {
            is DeviceByPremisesIdSpecification -> Criteria.where("premisesId").`is`(specification.premisesId.value)
            is DeviceByRegisteredBySpecification -> Criteria.where("registeredBy").`is`(specification.actorId.value)
            is DeviceByStateSpecification -> Criteria.where("state").`is`(specification.state)
            is DeviceByDeviceIdSpecification -> Criteria.where("deviceId").`is`(specification.deviceId.value)
        }
    }
}