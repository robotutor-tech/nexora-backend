package com.robotutor.nexora.context.iam.application.view

import com.robotutor.nexora.context.iam.domain.aggregate.TokenAggregate

data class Tokens(val authorizationToken: TokenAggregate, val refreshToken: TokenAggregate)
