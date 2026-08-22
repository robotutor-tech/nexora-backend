package com.robotutor.nexora.module.seed

import com.robotutor.nexora.module.seed.document.SeedHistory
import com.robotutor.nexora.module.seed.repository.SeedHistoryRepository
import com.robotutor.nexora.module.seed.seed.SeedData
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class SeedRunner(
    private val seeds: List<SeedData>,
    private val historyRepository: SeedHistoryRepository
) : ApplicationRunner {

    private val logger = Logger(SeedRunner::class.java)

    override fun run(args: ApplicationArguments) {
        println("=========================SEED STARTED============================")

        Flux.fromArray(seeds.toTypedArray())
            .flatMap { seedData ->
                historyRepository.findByName(seedData.name)
                    .logOnSuccess(logger, "Seed data already present for ${seedData.name}")
                    .switchIfEmpty {
                        seedData.execute()
                            .flatMap {
                                historyRepository.save(SeedHistory(name = seedData.name))
                            }
                            .logOnSuccess(logger, "Successfully inserted data for ${seedData.name}")
                    }
                    .logOnError(logger, "Error while inserting data for ${seedData.name}")
            }
            .blockLast()

        println("=========================SEED STOPPED============================")
    }
}
