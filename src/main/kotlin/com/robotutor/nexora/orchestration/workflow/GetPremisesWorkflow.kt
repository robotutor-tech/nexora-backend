package com.robotutor.nexora.orchestration.workflow

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.orchestration.client.IAMClient
import com.robotutor.nexora.orchestration.client.PremisesClient
import com.robotutor.nexora.orchestration.client.view.PremisesResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class GetPremisesWorkflow(
    private val iamClient: IAMClient,
    private val premisesClient: PremisesClient,
) {
    fun getPremises(accountData: AccountData): Flux<PremisesResponse> {
        return iamClient.getActors(accountData)
            .collectList()
            .flatMapMany { actorResponse ->
                premisesClient.getPremises(actorResponse.map { it.premisesId })
            }
    }
}
