package co.bmatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ActivePeopleListActivity extends Activity {


	private ListView listActivePeople;
	private ArrayList<HashMap<String, String>> activeContactList;	
	private ListAdapter activeAdapter;	

	public static final String TAG_NAME_ACTIVE = "Name";
	public static final String TAG_JOB_ACTIVE = "Job";
	public static final String TAG_ID_ACTIVE = "Id";


	private static ActivePeopleListActivity instancia;

	//Info when an item is selected
	private String activeName;
	private String activeJob;
	private String activeId;
	private HashMap<String,String> map2;


	// Info to add to the hashmap
	private String activeUserId;
	private String activeChatId;
	private String active_event;


	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		instancia = this;
		setContentView(R.layout.active_peoplelist_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Lista de contactos activos	
		activeContactList = new ArrayList<HashMap<String,String>>();
		listActivePeople = (ListView) findViewById(R.id.listViewActivePeople);
		activeAdapter = new SimpleAdapter(getApplicationContext(), activeContactList, R.layout.contact_main,
				new String[] {TAG_NAME_ACTIVE, TAG_JOB_ACTIVE},  new int[] {R.id.textName, R.id.textJob});
		activeId = "";

		try {
			//Obtengo el id del usuario logeado y id del evento activo
			activeUserId = ParseUser.getCurrentUser( ).getObjectId( );
		} catch (Exception e1) {
			e1.printStackTrace();
		}


		//Getting the event_id for the two multiple ways
		try {
			active_event = FirstActivity.darInstancia().getEventCode();
			if(active_event.isEmpty( ) || active_event == null){
				try {
					active_event = EventsHistoryActivity.darInstancia( ).getIdSelectedEventId();
				} catch (Exception e) {
					e.printStackTrace();
					Intent intent1 = new Intent(getApplicationContext(), LogInActivity.class);
					startActivity(intent1);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			Intent intent1 = new Intent(getApplicationContext(), LogInActivity.class);
			startActivity(intent1);
		}

		new AgregarChatsActivos( ).execute( );


		// Refresh Button
		try {
			Button refreshButton = (Button) findViewById(R.id.buttonActivePeople);
			refreshButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					CharSequence text = "Refreshing...";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(getApplicationContext(), text, duration);
					toast.show();
					new AgregarChatsActivos( ).execute( );

				}
			});			

		} catch (Exception e) {
			e.printStackTrace();
		}


		// ACTIVE CONTACT`S LIST

		listActivePeople.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				CharSequence text = "Please wait...";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show();

				String idString = listActivePeople.getItemAtPosition(position).toString();
				String idChar[] = idString.split("Id=");
				activeId = idChar[1].replace("}", "");
				cambiarNombreTrabajo(activeId);
				
				Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
				Bundle b = new Bundle();
				b.putString("activeEventId", active_event);
				b.putString("chatUserId", activeId);
				b.putString("loggedUserId", activeUserId);
				intent.putExtras(b);
				startActivity(intent);
				finish();
			}	

		});

		//Long click para eliminar chat activo

		listActivePeople.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//Pregunta si desea eliminar chat 
				dialog(position);
				return true;
			}

		});

	}

	private class AgregarChatsActivos extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog = new ProgressDialog(ActivePeopleListActivity.this);

		List<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Cargando Chat's activos");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {

			ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Chat");
			query1.whereEqualTo("event", ParseObject.createWithoutData("Event", active_event));
			query1.whereEqualTo("user1", ParseObject.createWithoutData("_User", activeUserId));
			query1.whereEqualTo("active1", true);

			ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Chat");
			query2.whereEqualTo("event", ParseObject.createWithoutData("Event", active_event));
			query2.whereEqualTo("user2", ParseObject.createWithoutData("_User", activeUserId));
			query2.whereEqualTo("active2", true);

			queryList.add(query1);
			queryList.add(query2);

			return "Termino";

		}

		@Override
		protected void onPostExecute(String result) {		

			//Creo el condicional OR de la busqueda - Si esta activo en alguno de los dos users lo agrego
			ParseQuery<ParseObject> queryOr = ParseQuery.or(queryList);

			try{
				List<ParseObject> postList = queryOr.find();

				if(!postList.isEmpty()) //Pregunto si tiene algun chat activo
				{
					for(ParseObject post: postList)
					{
						activeChatId = post.getObjectId();						
						post.getParseObject("user1").fetchIfNeededInBackground(new GetCallback<ParseObject>() {

							@Override
							public void done(ParseObject user, ParseException e) {
								if(e==null)
								{
									if(!user.getObjectId().equals(activeUserId))
									{
										activeName = user.getString("name")+" "+user.getString("lastName");
										activeJob = user.getString("career");
										activeId = user.getObjectId();

										map2 = new HashMap<String, String>();
										map2.put(TAG_NAME_ACTIVE, activeName);
										map2.put(TAG_JOB_ACTIVE, activeJob);
										map2.put(TAG_ID_ACTIVE, activeId);										


										if(!activeContactList.contains(map2))
										{
											activeContactList.add(map2);									
										}
										((BaseAdapter) activeAdapter).notifyDataSetChanged();
										listActivePeople.setAdapter(activeAdapter);							

									}
								}
								else
								{
									Log.d("Post retrieval", "Error: " + e.getMessage());
								}
							}
						});

						post.getParseObject("user2").fetchIfNeededInBackground(new GetCallback<ParseObject>() {

							@Override
							public void done(ParseObject user,ParseException e) {
								if(e==null)
								{
									if(!user.getObjectId().equals(activeUserId))
									{
										activeName = user.getString("name")+" "+user.getString("lastName");
										activeJob = user.getString("career");
										activeId = user.getObjectId();

										map2 = new HashMap<String, String>();
										map2.put(TAG_NAME_ACTIVE, activeName);
										map2.put(TAG_JOB_ACTIVE, activeJob);
										map2.put(TAG_ID_ACTIVE, activeId);										


										if(!activeContactList.contains(map2))
										{
											activeContactList.add(map2);									
										}
										((BaseAdapter) activeAdapter).notifyDataSetChanged();
										listActivePeople.setAdapter(activeAdapter);		
									}
								}
								else
								{
									Log.d("Post retrieval", "Error: " + e.getMessage());
								}
							}

						});
						
					}					
				}
				dialog.dismiss();

			}
			catch(ParseException e){
				e.printStackTrace();
			}
		}
	}

	public String getActiveName() {
		return activeName;
	}

	public String getActiveJob() {
		return activeJob;
	}

	public String getActiveId()	{
		return activeId;
	}
	/**
	 * Dialogo para eliminar un chat 
	 * @param nPosition la posicion de la lista de usuarios activos
	 */

	@SuppressWarnings("deprecation")
	private void dialog(final int nPosition) {
		AlertDialog dialogAlert = new AlertDialog.Builder(ActivePeopleListActivity.this).create();
		dialogAlert.setMessage("Delete active chat ?");
		dialogAlert.setButton("Delete",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){
				ActivePeopleListActivity.this.activeContactList.remove(nPosition);
				setActiveChat(false);
				((BaseAdapter) ActivePeopleListActivity.this.activeAdapter).notifyDataSetChanged();

			}
		});
		dialogAlert.setButton2("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){
				dialog.cancel();
			}
		});

		dialogAlert.show();
	}

	private void cambiarNombreTrabajo(String id)
	{
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatUser");
			query.whereEqualTo("objectId", id);

			List<ParseObject> postList = query.find();
			for(ParseObject post: postList)
			{
				String firstName = post.getString("Nombre");	
				String lastName = post.getString("Apellido");
				activeName = firstName+" "+lastName;
				String job = post.getString("Profesion");
				activeJob = job;
			}

		} catch (ParseException e) {
			Log.d("Post retrieval", "Error: " + e.getMessage());
		}
	}

	public static ActivePeopleListActivity darInstancia( )
	{
		return instancia;
	}

	/**
	 * Setting the boolean of an active message in Parse
	 */
	public void setActiveChat(boolean activeBoolean)
	{
		try {
			//Find if exists any chatmessage active with the actual chat_id
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");
			query.whereEqualTo("objectId", activeChatId);

			List<ParseObject> activeChats = query.find();
			if(!activeChats.isEmpty()){

				for(ParseObject chat: activeChats)
				{
					String idUser1 = chat.getParseObject("user1").getObjectId();
					System.out.println(idUser1);

					if(idUser1.equals(activeUserId))
					{
						chat.put("active1", activeBoolean);
						chat.save();
					}
					if(!idUser1.equals(activeUserId))
					{
						chat.put("active2", activeBoolean);
						chat.save();
					}
				}
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
			Log.d("Setting active a chat", "Error: " + e1.getMessage());
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
	@Override
	public void onDestroy() {
		super.onDestroy();
	}


}

