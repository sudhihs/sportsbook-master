package uk.co.williamhill.exercise.sportsbook.service.scoreboard;

import uk.co.williamhill.exercise.sportsbook.domain.LatestScore;
import uk.co.williamhill.exercise.sportsbook.domain.Score;

public interface ScoreboardService {

	public Score registerScore(Long eventId, Score score);

	public LatestScore findLatestScore(Long eventId);

}
