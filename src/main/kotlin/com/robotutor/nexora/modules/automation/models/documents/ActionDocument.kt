//package com.robotutor.nexora.modules.automation.models.documents
//
//import com.robotutor.nexora.modules.automation.controllers.views.ActionRequest
//import com.robotutor.nexora.modules.automation.models.ActionConfig
//import com.robotutor.nexora.modules.automation.models.ActionId
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
//const val ACTION_COLLECTION = "actions"
//
//@TypeAlias("Action")
//@CompoundIndexes(
//    CompoundIndex(
//        name = "unique_action_by_premisesId_config",
//        def = "{'premisesId': 1, 'config': 1}",
//        unique = true
//    )
//)
//@Document(ACTION_COLLECTION)
//data class ActionDocument(
//    var id: ObjectId? = null,
//    @Indexed(unique = true)
//    val actionId: ActionId,
//    @Indexed
//    val premisesId: PremisesId,
//    val name: String,
//    val description: String? = null,
//    @Indexed
//    val config: Map<String, Any?>,
//    val createdOn: Instant = Instant.now(),
//    val updatedOn: Instant = Instant.now(),
//    @Version
//    val version: Long? = null
//) {
//    companion object {
//        fun from(
//            actionId: ActionId,
//            config: ActionConfig,
//            request: ActionRequest,
//            premisesActorData: PremisesActorData
//        ): ActionDocument {
//            return ActionDocument(
//                actionId = actionId,
//                premisesId = premisesActorData.premisesId,
//                name = request.name,
//                description = request.description,
//                config = config.toMap(),
//            )
//        }
//    }
//}
