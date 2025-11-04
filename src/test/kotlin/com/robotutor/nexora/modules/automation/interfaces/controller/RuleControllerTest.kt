package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.RuleUseCase
import com.robotutor.nexora.modules.automation.application.command.CreateRuleCommand
import com.robotutor.nexora.modules.automation.domain.entity.Rule
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.domain.entity.RuleType
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.domain.entity.objects.ComparisonOperator
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.RuleRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.RuleResponse
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.testUtils.assertNextWith
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
import java.time.Instant

class RuleControllerTest {
    private val mockUseCase = mockk<RuleUseCase>()
    private val controller = RuleController(mockUseCase)

    private val actorData = ActorData(
        actorId = ActorId("actor-1"),
        role = Role(RoleId("role-1"), PremisesId("prem-1"), Name("Role"), RoleType.USER),
        premisesId = PremisesId("prem-1"),
        principalType = ActorPrincipalType.USER,
        principal = UserData(
            UserId("user-1"),
            Name("John"),
            Email("john@example.com"),
            Instant.parse("2020-01-01T00:00:00Z")
        )
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should create rule`() {
        val request = RuleRequest(
            name = "Morning Rule",
            type = RuleType.TRIGGER,
            description = "desc",
            config = com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.FeedControlConfigRequest(
                feedId = "feed-1", operator = ComparisonOperator.GREATER_THAN, value = 10
            )
        )
        val domain = Rule.create(
            RuleId("rule-1"),
            CreateRuleCommand(
                Name("Morning Rule"),
                "desc",
                RuleType.TRIGGER,
                FeedControlConfig(FeedId("feed-1"), ComparisonOperator.GREATER_THAN, 10)
            ),
            actorData
        )
        every { mockUseCase.createRule(any(), any()) } returns Mono.just(domain)

        val mono = controller.createTrigger(request, actorData)

        assertNextWith(mono) {
            it.ruleId shouldBe "rule-1"
            it.premisesId shouldBe "prem-1"
            it.name shouldBe "Morning Rule"
            it.type shouldBe RuleType.TRIGGER
            it.description shouldBe "desc"
            // config is mapped; verify type only to avoid deep mapping coupling
            (it.config as com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.FeedControlConfigResponse).apply {
                feedId shouldBe "feed-1"
                operator shouldBe ComparisonOperator.GREATER_THAN
                value shouldBe 10
            }
        }
        verify(exactly = 1) { mockUseCase.createRule(any(), actorData) }
    }

    @Test
    fun `should list rules`() {
        val r1 = Rule.create(
            RuleId("r1"),
            CreateRuleCommand(
                Name("Rule-A"),
                null,
                RuleType.TRIGGER,
                FeedControlConfig(FeedId("f1"), ComparisonOperator.EQUAL, 1)
            ),
            actorData
        )
        val r2 = Rule.create(
            RuleId("r2"),
            CreateRuleCommand(
                Name("Rule-B"),
                null,
                RuleType.CONDITION,
                FeedControlConfig(FeedId("f2"), ComparisonOperator.EQUAL, 2)
            ),
            actorData
        )
        every { mockUseCase.getRules(any(), any()) } returns Flux.just(r1, r2)

        val resources = ResourcesData(
            listOf(
                ResourceEntitlement(
                    ResourceContext(ResourceType.AUTOMATION_RULE, "r1", ActionType.READ),
                    PremisesId("prem-1")
                ),
                ResourceEntitlement(
                    ResourceContext(ResourceType.AUTOMATION_RULE, "r2", ActionType.READ),
                    PremisesId("prem-1")
                ),
            )
        )

        val list = controller.getRules(actorData, resources).collectList().block()!!
        list.size shouldBe 2
//        list[0] is RuleResponse shouldBe true
        verify(exactly = 1) {
            mockUseCase.getRules(
                match { it.map { id -> id.value } == listOf("r1", "r2") },
                actorData
            )
        }
    }

    @Test
    fun `should get rule by id`() {
        val r1 = Rule.create(
            RuleId("r1"),
            CreateRuleCommand(
                Name("Rule-A"),
                null,
                RuleType.TRIGGER,
                FeedControlConfig(FeedId("f1"), ComparisonOperator.EQUAL, 1)
            ),
            actorData
        )
        every { mockUseCase.getRule(RuleId("r1"), actorData) } returns Mono.just(r1)

        val mono = controller.getRule("r1", actorData)
        assertNextWith(mono) {
            it.ruleId shouldBe "r1"
        }
        verify(exactly = 1) { mockUseCase.getRule(RuleId("r1"), actorData) }
    }
}

