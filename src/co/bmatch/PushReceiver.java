package co.bmatch;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class PushReceiver extends BroadcastReceiver {


	private static final String TAG = "PushReceiver";

	private String chat_id;
	
	private String activeEventId;
	
	private String chatUserId;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			System.out.println("ENTRAAAAAA");			
			
			JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
			Iterator itr = json.keys();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				if(key.equals("event_id")){
					 activeEventId = json.getString("event_id");
				}
				if(key.equals("chatUser")){
					chatUserId = json.getString("chatUser");
				}
				if(key.equals("chat")){
					chat_id = json.getString("chat");
				}	        

				Log.d(TAG, "..." + key + " => " + json.getString(key));
			}
			
			
			SharedPreferences prefs = context.getSharedPreferences("your_file_name", context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("activeEventId", activeEventId);
			editor.putString("chatUserId", chatUserId);
			editor.commit(); // This line is IMPORTANT. If you miss this one its not gonna work!
			
			
			
			
			//context.startActivity(chatIntent);
			activateChat(true);
			
			
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}

	private void activateChat(boolean active_chat_boolean) 
	{
		String logged_user_id = ParseUser.getCurrentUser().getObjectId();

		//First check chat id is not null
		if (chat_id != null) {
			//Find the chat with the push id we get
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");
			query.whereEqualTo("objectId", chat_id);
			try {
				List<ParseObject> activeChats = query.find();
				if (!activeChats.isEmpty()) {

					for (ParseObject chat : activeChats) {
						String idUser1 = chat.getParseObject("user1")
								.getObjectId();

						//Set active chat
						if (idUser1.equals(logged_user_id)) {
							chat.put("active1", active_chat_boolean);
							chat.save();
						}
						if (!idUser1.equals(logged_user_id)) {
							chat.put("active2", active_chat_boolean);
							chat.save();
						}
					}
				}

			} catch (ParseException e1) {
				e1.printStackTrace();
				Log.d("Setting active a chat", "Error: " + e1.getMessage());
			}
		}
	}
	
	public String getChatUserId(){
		return  chatUserId;
	}
	
	public String getActiveEventId(){
		return  activeEventId;
	}
	



}