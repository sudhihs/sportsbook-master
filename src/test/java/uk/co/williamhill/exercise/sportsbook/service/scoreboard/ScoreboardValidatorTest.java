package uk.co.williamhill.exercise.sportsbook.service.scoreboard;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.williamhill.exercise.sportsbook.domain.Score;
import uk.co.williamhill.exercise.sportsbook.entities.EventEntity;
import uk.co.williamhill.exercise.sportsbook.entities.TeamEntity;

@ExtendWith(MockitoExtension.class)
public class ScoreboardValidatorTest {

	@Test
	public void testInvalidEventId() throws Exception {

		ScoreboardValidator scoreboardValidator = new ScoreboardValidator();

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			Score score = Score.builder().build();
			scoreboardValidator.validateMandatoryParameters(null, score);

		});
	}

	@Test
	public void testInvalidScore() throws Exception {

		ScoreboardValidator scoreboardValidator = new ScoreboardValidator();

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			scoreboardValidator.validateMandatoryParameters(Long.valueOf(1), null);

		});
	}

	@Test
	public void testValidateScoreWithMissingScoreValue() throws Exception {
		ScoreboardValidator scoreboardValidator = new ScoreboardValidator();

		Assertions.assertThrows(ScoreboardValidationException.class, () -> {
			EventEntity eventEntity = EventEntity.builder().build();
			Score score = Score.builder().build();
			scoreboardValidator.validateScore(eventEntity, score);

		});
	}

	@Test
	public void validateInvalidTeamForScore() throws Exception {
		ScoreboardValidator scoreboardValidator = new ScoreboardValidator();

		Assertions.assertThrows(ScoreboardValidationException.class, () -> {

			TeamEntity teamEntity = TeamEntity.builder().id(1L).build();
			LocalDateTime eventTime = LocalDateTime.parse("2022-01-25T15:00:00");
			EventEntity eventEntity = EventEntity.builder().eventDateTime(eventTime).teamB(teamEntity).build();

			LocalDateTime scoreTime = LocalDateTime.parse("2022-01-25T15:00:00");
			Score score = Score.builder().timeRecorded(scoreTime).score(5L).teamId(2L)
					.build();
			scoreboardValidator.validateScore(eventEntity, score);

		});
	}

	@Test
	public void validateMissingForTimeScore() throws Exception {
		ScoreboardValidator scoreboardValidator = new ScoreboardValidator();

		Assertions.assertThrows(ScoreboardValidationException.class, () -> {

			TeamEntity teamEntity = TeamEntity.builder().id(1L).build();
			EventEntity eventEntity = EventEntity.builder().teamB(teamEntity).build();

			Score score = Score.builder().score(1L).build();
			scoreboardValidator.validateScore(eventEntity, score);

		});
	}

	@Test
	public void validateScoreTimeBeforeEventTime() throws Exception {
		ScoreboardValidator scoreboardValidator = new ScoreboardValidator();

		Assertions.assertThrows(ScoreboardValidationException.class, () -> {

			LocalDateTime eventDateTime = LocalDateTime.parse("2022-01-25T20:00:00");
			Long teamId = 1L;
			TeamEntity teamEntity = TeamEntity.builder().id(teamId).build();
			EventEntity eventEntity = EventEntity.builder().teamB(teamEntity).eventDateTime(eventDateTime).build();

			LocalDateTime scoreTime = LocalDateTime.parse("2022-01-25T15:00:00");
			Score score = Score.builder().score(1L).teamId(teamId).timeRecorded(scoreTime).build();
			scoreboardValidator.validateScore(eventEntity, score);

		});
	}

}
