package hsush.line.reminder.validator;

public class Validator {
//	private static final String WHO_REGEX = "[\\s\\S]+";
	private static final String MSG_REGEX = "\"[\\s\\S]+\"";
	private static final String TIME_REGEX = "( at ([0-1]?[0-9]|2[0-3]):[0-5][0-9])?";
	private static final String DATE_REGEX = "( on (2000|2400|2800|(19|2[0-9](0[48]|[2468][048]|[13579][26])))-02-29"
			+ "| on ((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8])"
			+ "| on ((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01])"
			+ "| on ((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))?";
	private static final String everyRegex = "( every (minute|hour|day|week|month|year|sun|mon|tue|wed|thu|fri|sat))?";

	/**
	 * check if text is a valid remind command
	 * @param text
	 * @return
	 */
	public static boolean isValidRemindCommand(String text) {
//		String regex = "/remind " + WHO_REGEX + " " + MSG_REGEX + TIME_REGEX + DATE_REGEX + everyRegex;
		String regex = "/remind " + MSG_REGEX + TIME_REGEX + DATE_REGEX + everyRegex;
		return text.matches(regex) && (text.indexOf("at") > 0 || text.indexOf("on") > 0 || text.indexOf("every") > 0);
	}

	public static boolean isValidRemoveCommand(String text) {
		return text.matches("/rm \\d+");
	}
}
