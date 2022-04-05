package uk.co.williamhill.exercise.sportsbook.service.event;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.williamhill.exercise.sportsbook.domain.Event;
import uk.co.williamhill.exercise.sportsbook.entities.EventEntity;
import uk.co.williamhill.exercise.sportsbook.entities.TeamEntity;
import uk.co.williamhill.exercise.sportsbook.persistence.EventRepository;
import uk.co.williamhill.exercise.sportsbook.persistence.TeamRepository;

@Service
public class EventServiceImplementation implements EventService {

	Logger logger = LoggerFactory.getLogger(EventServiceImplementation.class);

	@Autowired
	private EventRepository eventRepository;

	@Autowired
    TeamRepository teamRepository;

	@Override
	public Event createEvent(Event newEvent) {

		validateEvent(newEvent);

		TeamEntity teamA = findTeam(newEvent.getTeamAId());
		TeamEntity teamB = findTeam(newEvent.getTeamBId());

		EventEntity eventEntity = EventEntity.builder().eventDateTime(newEvent.getEventDateTime())
				.name(newEvent.getName()).teamA(teamA).teamB(teamB).build();

		EventEntity savedEvent = eventRepository.save(eventEntity);
		newEvent.setId(savedEvent.getId());

		logger.debug("new event created:{}", newEvent);

		return newEvent;
	}

	private TeamEntity findTeam(Long teamId) {
		TeamEntity entity = null;
		Optional<TeamEntity> teamEntity = teamRepository.findById(teamId);
		if (teamEntity.isPresent()) {
			entity = teamEntity.get();
		} else {
			throw new TeamNotFoundException("team with id:" + teamId + " not found");
		}
		return entity;

	}

	private void validateEvent(Event event) {
		validateTeamAPresent(event);
		validateTEamBPresent(event);
		validateEventDateTime(event);
		validateEventName(event);
	}

	private void validateTeamAPresent(Event event){
		if (null == event.getTeamAId()) {
			throw new EventValidationException(
					"Payload must contain valid team for TeamA - please check your request payload");
		}
	}

	private void validateTEamBPresent(Event event){
		if (null == event.getTeamBId()) {
			throw new EventValidationException(
					"Payload must contain valid team for TeamB - please check your request payload");
		}
	}

	private void validateEventDateTime(Event event)
	{
		if (null == event.getEventDateTime()) {
			throw new EventValidationException(
					"Payload must contain valid date for Event Date - please check your request payload");
		}
	}

	private void validateEventName(Event event){
		if (null == event.getName() || event.getName().isEmpty()) {
			throw new EventValidationException(
					"Payload must contain valid name for the event - please check your request payload");
		}
	}

}
