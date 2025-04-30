package com.robotutor.nexora.feed.repositories

import com.robotutor.nexora.feed.models.Feed
import com.robotutor.nexora.feed.models.FeedId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedRepository : ReactiveCrudRepository<Feed, FeedId>{

}
