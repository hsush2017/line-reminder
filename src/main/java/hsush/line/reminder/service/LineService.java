package hsush.line.reminder.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linecorp.bot.model.event.source.Source;

import hsush.line.reminder.controller.LineController;
import hsush.line.reminder.model.Remind;

/**
 * Service
 * 
 * @author hsush
 * @since 2018-12-10
 */
@Service
public class LineService {
	private static final Logger logger = LoggerFactory.getLogger(LineController.class.getName());

	@Autowired
	private JdbcTemplate dao;

	/**
	 * add a remind task to db
	 * 
	 * @param r
	 * @return insert count
	 */
	@Transactional
	public int addRemind(Remind r) {
		logger.info("insert...");
		
		StringBuilder sb = new StringBuilder();
		sb.append("insert into remind_task(creator, cmd, \"to\", msg, exec_at, every, create_at, finish, enable) ");
		sb.append("values (?, ?, ?, ?, ?::timestamp, ?, CURRENT_TIMESTAMP AT TIME ZONE 'CCT', false, true)");
		
		return this.dao.update(sb.toString(), r.getCreator(), r.getCmd(), r.getTo(), r.getMsg(), r.getExecAt(), r.getEvery());
	}

	/**
	 * get reminds which is going to be executed
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Remind> getNeedExecRemind() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT seq, creator, cmd, who, \"to\", msg, TO_CHAR(exec_at, 'YYYY-MM-DD HH24:MI:SS') AS exec_at");
		sb.append("	,every, create_at, update_at, finish, enable ");
		sb.append("FROM remind_task ");
		sb.append("WHERE exec_at <= current_timestamp AT TIME ZONE 'CCT' AND finish = false AND enable = true");
		
		return this.dao.query(sb.toString(), new BeanPropertyRowMapper(Remind.class));
	}

	/**
	 * update database
	 * 
	 * @param r
	 * @return
	 */
	@Transactional
	public int update(Remind r) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE remind_task SET ");
		sb.append("	cmd = ?, who = ?, \"to\" = ?, msg = ?, exec_at = ?::timestamp, every = ?, ");
		sb.append("	update_at = CURRENT_TIMESTAMP AT TIME ZONE 'CCT', finish = ?, enable = ? ");
		sb.append("WHERE creator = ? AND create_at = ?::timestamp");
		
		return this.dao.update(sb.toString(), r.getCmd(), r.getWho(), r.getTo(), r.getMsg(), r.getExecAt(), r.getEvery(),
				r.isFinish(), r.isEnable(), r.getCreator(), r.getCreateAt());
	}

	/**
	 * list unfinished reminders
	 * @param source
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getReminders(Source source) {
		String creator = source.getSenderId() == null ? source.getUserId() : source.getSenderId();
		
		// query data
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT seq, CASE WHEN length(msg) <= 10 THEN msg ELSE substring(msg, 0, 8)||'...' END AS msg");
		sb.append("	,every, TO_CHAR(exec_at, 'YYYY-MM-DD HH24:MI:SS') AS exec_at ");
		sb.append("FROM remind_task ");
		sb.append("WHERE creator = ? AND enable = true AND finish = false ");
		sb.append("ORDER BY seq ");
		List<Remind> result = this.dao.query(sb.toString(), new Object[]{creator}, new BeanPropertyRowMapper(Remind.class));

		// generate text message
		sb = new StringBuilder(String.format("%-4s%-27s%-15s%-10s\n", "id", "next_exec_time", "every", "message"));
		for (Remind remind : result) {
			sb.append(String.format("%-4s%-25s%-15s%-10s\n", remind.getSeq(), remind.getExecAt(), remind.getEvery(), remind.getMsg()));
		}
		
		return sb.toString();
	}

	/**
	 * delete remind
	 * 
	 * @param seq
	 * @return
	 */
	@Transactional
	public int delRemind(String seq) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE remind_task SET finish = true, enable = false WHERE seq = ?::integer");
		
		return this.dao.update(sb.toString(), seq);
	}
}
