package hsush.line.reminder.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.linecorp.bot.model.event.source.Source;

/**
 * Remind json bean
 * 
 * @author hsush
 * @since 2018-12-03
 */
public class Remind {
	private int seq;
	private String cmd;
	private String who;
	private String to;
	private String msg;
	private String execAt;
	private String every;
	private String creator;
	private String createAt;
	private String updateAt;
	private boolean finish;
	private boolean enable;
	
	public Remind() {
		super();
	}

	public Remind(Source source, String text) {
		List<String> list = Arrays.asList(text.split(" "));
		Date now = Calendar.getInstance().getTime();
		
		this.cmd = text;
		this.who = null;
		this.to = source.getSenderId() == null ? source.getUserId() : source.getSenderId();
		this.msg = list.get(1).trim().replace("\"", "");
		
		String at = list.indexOf("at") > 0 ? list.get(list.indexOf("at") + 1) : "00:00";
		String on = list.indexOf("on") > 0 ? list.get(list.indexOf("on") + 1) : new SimpleDateFormat("yyyy-MM-dd").format(now);
		this.execAt = on + " " + at;
		
		this.every = list.indexOf("every") > 0 ? list.get(list.indexOf("every") + 1) : null;
		this.creator = source.getSenderId() == null ? source.getUserId() : source.getSenderId();
		this.createAt = null;
		this.updateAt = null;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getExecAt() {
		return execAt;
	}

	public void setExecAt(String execAt) {
		this.execAt = execAt;
	}

	public String getEvery() {
		return every;
	}

	public void setEvery(String every) {
		this.every = every;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public String getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(String updateAt) {
		this.updateAt = updateAt;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
