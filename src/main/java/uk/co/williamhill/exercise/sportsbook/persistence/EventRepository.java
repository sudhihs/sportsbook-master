package uk.co.williamhill.exercise.sportsbook.persistence;

import org.springframework.data.repository.CrudRepository;

import uk.co.williamhill.exercise.sportsbook.entities.EventEntity;

public interface EventRepository extends CrudRepository<EventEntity, Long> {

}
