//package com.robotutor.nexora.modules.premises.application
//
//import com.robotutor.nexora.modules.premises.application.command.CreatePremisesCommand
//import com.robotutor.nexora.modules.premises.application.command.RegisterPremisesResourceCommand
//import com.robotutor.nexora.modules.premises.application.dto.ActorWithRolesPremises
//import com.robotutor.nexora.modules.premises.application.facade.ActorResourceFacade
//import com.robotutor.nexora.modules.premises.application.facade.PremisesResourceFacade
//import com.robotutor.nexora.modules.premises.application.facade.dto.ActorWithRoles
//import com.robotutor.nexora.modules.premises.application.facade.dto.Role
//import com.robotutor.nexora.modules.premises.domain.entity.Address
//import com.robotutor.nexora.modules.premises.domain.entity.IdType
//import com.robotutor.nexora.modules.premises.domain.entity.Premises
//import com.robotutor.nexora.modules.premises.domain.event.PremisesEvent
//import com.robotutor.nexora.modules.premises.domain.repository.PremisesRepository
//import com.robotutor.nexora.shared.domain.model.*
//import com.robotutor.nexora.shared.domain.service.IdGeneratorService
//import com.robotutor.nexora.testUtils.assertNextWith
//import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
//import io.kotest.matchers.shouldBe
//import io.mockk.*
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//import java.time.Instant
//
//class PremisesUseCaseTest {
//    private val idGeneratorService = mockk<IdGeneratorService>()
//    private val premisesRepository = mockk<PremisesRepository>()
//    private val premisesResourceFacade = mockk<PremisesResourceFacade>()
//    private val actorResourceFacade = mockk<ActorResourceFacade>()
//    private val eventPublisherDeprecated = mockk<EventPublisherDeprecated<PremisesEvent>>()
//
//    private val premisesUseCase = PremisesUseCase(
//        idGeneratorService,
//        premisesRepository,
//        premisesResourceFacade,
//        actorResourceFacade,
//        eventPublisherDeprecated
//    )
//
//    @BeforeEach
//    fun setup() {
//        clearAllMocks()
//        val fixedInstant = Instant.parse("2023-01-01T00:00:00Z")
//        mockkStatic(Instant::class)
//        every { Instant.now() } returns fixedInstant
//    }
//
//    @AfterEach
//    fun tearDown() {
//        clearAllMocks()
//    }
//
//    @Test
//    fun `should create premises successfully`() {
//        val owner = UserData(UserId("user-1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
//        val address = Address("street", "city", "state", "country", "12345")
//        val cmd = CreatePremisesCommand(Name("Home"), address, owner)
//        val generatedPremisesId = PremisesId("prem-0001")
//
//        val savedPremises = Premises.register(generatedPremisesId, Name("Home"), address, owner.userId)
//        val actorWithRoles = ActorWithRoles(
//            actorId = ActorId("actor-1"),
//            premisesId = generatedPremisesId,
//            roles = listOf(Role(RoleId("role-1"), generatedPremisesId, Name("Owner"), RoleType.OWNER)),
//            principalType = ActorPrincipalType.USER,
//            principal = UserContext(owner.userId)
//        )
//
//        every { idGeneratorService.generateId(IdType.PREMISE_ID, PremisesId::class.java) } returns Mono.just(generatedPremisesId)
//        every { premisesRepository.save(any()) } answers { Mono.just(firstArg()) }
//        // No domain events yet in Premises.register, so eventPublisher.publish won't be invoked; mock anyway
//        every { eventPublisherDeprecated.publish(any()) } returns Mono.just(Unit)
//        every { premisesResourceFacade.register(RegisterPremisesResourceCommand(generatedPremisesId, owner)) } returns Mono.just(actorWithRoles)
//
//        val mono = premisesUseCase.createPremises(cmd)
//
//        assertNextWith(mono) {
//            it shouldBe ActorWithRolesPremises(actorWithRoles, savedPremises)
//        }
//
//        verify(exactly = 1) {
//            idGeneratorService.generateId(IdType.PREMISE_ID, PremisesId::class.java)
//            premisesRepository.save(match { it.premisesId == generatedPremisesId && it.name == Name("Home") && it.address == address && it.owner == owner.userId })
//            premisesResourceFacade.register(RegisterPremisesResourceCommand(generatedPremisesId, owner))
//        }
//        // publishEvents over empty list, so no guaranteed calls to eventPublisher.publish
//        confirmVerified(idGeneratorService, premisesRepository, premisesResourceFacade)
//    }
//
//    @Test
//    fun `should get all premises for user`() {
//        val user = UserData(UserId("user-1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
//        val p1Id = PremisesId("prem-1")
//        val p2Id = PremisesId("prem-2")
//        val actorWithRoles1 = ActorWithRoles(ActorId("actor-1"), p1Id, listOf(Role(RoleId("r1"), p1Id, Name("Owner"), RoleType.OWNER)), ActorPrincipalType.USER, UserContext(user.userId))
//        val actorWithRoles2 = ActorWithRoles(ActorId("actor-2"), p2Id, listOf(Role(RoleId("r2"), p2Id, Name("Guest"), RoleType.GUEST)), ActorPrincipalType.USER, UserContext(user.userId))
//        val premises1 = Premises.register(p1Id, Name("Home"), Address("s1", "c1", "st1", "ct1", "pc1"), user.userId)
//        val premises2 = Premises.register(p2Id, Name("Office"), Address("s2", "c2", "st2", "ct2", "pc2"), user.userId)
//
//        every { actorResourceFacade.getActors(user) } returns Flux.just(actorWithRoles1, actorWithRoles2)
//        every { premisesRepository.findAllByPremisesIdIn(match { it.toSet() == setOf(p1Id, p2Id) }) } returns Flux.just(premises1, premises2)
//
//        val flux = premisesUseCase.getAllPremises(user)
//
//        StepVerifier.create(flux)
//            .recordWith { mutableListOf() }
//            .thenConsumeWhile({ true }) { }
//            .consumeRecordedWith { list ->
//                list.map { it.premises }.toSet() shouldContainExactlyInAnyOrder setOf(premises1, premises2)
//                list.map { it.actor.premisesId }.toSet() shouldBe setOf(p1Id, p2Id)
//            }
//            .verifyComplete()
//
//        verify(exactly = 1) {
//            actorResourceFacade.getActors(user)
//            premisesRepository.findAllByPremisesIdIn(match { it.toSet() == setOf(p1Id, p2Id) })
//        }
//    }
//}
//
