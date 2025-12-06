//package com.robotutor.nexora.modules.user.infrastructure.persistence.repository
//
//import com.robotutor.nexora.modules.user.builder.UserBuilder
//import com.robotutor.nexora.testUtils.assertNextWith
//import io.kotest.matchers.shouldBe
//import io.mockk.*
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.springframework.data.mongodb.core.ReactiveMongoTemplate
//import org.springframework.data.mongodb.core.query.Criteria
//import org.springframework.data.mongodb.core.query.Query
//import reactor.core.publisher.Mono
//
//class MongoUserRepositoryTest {
//    private lateinit var mongoTemplate: ReactiveMongoTemplate
//    private lateinit var repository: MongoUserRepository
//
//    @BeforeEach
//    fun setup() {
//        clearAllMocks()
//        mongoTemplate = mockk(relaxed = true)
//        repository = spyk(MongoUserRepository(mongoTemplate))
//    }
//
//    @AfterEach
//    fun tearDown() {
//        clearAllMocks()
//    }
//
//    @Test
//    fun `should save user and return user`() {
//        val user = UserBuilder().build()
//        val query = Query(Criteria.where("userId").`is`(user.userId.value))
//        every { repository.findAndReplace(query, user) } returns Mono.just(user)
//
//        val result = repository.save(user)
//        assertNextWith(result) {
//            it shouldBe user
//            verify(exactly = 1) { repository.findAndReplace(query, user) }
//        }
//    }
//
//    @Test
//    fun `should delete user by userId and return user`() {
//        val user = UserBuilder().build()
//        val query = Query(Criteria.where("userId").`is`(user.userId.value))
//        every { repository.deleteOne(query) } returns Mono.just(user)
//
//        val result = repository.deleteByUserId(user.userId)
//        assertNextWith(result) {
//            it shouldBe user
//            verify(exactly = 1) { repository.deleteOne(query) }
//        }
//    }
//
//    @Test
//    fun `should find user by userId and return user`() {
//        val user = UserBuilder().build()
//        val query = Query(Criteria.where("userId").`is`(user.userId.value))
//        every { repository.findOne(query) } returns Mono.just(user)
//
//        val result = repository.findByUserId(user.userId)
//        assertNextWith(result) {
//            it shouldBe user
//            verify(exactly = 1) { repository.findOne(query) }
//        }
//    }
//
//    @Test
//    fun `should find user by email and return user`() {
//        val user = UserBuilder().build()
//        val query = Query(Criteria.where("email").`is`(user.email.value))
//        every { repository.findOne(query) } returns Mono.just(user)
//
//        val result = repository.findByEmail(user.email)
//        assertNextWith(result) {
//            it shouldBe user
//            verify(exactly = 1) { repository.findOne(query) }
//        }
//    }
//}
