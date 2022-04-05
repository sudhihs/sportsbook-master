package uk.co.williamhill.exercise.sportsbook.service.scoreboard.notification;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import uk.co.williamhill.exercise.sportsbook.domain.ScoreNotification;
import uk.co.williamhill.exercise.sportsbook.service.scoreboard.ScoreboardServiceImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class ScoreNotificationServiceImplementation implements ScoreNotificationService {

    Logger logger = LoggerFactory.getLogger(ScoreboardServiceImplementation.class);

    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Override
    public void notify(ScoreNotification scoreNotification) {

        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().data(scoreNotification));
                logger.debug("notification sent for score : {})", scoreNotification);
            } catch (Exception e) {
                deadEmitters.add(emitter);
                log.error("error sending notification {}", emitter, e);
            }
        });
        emitters.removeAll(deadEmitters);

    }

    @Override
    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);

    }

    @Override
    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);

    }

}
