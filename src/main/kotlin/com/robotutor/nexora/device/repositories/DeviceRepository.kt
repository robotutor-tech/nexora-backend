package com.robotutor.nexora.device.repositories

import com.robotutor.nexora.device.models.Device
import com.robotutor.nexora.device.models.DeviceId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository: ReactiveCrudRepository<Device, DeviceId> {

}
