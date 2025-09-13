package com.robotutor.nexora


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class AppContext(
    @Autowired applicationContext: ApplicationContext
) {

    init {
        Companion.applicationContext = applicationContext
    }

    companion object {
        lateinit var applicationContext: ApplicationContext
    }
}

