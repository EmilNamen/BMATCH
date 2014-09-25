package co.bmatch;


import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ChatParseManager {

	//The unique id of the chat
	private String chatId;

	//The id of the event which the chat belong to
	private String eventId;

	//The id of the user which i am talking to
	private String chatUserId;

	//The logged user id
	private String loggedUserId;

	//Display name
	private String chatUserdisplayName;

	//Display job and company
	private String chatUserJobCompany;



	public ChatParseManager(String activeEventId, String chatId, String chatUserId, String loggedUserId){

		this.eventId = activeEventId;
		this.chatId = chatId;
		this.chatUserId = chatUserId;
		this.loggedUserId = loggedUserId;
		
		setChatUserInfo();
	}


	/**
	 * Method that sends a new message
	 * @param msj message to be sent
	 */
	public void sendMessage(String msj){		
		ParseObject message = new ParseObject("ChatMessage");		
		message.put("message", msj);
		message.put("chat", ParseObject.createWithoutData("Chat", chatId));
		message.put("from", ParseObject.createWithoutData("_User", loggedUserId));
		message.put("to", ParseObject.createWithoutData("_User", chatUserId));
		try {
			message.save();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	

	public void sendPush(String msj)
	{
		ParsePush push = new ParsePush();
		String author = ParseUser.getCurrentUser().getString("name") +" "+ ParseUser.getCurrentUser().getString("lastName") ;
		try {
			JSONObject data = new JSONObject( );
			data.put("action", "co.bmatch.ChatActivity");
			data.put( "alert", author+": "+msj );
			data.put( "chat", chatId);
			data.put("chatUser", chatUserId);
			data.put("msg", msj);
			data.put("event_id", eventId);

			//TODO Cambiar al id del usuario user_id
			push.setChannel(chatUserId);
			push.setData(data);
			push.sendInBackground();
			System.out.println("PUSH OK");
		} catch (JSONException e) {
			e.printStackTrace( );
		}
	}



	/**
	 * Method that set the display name and displayjobcompany of the chat
	 */
	public void setChatUserInfo()
	{		
		ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
		query.whereEqualTo("objectId", chatUserId);
		try {
			ParseUser post = query.getFirst();
			chatUserdisplayName = post.getString("name")+" "+post.getString("lastName");
			chatUserJobCompany = post.getString("career")+"   "+post.getString("company");

		} catch (ParseException e) {
			Log.d("Post retrieval", "Error: " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Method that returns the chatUserdisplayName of user which i am talking to
	 */
	public String getDisplayName( ){
		return chatUserdisplayName;
	}

	/**
	 * Method that returns the chatUserJobCompany of user which i am talking to
	 */
	public String getJobCompany( ){
		return chatUserJobCompany;
	}





}
