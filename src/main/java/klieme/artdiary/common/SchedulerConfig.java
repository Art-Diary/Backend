package klieme.artdiary.common;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import klieme.artdiary.fcm.SendAlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {
	private final SendAlarmService sendAlarmService;

	// @Scheduled(cron = "0 0/1 * * * ?")
	@Scheduled(cron = "0 0 9 * * ?")
	public void run() throws IOException {
		sendAlarmService.sendMessageAboutExh();
	}
}
