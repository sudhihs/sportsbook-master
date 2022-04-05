package uk.co.williamhill.exercise.sportsbook.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import uk.co.williamhill.exercise.sportsbook.domain.Event;
import uk.co.williamhill.exercise.sportsbook.domain.LatestScore;
import uk.co.williamhill.exercise.sportsbook.service.scoreboard.ScoreboardService;

@WebMvcTest(SportsBookController.class)
public class SportBookControllerTest extends BaseControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
    ScoreboardService scoreboardService;

	@Test
	public void registerScore() throws Exception {
		Long teamAId = 1L;
		Long teamBId = 1L;
		LocalDateTime eventDateTime = LocalDateTime.parse("2022-01-25T20:00");
		Long eventId = 1L;
		Event event = Event.builder().id(eventId).eventDateTime(eventDateTime).name("TEST").teamAId(teamAId)
				.teamBId(teamBId).build();
		mvc.perform(MockMvcRequestBuilders.post("/api/scoreboards/" + eventId + "/scores/").content(asJsonString(event))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	public void getLatestScore() throws Exception {

		Long eventId = 1L;
		LocalDateTime latestScoreDateTime = LocalDateTime.parse("2022-01-25T20:00");
		LatestScore latestScore = LatestScore.builder().teamNameA("Liverpool").teamNameB("Luton").latestScoreDateTime(
				latestScoreDateTime)
				.teamAScore(1L).teamBScore(2L).build();
		mvc.perform(MockMvcRequestBuilders.get("/api/scoreboards/" + eventId + "/scores/latest")
				.content(asJsonString(latestScore)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
	}

}
