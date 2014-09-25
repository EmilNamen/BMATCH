package co.bmatch;


import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
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

	//The id of the event which the chat belongs to
	private String active_event;

	//The chat user id which the logged user is talking to
	private String chatUserId;

	//The logged user id
	private String loggedUserId;

	//The Chat parse manager object
	private ChatParseManager chatParseManager;

	//The Chat object
	private Chat chat;

	//The unique Chat id that represents the active chat
	private String chatId;

	private EditText chatField;

	private TextView chatTextName;
	private TextView chatTextJobCompany;

	private co.bmatch.ChatAdapter adapter;

	private ListView lv;

	private Button buttonSend;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		chatTextName = (TextView) findViewById(R.id.chatTextName);
		chatTextJobCompany = (TextView) findViewById(R.id.chatTextJob);
		chatField = (EditText) findViewById(R.id.editTextChatfield);
		adapter = new ChatAdapter(getApplicationContext(), R.layout.chat_main);
		lv = (ListView) findViewById(R.id.chatList);
		lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		buttonSend = (Button) findViewById(R.id.buttonSendChat);

		//Calls the data from intent
		Bundle b = getIntent().getExtras();

		this.active_event = b.getString("activeEventId");
		this.chatUserId = b.getString("chatUserId");
		if(this.chatUserId == null && this.active_event ==  null){

			SharedPreferences prefs = getSharedPreferences("your_file_name", MODE_PRIVATE); 
			this.chatUserId = prefs.getString("chatUserId", " ");
			this.active_event = prefs.getString("activeEventId",	" ");
			System.out.println("ME LLEGAN LOS DATOS: " +this.chatUserId +"/^"+ this.active_event );
		}

		this.loggedUserId = b.getString("loggedUserId");
		
		if(this.loggedUserId ==  null){
			loggedUserId = ParseUser.getCurrentUser().getObjectId().toString();
		}

		//Creates a new Chat object

		chat = new Chat(this.active_event, this.chatUserId);
		chatId = chat.getChatId();
		System.out.println("Encuentro el chat con id:"+chatId);


		// Creates the chat parse manager

		chatParseManager =  new ChatParseManager(active_event, chatId, chatUserId, loggedUserId);

		//Set the chat display values
		setChatUserInfo();

		//TODO Pensar bien cuando debo descargar los mensajes del servidor
		new DownloadChat( ).execute( );

	}

	protected void onResume( ){
		super.onResume();

		buttonSend.setOnClickListener( new View.OnClickListener( ) {

			@Override
			public void onClick(View v) {				

				if(!chatField.getText( ).toString( ).isEmpty( ))
				{
					//String nowAsString = new SimpleDateFormat("h:mm").format(System.currentTimeMillis());
					chatParseManager.sendMessage(chatField.getText( ).toString());
					adapter.add( new ChatField(false, chatField.getText().toString() ) );	
					//TODO Solo envio push cuando el otro usuario no este dentro del chat
					chatParseManager.sendPush(chatField.getText().toString());
					chatField.setText(" ");
				}

			}
		});
		lv.setAdapter(adapter);

	}


	/**
	 * Method that set the display name and displayjobcompany of the chat
	 */
	public void setChatUserInfo()
	{
		chatTextName.clearComposingText();
		chatTextJobCompany.clearComposingText();

		try {			
			chatTextName.setText( chatParseManager.getDisplayName() );
			chatTextJobCompany.setText( chatParseManager.getJobCompany() );

		} catch (Exception e) {
			Log.d("Post retrieval", "Error: " + e.getMessage());
			e.printStackTrace();
			chatTextName.setText( "ERROR" );
			chatTextJobCompany.setText( "ERROR" );
		}

	}


	/**
	 * Clase que descarga los mensajes del usuario
	 */

	private class DownloadChat extends AsyncTask<Void, Void, String> {

		ParseQuery<ParseObject> queryMessage = ParseQuery.getQuery("ChatMessage");
		List<ParseObject> chatList = new ArrayList<ParseObject>();
		ProgressDialog dialog = new ProgressDialog(ChatActivity.this);

		@Override
		protected String doInBackground(Void... params) {

			try {
				queryMessage.whereEqualTo("chat", ParseObject.createWithoutData( "Chat", chatId ));
				queryMessage.orderByAscending("createdAt");
				chatList = queryMessage.find();

			} catch (ParseException e) {
				e.printStackTrace();
			}
			return "Downloading chat...";
		}

		@Override
		protected void onPreExecute() { 
			dialog.setMessage("Descargando mensajes...");
			dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {			
			for( ParseObject chatActual: chatList ){				
				ParseObject po = chatActual.getParseObject("to");
				String idChat = po.getObjectId();
				//String nowAsString = ""+chatActual.getCreatedAt().getHours()+": "+chatActual.getCreatedAt().getMinutes();
				adapter.add( new ChatField( idChat.equals( loggedUserId ), chatActual.get("message").toString()) );
			}

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

		}
		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}


	@Override
	protected void onPause( ) {
		super.onPause();
	}


	@Override
	protected void onStop( ) {
		super.onStop();

	}

}
