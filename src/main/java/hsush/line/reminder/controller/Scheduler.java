package hsush.line.reminder.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;

import hsush.line.reminder.model.Remind;
import hsush.line.reminder.service.LineService;

/**
 * An scheduler that checks all reminds periodically
 * 
 * @author hsush
 * @since 2018-12-06
 */
@Component
@EnableScheduling
public class Scheduler {
	private static final Logger logger = LoggerFactory.getLogger(LineController.class.getName());

	@Autowired
	private LineService service;

	@Autowired
	private LineMessagingClient lineMessagingClient;

	@Scheduled(cron = "* * * ? * *")
	public void work() throws ParseException {
		logger.info("work()");

		List<Remind> reminds = this.service.getNeedExecRemind();
		for (Remind r : reminds) {
			this.lineMessagingClient.pushMessage(new PushMessage(r.getTo(), new TextMessage(r.getMsg())));

			if (r.getEvery() == null) {
				r.setFinish(true);
			} else {
				r.setExecAt(this.getNextExecTime(r.getExecAt(), r.getEvery()));
			}
			
			this.service.update(r);
		}
	}

	/**
	 * generate next execution timestamp based on [every] parameter
	 * @param timeStr
	 * @param every
	 * @return
	 * @throws ParseException
	 */
	private String getNextExecTime(String timeStr, String every) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calender = Calendar.getInstance();
		calender.setTime(sdf.parse(timeStr));
		
		if("minute".equals(every)) {
			calender.add(Calendar.MINUTE, 1);
			return sdf.format(calender.getTime());
		} else if("hour".equals(every)) {
			calender.add(Calendar.HOUR, 1);
			return sdf.format(calender.getTime());
		} else if("day".equals(every) || "week".equals(every)) {
			calender.add(Calendar.DATE, "day".equals(every) ? 1 : 7);
			return sdf.format(calender.getTime());
		} else if("month".equals(every)) {
			calender.add(Calendar.MONTH, 1);
			return sdf.format(calender.getTime());
		} else if("year".equals(every)) {
			calender.add(Calendar.YEAR, 1);
			return sdf.format(calender.getTime());
		} else if("sun".equals(every)) {
			LocalDateTime ldt = calender.toInstant().atZone(ZoneId.of("Asia/Taipei")).toLocalDateTime().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
			return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} else if("mon".equals(every)) {
			LocalDateTime ldt = calender.toInstant().atZone(ZoneId.of("Asia/Taipei")).toLocalDateTime().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
			return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} else if("tue".equals(every)) {
			LocalDateTime ldt = calender.toInstant().atZone(ZoneId.of("Asia/Taipei")).toLocalDateTime().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
			return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} else if("wed".equals(every)) {
			LocalDateTime ldt = calender.toInstant().atZone(ZoneId.of("Asia/Taipei")).toLocalDateTime().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
			return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} else if("thu".equals(every)) {
			LocalDateTime ldt = calender.toInstant().atZone(ZoneId.of("Asia/Taipei")).toLocalDateTime().with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
			return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} else if("fri".equals(every)) {
			LocalDateTime ldt = calender.toInstant().atZone(ZoneId.of("Asia/Taipei")).toLocalDateTime().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
			return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} else if("sat".equals(every)) {
			LocalDateTime ldt = calender.toInstant().atZone(ZoneId.of("Asia/Taipei")).toLocalDateTime().with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
			return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} 
		
		return sdf.format(calender.getTime());
	}
}
