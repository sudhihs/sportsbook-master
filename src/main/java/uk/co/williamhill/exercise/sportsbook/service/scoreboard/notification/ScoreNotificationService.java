package uk.co.williamhill.exercise.sportsbook.service.scoreboard.notification;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import uk.co.williamhill.exercise.sportsbook.domain.ScoreNotification;

public interface ScoreNotificationService {

	public void notify(ScoreNotification scoreNotification);

	public void addEmitter(final SseEmitter emitter);

	public void removeEmitter(final SseEmitter emitter);

}
