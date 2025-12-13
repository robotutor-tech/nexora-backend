//
//package com.robotutor.nexora.modules.premises.domain.entity
//
//import com.robotutor.nexora.shared.domain.vo.Name
//import com.robotutor.nexora.shared.domain.vo.PremisesId
//import com.robotutor.nexora.shared.domain.model.UserId
//import io.kotest.matchers.shouldBe
//import org.junit.jupiter.api.Test
//
//class PremisesTest {
//    @Test
//    fun `register should initialize fields`() {
//        val premisesId = PremisesId("prem-0001")
//        val name = Name("Home")
//        val address = Address("street", "city", "state", "country", "12345")
//        val owner = UserId("user-1")
//
//        val premises = Premises.register(premisesId, name, address, owner)
//
//        premises.premisesId shouldBe premisesId
//        premises.name shouldBe name
//        premises.address shouldBe address
//        premises.owner shouldBe owner
//    }
//}
//
