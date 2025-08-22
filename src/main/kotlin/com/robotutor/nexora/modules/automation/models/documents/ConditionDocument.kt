//package com.robotutor.nexora.modules.automation.models.documents
//
//import com.robotutor.nexora.modules.automation.controllers.views.ConditionRequest
//import com.robotutor.nexora.modules.automation.models.ConditionConfig
//import com.robotutor.nexora.modules.automation.models.ConditionId
//import com.robotutor.nexora.common.security.models.PremisesId
//import com.robotutor.nexora.common.security.models.PremisesActorData
//import com.robotutor.nexora.utils.toMap
//import org.bson.types.ObjectId
//import org.springframework.data.annotation.TypeAlias
//import org.springframework.data.annotation.Version
//import org.springframework.data.mongodb.core.index.CompoundIndex
//import org.springframework.data.mongodb.core.index.CompoundIndexes
//import org.springframework.data.mongodb.core.index.Indexed
//import org.springframework.data.mongodb.core.mapping.Document
//import java.time.Instant
//
//const val CONDITION_COLLECTION = "conditions"
//
//@TypeAlias("Condition")
//@CompoundIndexes(
//    CompoundIndex(
//        name = "unique_condition_by_premisesId_type_config",
//        def = "{'premisesId': 1, 'type': 1, 'config': 1}",
//        unique = true
//    )
//)
//@Document(CONDITION_COLLECTION)
//data class ConditionDocument(
//    var id: ObjectId? = null,
//    @Indexed(unique = true)
//    val conditionId: ConditionId,
//    @Indexed
//    val premisesId: PremisesId,
//    val name: String,
//    val description: String? = null,
//    val config: Map<String, Any?>,
//    val createdOn: Instant = Instant.now(),
//    val updatedOn: Instant = Instant.now(),
//    @Version
//    val version: Long? = null
//) {
//    companion object {
//        fun from(
//            conditionId: ConditionId,
//            config: ConditionConfig,
//            request: ConditionRequest,
//            premisesActorData: PremisesActorData
//        ): ConditionDocument {
//            return ConditionDocument(
//                conditionId = conditionId,
//                premisesId = premisesActorData.premisesId,
//                name = request.name,
//                description = request.description,
//                config = config.toMap(),
//            )
//        }
//    }
//}