package hsush.line.reminder.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import hsush.line.reminder.model.Remind;
import hsush.line.reminder.service.LineService;
import hsush.line.reminder.validator.Validator;

/**
 * Line event controller
 * 
 * @author hsush
 * @since 2018-11-28
 */
@LineMessageHandler
public class LineController {
	private static final Logger logger = LoggerFactory.getLogger(LineController.class.getName());

	@Autowired
	private LineService service;
	
	@EventMapping
	public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
		String text = event.getMessage().getText().trim().toLowerCase();
		
		try {
			if(text.equals("/reminders")) {
				return new TextMessage(this.service.getReminders(event.getSource()));
			} else if(text.startsWith("/remind")) {
				if(!Validator.isValidRemindCommand(text)) {
					return new TextMessage("Arguments error!");
				}
				
				this.service.addRemind(new Remind(event.getSource(), text));
				return new TextMessage("OK!");
			} else if(text.startsWith("/rm")) {
				if(!Validator.isValidRemoveCommand(text)) {
					return new TextMessage("Arguments error!");
				}
			
				this.service.delRemind(text.replace("/rm", "").trim());
				return new TextMessage("OK!");
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		} 
		
		return null;
	}

	@EventMapping
	public List<Message> handlePostbackEvent(PostbackEvent event) {
		try {
		} catch (Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	

	@EventMapping
    public void handleOtherEvent(Event event) {
    }
}
