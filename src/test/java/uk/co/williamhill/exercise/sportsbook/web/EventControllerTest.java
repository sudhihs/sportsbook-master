package uk.co.williamhill.exercise.sportsbook.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import uk.co.williamhill.exercise.sportsbook.domain.Event;
import uk.co.williamhill.exercise.sportsbook.service.event.EventService;

@WebMvcTest(EventsController.class)
public class EventControllerTest extends BaseControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
    EventService eventService;

	@Test
	public void createEvent() throws Exception {

		Long teamAId = 1L;
		Long teamBId = 2L;
		String eventName = "Liverpool v Luton";
		LocalDateTime eventDateTime = LocalDateTime.parse("2022-01-25T20:00");

		Event event = Event.builder().eventDateTime(eventDateTime).name("TEST").teamAId(teamAId).teamBId(teamBId)
				.build();

		Event savedEvent = Event.builder().eventDateTime(eventDateTime).teamAId(teamAId).teamBId(teamBId)
				.name(eventName).id(1L).build();

		when(eventService.createEvent(event)).thenReturn(savedEvent);

		mvc.perform(MockMvcRequestBuilders.post("/api/events/").content(asJsonString(event))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

}
