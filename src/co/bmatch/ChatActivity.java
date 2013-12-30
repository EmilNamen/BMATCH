package co.bmatch;


import java.util.List;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends Activity{

	private String chat_user_id;

	private String user_id;
	private String user_name;
	private String user_job;
	
	private String chat_id;

	private EditText chatField;
	private Context activityContext;
	private TextView chatTextName;
	private TextView chatTextJob;
	private co.bmatch.ChatAdapter adapter;
	private ListView lv;


	private static ChatActivity instancia;

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
			Log.d("TAG_CHAT_INFO", "Error: " + e.getMessage());
			e.printStackTrace();
		}
		chatField = (EditText) findViewById(R.id.editTextChatfield);


		adapter = new ChatAdapter(activityContext, R.layout.chat_main);
		lv = (ListView) findViewById(R.id.chatList);
		lv.setAdapter(adapter);


		Button buttonSend = (Button) findViewById(R.id.buttonSendChat);
		buttonSend.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.buttonSendChat)
				{
					adapter.add(new ChatField(false, chatField.getText().toString()));
					if(chatField.getText().toString() != null && chatField.getText().toString() != "")
					{
						//send message
						newChatClass();
						ParseObject message = new ParseObject("MensajeChat");
						message.put("Chat", ParseObject.createWithoutData("Chat", chat_id));
						message.put("From", ParseObject.createWithoutData("ChatUser", chat_user_id));
						message.put("To", ParseObject.createWithoutData("ChatUser", user_id));
						message.put("Mensaje", chatField.getText().toString());
						message.put("Download", false);
						try {
							message.save();
						} catch (ParseException e) {
							e.printStackTrace();
						}
						chatField.setText(" ");
					}
				}				
			}
		});
	}

	public static ChatActivity darInstancia ( )	{
		return instancia;
	}

	public void setChatUserInfo()
	{
		chatTextName.clearComposingText();
		chatTextJob.clearComposingText();

		Intent intent = getIntent();
		user_id = intent.getStringExtra(PeopleListActivity.CHAT_USER_ID);
		user_name = intent.getStringExtra(PeopleListActivity.CHAT_USER_NAME);
		user_job = intent.getStringExtra(PeopleListActivity.CHAT_USER_JOB);
		
		chatTextName.setText(user_name);
		chatTextJob.setText(user_job);

		chat_user_id = intent.getStringExtra(PeopleListActivity.CHAT_USER_ID_MASTER);
		
	}
	
	public void newChatClass(){
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

	public void newChat(){

		ParseObject chat = new ParseObject("Chat");
		if(user_id.compareTo(chat_user_id) <= 0  )
		{
			chat.put("Usuario1", ParseObject.createWithoutData("ChatUser", user_id));
			chat.put("Usuario2", ParseObject.createWithoutData("ChatUser",  chat_user_id));
		}
		else
		{
			chat.put("Usuario2", ParseObject.createWithoutData("ChatUser", user_id));
			chat.put("Usuario1", ParseObject.createWithoutData("ChatUser",  chat_user_id));
		}
		try {
			chat.save();
			chat_id = chat.getObjectId();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
