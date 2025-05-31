package com.robotutor.nexora.iam.models

import com.robotutor.nexora.iam.controllers.view.PolicyRequest
import com.robotutor.nexora.iam.models.Resource.FEED
import com.robotutor.nexora.iam.models.Resource.WIDGET
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val POLICY_COLLECTION = "policies"

@TypeAlias("Policy")
@Document(POLICY_COLLECTION)
data class Policy(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val policyId: PolicyId,
    @Indexed
    val premisesId: PremisesId,
    val permission: Permission,
    val identifier: Identifier<Resource>? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(policyId: String, request: PolicyRequest, premisesActorData: PremisesActorData): Policy {
            return Policy(
                policyId = policyId,
                premisesId = premisesActorData.premisesId,
                permission = request.permission,
                identifier = request.identifier,
            )
        }
    }
}

enum class Scope {
    GLOBAL,
    RESOURCE,
}

enum class Permission(val resource: Resource? = null, val scope: Scope = Scope.RESOURCE) {
    FEED_CREATE(scope = Scope.GLOBAL), FEED_READ(FEED), FEED_UPDATE(FEED), FEED_DELETE(FEED),
    WIDGET_CREATE(scope = Scope.GLOBAL), WIDGET_READ(WIDGET), WIDGET_UPDATE(WIDGET), WIDGET_DELETE(WIDGET),
}

enum class Resource {
    FEED,
    WIDGET
}


typealias PolicyId = String