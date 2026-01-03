}
    }
        }
            e.errorCode shouldBe UserError.NEXORA0205.errorCode
            val e = ex as DataNotFoundException
        assertErrorWith(service.execute(query)) { ex ->

        val query = GetUserQuery(PrincipalId("acc-404"))

        every { userRepository.findByUserId(UserId("acc-404")) } returns Mono.empty()
    fun `should return DataNotFoundException when repository empty`() {
    @Test

    }
        assertNextWith(service.execute(query)) { it.userId shouldBe userId }

        val query = GetUserQuery(PrincipalId("acc-1"))

        every { userRepository.findByUserId(UserId("acc-1")) } returns Mono.just(user)

        )
            updatedAt = fixedInstant
            registeredAt = fixedInstant,
            state = UserState.REGISTERED,
            mobile = Mobile("9012345678"),
            email = Email("john@example.com"),
            name = Name("John"),
            userId = userId,
        val user = UserAggregate.create(
        val fixedInstant = Instant.parse("2023-01-01T00:00:00Z")
        val userId = UserId("user-1")
    fun `should return user when repository hits`() {
    @Test

    private val service = GetUserService(userRepository)
    private val userRepository: UserRepository = mockk()

class GetUserServiceTest {

import kotlin.test.Test
import java.time.Instant
import reactor.core.publisher.Mono
import io.mockk.mockk
import io.mockk.every
import io.kotest.matchers.shouldBe
import com.robotutor.nexora.testUtils.assertNextWith
import com.robotutor.nexora.testUtils.assertErrorWith
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.module.user.domain.vo.Mobile
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.module.user.domain.repository.UserRepository
import com.robotutor.nexora.module.user.domain.exception.UserError
import com.robotutor.nexora.module.user.domain.aggregate.UserState
import com.robotutor.nexora.module.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.module.user.application.command.GetUserQuery



