package uk.co.williamhill.exercise.sportsbook.persistence;

import org.springframework.data.repository.CrudRepository;

import uk.co.williamhill.exercise.sportsbook.entities.ScoreEntity;

public interface ScoreRepository extends CrudRepository<ScoreEntity, Long> {

}
