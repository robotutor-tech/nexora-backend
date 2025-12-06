//package com.robotutor.nexora.modules.premises.interfaces.controller
//
//import com.robotutor.nexora.modules.premises.application.PremisesUseCase
//import com.robotutor.nexora.modules.premises.application.command.CreatePremisesCommand
//import com.robotutor.nexora.modules.premises.application.dto.ActorWithRolesPremises
//import com.robotutor.nexora.modules.premises.application.facade.dto.ActorWithRoles
//import com.robotutor.nexora.modules.premises.application.facade.dto.Role
//import com.robotutor.nexora.modules.premises.domain.entity.Address
//import com.robotutor.nexora.modules.premises.domain.entity.Premises
//import com.robotutor.nexora.modules.premises.interfaces.controller.dto.*
//import com.robotutor.nexora.shared.domain.model.*
//import com.robotutor.nexora.testUtils.assertNextWith
//import io.kotest.matchers.shouldBe
//import io.mockk.clearAllMocks
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import java.time.Instant
//
//class PremisesControllerTest {
//    private val mockPremisesUseCase = mockk<PremisesUseCase>()
//    private val controller = PremisesController(mockPremisesUseCase)
//
//    private val user = UserData(UserId("user-1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
//
//    @BeforeEach
//    fun setup() {
//        clearAllMocks()
//    }
//
//    @AfterEach
//    fun tearDown() {
//        clearAllMocks()
//    }
//
//    @Test
//    fun `should create premises`() {
//        val req = PremisesCreateRequest(
//            name = "Home",
//            address = AddressRequest("street", "city", "state", "country", "12345")
//        )
//        val premises = Premises(
//            premisesId = PremisesId("prem-0001"),
//            name = Name("Home"),
//            address = Address("street", "city", "state", "country", "12345"),
//            owner = user.userId,
//            createdAt = Instant.parse("2023-01-01T00:00:00Z"),
//            version = null
//        )
//        val actor = ActorWithRoles(
//            actorId = ActorId("actor-1"),
//            premisesId = premises.premisesId,
//            roles = listOf(Role(RoleId("role-1"), premises.premisesId, Name("Owner"), RoleType.OWNER)),
//            principalType = ActorPrincipalType.USER,
//            principal = UserContext(user.userId)
//        )
//        val returned = ActorWithRolesPremises(actor, premises)
//
//        every { mockPremisesUseCase.createPremises(any()) } returns Mono.just(returned)
//
//        val result = controller.createPremises(req, user)
//
//        assertNextWith(result) {
//            it shouldBe PremisesActorResponse(
//                premisesId = "prem-0001",
//                name = "Home",
//                address = AddressResponse("street", "city", "state", "country", "12345"),
//                createdAt = Instant.parse("2023-01-01T00:00:00Z"),
//                actor = ActorWithRoleResponse(
//                    actorId = "actor-1",
//                    premisesId = "prem-0001",
//                    roles = listOf(RoleResponse("role-1", "Owner", RoleType.OWNER))
//                )
//            )
//            verify(exactly = 1) {
//                mockPremisesUseCase.createPremises(
//                    CreatePremisesCommand(
//                        name = Name("Home"),
//                        address = Address("street", "city", "state", "country", "12345"),
//                        owner = user
//                    )
//                )
//            }
//        }
//    }
//
//    @Test
//    fun `should list premises for user`() {
//        val p1 = Premises(PremisesId("prem-1"), Name("Home"), Address("s1", "c1", "st1", "ct1", "pc1"), user.userId, Instant.parse("2023-01-01T00:00:00Z"))
//        val p2 = Premises(PremisesId("prem-2"), Name("Office"), Address("s2", "c2", "st2", "ct2", "pc2"), user.userId, Instant.parse("2023-01-02T00:00:00Z"))
//        val a1 = ActorWithRoles(ActorId("actor-1"), p1.premisesId, listOf(Role(RoleId("r1"), p1.premisesId, Name("Owner"), RoleType.OWNER)), ActorPrincipalType.USER, UserContext(user.userId))
//        val a2 = ActorWithRoles(ActorId("actor-2"), p2.premisesId, listOf(Role(RoleId("r2"), p2.premisesId, Name("Guest"), RoleType.GUEST)), ActorPrincipalType.USER, UserContext(user.userId))
//
//        every { mockPremisesUseCase.getAllPremises(user) } returns Flux.just(
//            ActorWithRolesPremises(a1, p1), ActorWithRolesPremises(a2, p2)
//        )
//
//        val responses = controller.getPremises(user).collectList().block()!!
//
//        responses.size shouldBe 2
//        responses[0] shouldBe PremisesActorResponse(
//            premisesId = "prem-1",
//            name = "Home",
//            address = AddressResponse("s1", "c1", "st1", "ct1", "pc1"),
//            createdAt = Instant.parse("2023-01-01T00:00:00Z"),
//            actor = ActorWithRoleResponse(
//                actorId = "actor-1",
//                premisesId = "prem-1",
//                roles = listOf(RoleResponse("r1", "Owner", RoleType.OWNER))
//            )
//        )
//        responses[1] shouldBe PremisesActorResponse(
//            premisesId = "prem-2",
//            name = "Office",
//            address = AddressResponse("s2", "c2", "st2", "ct2", "pc2"),
//            createdAt = Instant.parse("2023-01-02T00:00:00Z"),
//            actor = ActorWithRoleResponse(
//                actorId = "actor-2",
//                premisesId = "prem-2",
//                roles = listOf(RoleResponse("r2", "Guest", RoleType.GUEST))
//            )
//        )
//
//        verify(exactly = 1) { mockPremisesUseCase.getAllPremises(user) }
//    }
//
//    @Test
//    fun `should get premises details`() {
//        val premises = Premises(
//            premisesId = PremisesId("prem-1"),
//            name = Name("Home"),
//            address = Address("s1", "c1", "st1", "ct1", "pc1"),
//            owner = user.userId,
//            createdAt = Instant.parse("2023-01-01T00:00:00Z"),
//            version = null
//        )
//        every { mockPremisesUseCase.getPremisesDetails(PremisesId("prem-1")) } returns Mono.just(premises)
//
//        val mono = controller.getPremisesDetails("prem-1")
//
//        assertNextWith(mono) {
//            it shouldBe PremisesResponse(
//                premisesId = "prem-1",
//                name = "Home",
//                address = AddressResponse("s1", "c1", "st1", "ct1", "pc1"),
//                createdAt = Instant.parse("2023-01-01T00:00:00Z")
//            )
//            verify(exactly = 1) { mockPremisesUseCase.getPremisesDetails(PremisesId("prem-1")) }
//        }
//    }
//}
//
