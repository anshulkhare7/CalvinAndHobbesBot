package com.anshul.bot.calvinbot.core;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Component
public class CalvinBot extends TelegramLongPollingBot{

	private static final String BOT_TOKEN = "<BOT_TOKEN>"; 
	private static final String BOT_NAME = "calvinandhobbes_bot";
	private static final String CALVIN_HOBBES_SEARCH_URL = "http://michaelyingling.com/random/calvin_and_hobbes/search.php?phrase=";
	private static final Logger log = LoggerFactory.getLogger(CalvinBot.class);
	
	@Override
	public void onUpdateReceived(Update update) {
	
		Long chatId;
		SendMessage message = new SendMessage() ;
		StringBuilder responseText = new StringBuilder("");
		XmlMapper xmlMapper = new XmlMapper();
		
		if(update.hasMessage() && update.getMessage().hasText()) {
			String receivedText = update.getMessage().getText().trim().toLowerCase();
			chatId = update.getMessage().getChatId();			
			message.setParseMode(ParseMode.HTML).setChatId(chatId);
			
			if(receivedText.equals("/start")) {
				responseText.append("Welcome to <strong>Calvin & Hobbes Bot</strong>!").append("\n").append("\n");
				responseText.append("This bot is for Calvin & Hobbes fans. Type any <b>search word</b> and I'll find a relevant Calvin & Hobbes comics for you.").append("\n").append("\n");				
				responseText.append("Currently, the search abilities are limited. I can only search single keywords. If you give me multiple search words together, I'll only search for the first word and ignore the rest.").append("\n").append("\n");
				responseText.append("If I find more than one comic for your search word, I'll randomly select one for you. If you want to see more for the same search word, please send me the same word again.").append("\n").append("\n");
				responseText.append("Very soon I'll be upgraded and you'll be allowed to search multiple words.").append("\n").append("\n");
				responseText.append("Please drop me a mail at <b>exponenthash[at]gmail[dot]com</b> to share your feedback.");
			}else {
				String searchWord = receivedText.split(" ")[0];
				log.info("Search Word Received: "+searchWord);
				
				try {
				
					RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<String> searchResponse = restTemplate.getForEntity(CALVIN_HOBBES_SEARCH_URL + searchWord, String.class);
					if(searchResponse.getStatusCode().equals(HttpStatus.OK) && 
							searchResponse.getBody().indexOf("Your search returned no results.")==-1) {
						String responseBody = searchResponse.getBody();
						String fieldsString = responseBody.substring(responseBody.indexOf("<fieldset>")-10, responseBody.lastIndexOf("</fieldset>"));
						if(!StringUtils.isEmpty(fieldsString)) {
							String fieldsets[] = fieldsString.split("</fieldset>");
							log.info("No. of Fieldsets received: "+fieldsets.length);
							Random rand = new Random(System.currentTimeMillis());
							int randIdx = rand.nextInt(fieldsets.length);
							String randomResult = fieldsets[randIdx];
							String randomComicLink = randomResult.substring(randomResult.indexOf("<a class='img_link' href='")+26, randomResult.indexOf("'><img src='comic.png'"));
							String description = randomResult.substring(randomResult.indexOf("<div class='description'>")+25, randomResult.indexOf("<span class='strip'")-7);
							log.info(randomComicLink);							
							responseText.append("<b>Description: </b>").append(description).append("\n").append("\n");
							responseText.append(randomComicLink);
						}						 
					}				
				    	
			    	}catch(Exception e) {		    		
			    		log.info("Exception in calling the Calvin Search Engine API."+e.getMessage());
			    	}
			}						
		}
		
		if(responseText.length() < 1) {
			responseText.append("I am sorry. I could not find anything for your search word.").append("\n").append("\n"); 
			responseText.append("It would be great if you could drop me a mail at exponenthash[at]gmail[dot]com explaining what were you trying to search. It will help me improve the bot.");
		}
		
		message.setText(responseText.toString());
		
		try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
	}
	
	@Override
	public String getBotUsername() {
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}
}

