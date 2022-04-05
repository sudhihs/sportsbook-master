package uk.co.williamhill.exercise.sportsbook.service.scoreboard;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.williamhill.exercise.sportsbook.domain.LatestScore;
import uk.co.williamhill.exercise.sportsbook.domain.Score;
import uk.co.williamhill.exercise.sportsbook.domain.ScoreNotification;
import uk.co.williamhill.exercise.sportsbook.entities.EventEntity;
import uk.co.williamhill.exercise.sportsbook.entities.ScoreEntity;
import uk.co.williamhill.exercise.sportsbook.entities.TeamEntity;
import uk.co.williamhill.exercise.sportsbook.persistence.EventRepository;
import uk.co.williamhill.exercise.sportsbook.persistence.ScoreRepository;
import uk.co.williamhill.exercise.sportsbook.persistence.TeamRepository;
import uk.co.williamhill.exercise.sportsbook.service.scoreboard.notification.ScoreNotificationService;

@Service
@Transactional
public class ScoreboardServiceImplementation implements ScoreboardService {

	Logger logger = LoggerFactory.getLogger(ScoreboardServiceImplementation.class);

	@Autowired
	private ScoreRepository scoreRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private ScoreNotificationService scoreNotificationService;

	@Autowired
	private ScoreboardValidator validator;

	@Override
	public Score registerScore(Long eventId, Score score) {

		validator.validateMandatoryParameters(eventId, score);

		Score savedScore = null;
		Optional<EventEntity> eventEntity = eventRepository.findById(eventId);
		if (eventEntity.isPresent()) {
			EventEntity event = eventEntity.get();

			validator.validateScore(event, score);

			Optional<TeamEntity> teamEntity = teamRepository.findById(score.getTeamId());
			TeamEntity team = teamEntity.get();

			ScoreEntity scoreEntity = getScoreEntity(score, team);
			scoreEntity.setEvent(event);

			scoreEntity = scoreRepository.save(scoreEntity);

			savedScore = getScore(score, scoreEntity);

			ScoreNotification scoreNotification = ScoreNotification.builder().score(score.getScore())
					.sendTime(score.getTimeRecorded()).teamId(team.getId()).teamName(team.getName()).build();
			scoreNotificationService.notify(scoreNotification);

			logger.debug("score created: {}", savedScore);

		} else {
			throw new EventNotFoundException("Cannot find event with an id of:" + eventId);
		}
		return savedScore;
	}

	@Override
	public LatestScore findLatestScore(Long eventId) {

		LatestScore latestScore = null;

		Optional<EventEntity> eventEntity = eventRepository.findById(eventId);
		if (eventEntity.isPresent()) {
			latestScore = processEvent(eventEntity);
		} else {
			throw new EventNotFoundException("event with eventId:" + eventId + " does not exist");
		}

		return latestScore;
	}

	public LocalDateTime latest(LocalDateTime a, LocalDateTime b) {
		return a == null ? b : (b == null ? a : (a.isAfter(b) ? a : b));
	}

	private LatestScore processEvent(Optional<EventEntity> eventEntity) {
		EventEntity theEvent = eventEntity.get();

		LatestScore latestScore = LatestScore.builder().teamNameA(theEvent.getTeamA().getName())
				.teamAScore(0L).teamBScore(0L).teamNameB(theEvent.getTeamB().getName())
				.build();

		if (null != theEvent.getScores() && theEvent.getScores().size() > 0) {
			latestScore = computeLatestScore(theEvent);
		}

		return latestScore;
	}

	private LatestScore computeLatestScore(EventEntity theEvent) {
		LatestScore latestScore = null;

		ScoreEntity teamALatestScore = theEvent.getScores().stream()
				.filter(t -> t.getTeam().getId().equals(theEvent.getTeamA().getId())).sorted(getComparator())
				.reduce((a, b) -> b).orElse(null);

		ScoreEntity teamBLatestScore = theEvent.getScores().stream()
				.filter(t -> t.getTeam().getId().equals(theEvent.getTeamB().getId())).sorted(getComparator())
				.reduce((a, b) -> b).orElse(null);

		latestScore = LatestScore.builder().teamNameA(theEvent.getTeamA().getName()).teamAScore(0L)
				.teamNameB(theEvent.getTeamB().getName()).teamBScore(0L).build();

		latestScore.setTeamAScore(getLatestScore(teamALatestScore));
		latestScore.setTeamBScore(getLatestScore(teamBLatestScore));
		latestScore.setLatestScoreDateTime(findLatestScoreDateTime(getLatestScoreDateTime(teamALatestScore),
				getLatestScoreDateTime(teamBLatestScore)));

		logger.debug("latestScore:{}", latestScore.toString());

		return latestScore;
	}

	private LocalDateTime getLatestScoreDateTime(ScoreEntity scoreEntity) {
		if (null != scoreEntity) {
			return scoreEntity.getTimeRecorded();
		} else {
			return null;
		}
	}

	private Long getLatestScore(ScoreEntity scoreEntity) {
		if (null != scoreEntity) {
			return scoreEntity.getScore();
		} else {
			return 0L;
		}
	}

	private Comparator<ScoreEntity> getComparator() {
		return Comparator.comparing(ScoreEntity::getTimeRecorded);
	}

	private LocalDateTime findLatestScoreDateTime(LocalDateTime teamATime, LocalDateTime teamBTime) {

		return latest(teamATime, teamBTime);
	}

	private Score getScore(Score score, ScoreEntity scoreEntity) {
		return Score.builder().id(scoreEntity.getId()).score(score.getScore())
				.timeRecorded(score.getTimeRecorded()).teamId(score.getTeamId()).build();
	}

	private ScoreEntity getScoreEntity(Score score, TeamEntity team) {
		return ScoreEntity.builder().team(team).score(score.getScore())
				.timeRecorded(score.getTimeRecorded()).build();
	}

}
