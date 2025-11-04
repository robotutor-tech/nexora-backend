package com.robotutor.nexora.modules.device.application

import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.modules.device.domain.entity.FeedIds
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.model.*
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

class DeviceUseCaseTest {
    private val deviceRepository = mockk<DeviceRepository>()
    private val deviceUseCase = DeviceUseCase(deviceRepository)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should get devices`() {
        val premisesId = PremisesId("prem-1")
        val d1 = Device(
            DeviceId("d1"),
            premisesId,
            Name("DeviceOne"),
            ModelNo("M1"),
            SerialNo("S1"),
            com.robotutor.nexora.modules.device.domain.entity.DeviceType.DEVICE,
            createdBy = ActorId("a1"),
            createdAt = Instant.parse("2023-01-01T00:00:00Z")
        )
        val d2 = Device(
            DeviceId("d2"),
            premisesId,
            Name("DeviceTwo"),
            ModelNo("M2"),
            SerialNo("S2"),
            com.robotutor.nexora.modules.device.domain.entity.DeviceType.DEVICE,
            createdBy = ActorId("a1"),
            createdAt = Instant.parse("2023-01-01T00:00:00Z")
        )
        every {
            deviceRepository.findAllByPremisesIdAndDeviceIdsIn(
                premisesId,
                listOf(DeviceId("d1"), DeviceId("d2"))
            )
        } returns Flux.just(d1, d2)

        val flux = deviceUseCase.getDevices(premisesId, listOf(DeviceId("d1"), DeviceId("d2")))
        StepVerifier.create(flux).expectNext(d1).expectNext(d2).verifyComplete()

        verify(exactly = 1) {
            deviceRepository.findAllByPremisesIdAndDeviceIdsIn(
                premisesId,
                listOf(DeviceId("d1"), DeviceId("d2"))
            )
        }
    }

    @Test
    fun `should get device by id`() {
        val premisesId = PremisesId("prem-1")
        val device = Device(
            DeviceId("d1"),
            premisesId,
            Name("DeviceOne"),
            ModelNo("M1"),
            SerialNo("S1"),
            com.robotutor.nexora.modules.device.domain.entity.DeviceType.DEVICE,
            createdBy = ActorId("a1"),
            createdAt = Instant.parse("2023-01-01T00:00:00Z")
        )
        every { deviceRepository.findByDeviceId(DeviceId("d1")) } returns Mono.just(device)

        val mono = deviceUseCase.getDevice(DeviceId("d1"))
        StepVerifier.create(mono).expectNext(device).verifyComplete()

        verify(exactly = 1) { deviceRepository.findByDeviceId(DeviceId("d1")) }
    }

    @Test
    fun `should update device feeds`() {
        val premisesId = PremisesId("prem-1")
        val device = Device(
            DeviceId("d1"),
            premisesId,
            Name("DeviceOne"),
            ModelNo("M1"),
            SerialNo("S1"),
            com.robotutor.nexora.modules.device.domain.entity.DeviceType.DEVICE,
            createdBy = ActorId("a1"),
            createdAt = Instant.parse("2023-01-01T00:00:00Z")
        )
        val updated = DeviceId("d1")
        val feeds = FeedIds(listOf(FeedId("f1"), FeedId("f2")))

        every { deviceRepository.findByPremisesIdAndDeviceId(premisesId, updated) } returns Mono.just(device)
        every { deviceRepository.save(any()) } answers { Mono.just(firstArg()) }

        val actor = ActorData(
            ActorId("a1"),
            Role(RoleId("r1"), premisesId, Name("Role"), RoleType.USER),
            premisesId,
            ActorPrincipalType.USER,
            UserData(UserId("u1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
        )
        val mono = deviceUseCase.updateDeviceFeeds(updated, feeds, actor)

        StepVerifier.create(mono)
            .assertNext {
                it.feedIds.feeds.map { f -> f.value } shouldBe listOf("f1", "f2")
            }
            .verifyComplete()

        verify(exactly = 1) {
            deviceRepository.findByPremisesIdAndDeviceId(premisesId, updated)
            deviceRepository.save(any())
        }
    }
}

