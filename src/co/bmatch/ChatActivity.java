package co.bmatch;


import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ChatActivity. Clase que representa al chat entre dos usuarios
 * @author emilcamilonamenleon
 *
 */
public class ChatActivity extends Activity{

	/*
	 * Id del Usuario Master
	 */
	private String chat_user_id;

	/*
	 * Id del Usuario seleccionado para chat
	 */
	private String user_id;

	/*
	 * Nombre del Usuario seleccionado para chat
	 */
	private String user_name;

	/*
	 * Profesion del Usuario seleccionado para chat
	 */
	private String user_job;


	private String chat_id;

	private EditText chatField;
	private Context activityContext;
	private TextView chatTextName;
	private TextView chatTextJob;
	private co.bmatch.ChatAdapter adapter;
	private ListView lv;

	private static ChatActivity instancia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		activityContext = getApplicationContext();
		instancia = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		chatTextJob = (TextView) findViewById(R.id.chatTextJob);
		chatTextName = (TextView) findViewById(R.id.chatTextName);

		try{
			setChatUserInfo();
		}
		catch(Exception e)
		{
			Log.d("Chat User Info", "Error: " + e.getMessage());
			e.printStackTrace();
		}
		chatField = (EditText) findViewById(R.id.editTextChatfield);


		adapter = new ChatAdapter(activityContext, R.layout.chat_main);
		lv = (ListView) findViewById(R.id.chatList);

		newChatClass();

		Button buttonSend = (Button) findViewById(R.id.buttonSendChat);
		buttonSend.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.buttonSendChat)
				{
					if(chatField.getText().toString().isEmpty() != true)
					{
						//send message
						try{
							ParseObject message = new ParseObject("MensajeChat");
							message.put("Chat", ParseObject.createWithoutData("Chat", chat_id));
							message.put("From", ParseObject.createWithoutData("ChatUser", chat_user_id));
							message.put("To", ParseObject.createWithoutData("ChatUser", user_id));
							message.put("Mensaje", chatField.getText().toString());
							message.put("Download", false);
							message.save();

							if(chatField.getText().toString().isEmpty() != true)
								adapter.add( new ChatField(false, chatField.getText().toString() ) );
							sendPush(chatField.getText().toString());
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						chatField.setText(" ");
					}
				}				
			}
		});

		lv.setAdapter(adapter);
		downloadChat();
	}


	public void sendPush(String msj)
	{
		ParsePush push = new ParsePush();
		String author = LogInActivity.darInstancia().getUserFirstName() +" "+ LogInActivity.darInstancia().getUserLastName();		
		try {
			JSONObject data = new JSONObject( );
			data.put( "alert", msj );
			data.put( "title", author );

			push.setChannel(chat_user_id);
			push.setData(data);
			push.sendInBackground();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Metodo que agrega el nombre y la profesion a la ventana del chat
	 */
	public void setChatUserInfo()
	{
		chatTextName.clearComposingText();
		chatTextJob.clearComposingText();		
		user_id = PeopleListActivity.darInstancia().getActiveId();
		user_name = PeopleListActivity.darInstancia().getActiveName();
		user_job = PeopleListActivity.darInstancia().getactiveJob();
		chatTextName.setText( user_name );
		chatTextJob.setText( user_job );
		chat_user_id = PeopleListActivity.darInstancia().getUserActiveId();

	}

	public void newChatClass( ){

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");

		if(user_id.compareTo(chat_user_id) <= 0  )
		{
			query.whereEqualTo("Usuario1", ParseObject.createWithoutData("ChatUser", user_id));
			query.whereEqualTo("Usuario2", ParseObject.createWithoutData("ChatUser", chat_user_id));
		}
		else
		{
			query.whereEqualTo("Usuario2", ParseObject.createWithoutData("ChatUser", user_id));
			query.whereEqualTo("Usuario1", ParseObject.createWithoutData("ChatUser", chat_user_id));
		}
		try {
			List<ParseObject> postList = query.find();
			if(postList.isEmpty())
			{
				newChat();
			}
			else
			{
				ParseObject obj = postList.get(0);
				chat_id = obj.getObjectId();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void newChat( ){

		ParseObject chat = new ParseObject("Chat");
		if(user_id.compareTo(chat_user_id) <= 0  )
		{
			chat.put("Usuario1", ParseObject.createWithoutData( "ChatUser", user_id));
			chat.put("Usuario2", ParseObject.createWithoutData( "ChatUser",  chat_user_id));
		}
		else
		{
			chat.put("Usuario2", ParseObject.createWithoutData( "ChatUser", user_id));
			chat.put("Usuario1", ParseObject.createWithoutData( "ChatUser",  chat_user_id));
		}
		try {
			chat.save();
			chat_id = chat.getObjectId();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	public void downloadChat( ){

		try {
			ParseQuery<ParseObject> queryMessage = ParseQuery.getQuery("MensajeChat");
			queryMessage.whereEqualTo("Chat", ParseObject.createWithoutData( "Chat", chat_id ));
			queryMessage.orderByAscending("createdAt");
			List<ParseObject> chatList = queryMessage.find();
			for( ParseObject chat: chatList ){				
				ParseObject po = chat.getParseObject("To");
				String idChat = po.getObjectId();
				System.out.println(idChat);
				System.out.println(chat_user_id);
				adapter.add( new ChatField( idChat.equals( chat_user_id.toString() ), chat.get("Mensaje").toString() ) );
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public static ChatActivity darInstancia()
	{
		return instancia;
	}
	
	@Override
	protected void onPause() {
        super.onPause();
    }

}
