package com.robotutor.nexora.modules.automation.application

import com.robotutor.nexora.modules.automation.application.command.CreateRuleCommand
import com.robotutor.nexora.modules.automation.application.validation.ConfigValidation
import com.robotutor.nexora.modules.automation.domain.entity.Rule
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.domain.entity.RuleType
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.domain.entity.objects.ComparisonOperator
import com.robotutor.nexora.modules.automation.domain.exception.NexoraError
import com.robotutor.nexora.modules.automation.domain.repository.RuleRepository
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import com.robotutor.nexora.testUtils.assertErrorWith
import com.robotutor.nexora.testUtils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

class RuleUseCaseTest {
    private val ruleRepository = mockk<RuleRepository>()
    private val idGeneratorService = mockk<IdGeneratorService>()
    private val resourceCreatedEventPublisherDeprecated = mockk<EventPublisherDeprecated<ResourceCreatedEvent>>()
    private val configValidation = mockk<ConfigValidation>()

    private val ruleUseCase = RuleUseCase(ruleRepository, idGeneratorService, resourceCreatedEventPublisherDeprecated, configValidation)

    private val actorData = ActorData(
        actorId = ActorId("actor-1"),
        role = Role(RoleId("role-1"), PremisesId("prem-1"), Name("Role"), RoleType.USER),
        premisesId = PremisesId("prem-1"),
        principalType = ActorPrincipalType.USER,
        principal = UserData(UserId("user-1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        val fixedInstant = Instant.parse("2023-01-01T00:00:00Z")
        mockkStatic(Instant::class)
        every { Instant.now() } returns fixedInstant
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should create rule successfully`() {
        val config = FeedControlConfig(FeedId("feed-1"), ComparisonOperator.GREATER_THAN, 10)
        val command = CreateRuleCommand(Name("Morning Rule"), "desc", RuleType.TRIGGER, config)
        val ruleId = RuleId("rule-000000000001")

        every { configValidation.validate(config, actorData) } returns Mono.just(config)
        every { ruleRepository.findByTypeAndPremisesIdAndConfig(command.type, actorData.premisesId, config) } returns Mono.empty()
        every { idGeneratorService.generateId(any(), any<Class<RuleId>>()) } returns Mono.just(ruleId)
        every { ruleRepository.save(any()) } answers { Mono.just(firstArg()) }
        every { resourceCreatedEventPublisherDeprecated.publish(any()) } returns Mono.just(Unit)

        val mono = ruleUseCase.createRule(command, actorData)

        assertNextWith(mono) {
            it.ruleId shouldBe ruleId
            it.premisesId shouldBe actorData.premisesId
            it.name shouldBe command.name
            it.description shouldBe command.description
            it.type shouldBe command.type
            it.config shouldBe command.config
            it.createdOn shouldBe Instant.parse("2023-01-01T00:00:00Z")
            it.updatedOn shouldBe Instant.parse("2023-01-01T00:00:00Z")
        }

        verify(exactly = 1) {
            configValidation.validate(config, actorData)
            ruleRepository.findByTypeAndPremisesIdAndConfig(command.type, actorData.premisesId, config)
            idGeneratorService.generateId(com.robotutor.nexora.modules.automation.domain.entity.IdType.RULE_ID, RuleId::class.java)
            ruleRepository.save(any())
            resourceCreatedEventPublisherDeprecated.publish(any())
        }
    }

    @Test
    fun `should error when duplicate rule exists`() {
        val config = FeedControlConfig(FeedId("feed-1"), ComparisonOperator.GREATER_THAN, 10)
        val existing = Rule.create(RuleId("rule-1"), CreateRuleCommand(Name("dupl"), null, RuleType.TRIGGER, config), actorData)
        val command = CreateRuleCommand(Name("Morning Rule"), "desc", RuleType.TRIGGER, config)

        every { configValidation.validate(config, actorData) } returns Mono.just(config)
        every { ruleRepository.findByTypeAndPremisesIdAndConfig(command.type, actorData.premisesId, config) } returns Mono.just(existing)

        val mono = ruleUseCase.createRule(command, actorData)

        assertErrorWith(mono) {
            // DuplicateDataException wraps ErrorResponse with NEXORA0302 and message extended
            (it as com.robotutor.nexora.shared.domain.exception.DuplicateDataException).errorResponse() shouldBe ErrorResponse(
                NexoraError.NEXORA0302.errorCode,
                NexoraError.NEXORA0302.message + " with ruleId: ${existing.ruleId.value}"
            )
        }

        verify(exactly = 1) {
            configValidation.validate(config, actorData)
            ruleRepository.findByTypeAndPremisesIdAndConfig(command.type, actorData.premisesId, config)
        }
        verify(exactly = 0) {
            idGeneratorService.generateId(any(), any<Class<RuleId>>())
            ruleRepository.save(any())
            resourceCreatedEventPublisherDeprecated.publish(any())
        }
    }

    @Test
    fun `should get rules by ids`() {
        val ids = listOf(RuleId("r1"), RuleId("r2"))
        val r1 = Rule.create(RuleId("r1"), CreateRuleCommand(Name("ruleA"), null, RuleType.TRIGGER, FeedControlConfig(FeedId("f1"), ComparisonOperator.EQUAL, 1)), actorData)
        val r2 = Rule.create(RuleId("r2"), CreateRuleCommand(Name("ruleB"), null, RuleType.CONDITION, FeedControlConfig(FeedId("f2"), ComparisonOperator.EQUAL, 2)), actorData)
        every { ruleRepository.findAllByPremisesIdAndRuleIdIn(actorData.premisesId, ids) } returns Flux.just(r1, r2)

        val flux = ruleUseCase.getRules(ids, actorData)
        StepVerifier.create(flux).expectNext(r1).expectNext(r2).verifyComplete()

        verify(exactly = 1) { ruleRepository.findAllByPremisesIdAndRuleIdIn(actorData.premisesId, ids) }
    }

    @Test
    fun `should get rule by id and error if not found`() {
        val id = RuleId("r1")
        every { ruleRepository.findByRuleIdAndPremisesId(id, actorData.premisesId) } returns Mono.empty()

        val mono = ruleUseCase.getRule(id, actorData)

        assertErrorWith(mono) {
            (it as DataNotFoundException).errorResponse() shouldBe ErrorResponse(
                com.robotutor.nexora.modules.automation.domain.exception.NexoraError.NEXORA0316.errorCode,
                com.robotutor.nexora.modules.automation.domain.exception.NexoraError.NEXORA0316.message,
            )
        }
    }
}

