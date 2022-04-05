package uk.co.williamhill.exercise.sportsbook.persistence;

import org.springframework.data.repository.CrudRepository;

import uk.co.williamhill.exercise.sportsbook.entities.TeamEntity;

public interface TeamRepository extends CrudRepository<TeamEntity, Long> {

}
