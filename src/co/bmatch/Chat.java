package co.bmatch;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Chat {


	//The unique id of the chat
	private String chatId;

	//The id of the event which the chat belong to
	private String eventId;

	//The id of the user which i am talking to
	private String chatUserId;

	//The logged user id
	private String loggedUserId;

	//List with the messages 
	private ArrayList<String> messagesList;

	/**
	 * Constructor of a new Chat object
	 * @param eventId
	 * @param chatUserId
	 */
	public Chat(String eventId, String chatUserId){

		this.eventId = eventId;

		this.chatUserId = chatUserId;

		messagesList = new ArrayList<String>();

		loggedUserId = ParseUser.getCurrentUser().getObjectId().toString();

		newChatClass( );
		
	}


	/**
	 * Method that verifies if is needed to create a new Chat relation in Parse
	 */
	public void newChatClass( ){

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");

		if(chatUserId.compareTo(loggedUserId) < 0  )
		{
			query.whereEqualTo("user1", ParseObject.createWithoutData("_User", chatUserId));
			query.whereEqualTo("user2", ParseObject.createWithoutData("_User", loggedUserId));
		}
		else
		{
			query.whereEqualTo("user2", ParseObject.createWithoutData("_User", chatUserId));
			query.whereEqualTo("user1", ParseObject.createWithoutData("_User", loggedUserId));
		}
		try {

			List<ParseObject> postList = query.find();
			if(postList.isEmpty())
			{
				newChat( );
			}
			else
			{
				ParseObject obj = postList.get(0);
				chatId = obj.getObjectId();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that create a new Chat relation in Parse
	 */
	public void newChat( ){

		ParseObject chat = new ParseObject("Chat");
		if(chatUserId.compareTo(loggedUserId) <= 0  )
		{
			chat.put("user1", ParseObject.createWithoutData( "_User", chatUserId));
			chat.put("user2", ParseObject.createWithoutData( "_User",  loggedUserId));
			chat.put("event", ParseObject.createWithoutData( "Event", eventId));
			chat.put("active1",false);
			chat.put("active2",false);
		}
		else
		{
			chat.put("user2", ParseObject.createWithoutData( "_User", chatUserId));
			chat.put("user1", ParseObject.createWithoutData( "_User",  loggedUserId));
			chat.put("event", ParseObject.createWithoutData( "Event", eventId));
			chat.put("active1",false);
			chat.put("active2",false);
		}
		try {
			chat.save();
			chatId = chat.getObjectId().toString();	
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Method that returns the chat UNIQUE ID
	 */
	public String getChatId( ){
		return chatId;
	}

	/**
	 * Method that returns the user which i am talking to
	 */
	public String getChatUserId( ){
		return chatUserId;
	}




}
