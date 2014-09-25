package co.bmatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * Actividad que muestra la lista de personas activas en el evento dado por el usuario
 * @author emilcamilonamenleon
 *
 */
public class PeopleListActivity extends Activity{


	private EditText textSearch;

	/*
	 * Chat User principal id
	 */
	private String chat_user_id;

	private String active_event;

	public static final String TAG_NAME = "Name";
	public static final String TAG_JOB = "Job";
	public static final String TAG_ID = "Id";

	private String activeName;
	private String activeJob;
	private String activeId;

	private ListView listPeople = null;
	private ArrayList<HashMap<String, String>> contactList;	
	private ListAdapter adapter;


	private List<ParseObject> postList;

	private static PeopleListActivity instancia;


	private String userInfo;

	private String loggedUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		instancia = this;
		setContentView(R.layout.peoplelist_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


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


		loggedUserId = ParseUser.getCurrentUser().getObjectId().toString();
		//Parse push notifications		
		try {
			ParseInstallation pi = ParseInstallation.getCurrentInstallation();
			PushService.subscribe(getApplicationContext(), loggedUserId, ChatActivity.class);
			ParseAnalytics.trackAppOpened(getIntent());
			pi.saveEventually();


		} catch (Exception e) {
			e.printStackTrace();
		}


		// Activo la busqueda en la lista

		searchInList();	


		// Refresh button

		Button buttonRefresh = (Button) findViewById(R.id.buttonPeople);
		buttonRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CharSequence text = "Refreshing...";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show();

				// Query users of event
				new SearchPeopleEvent( ).execute( );
			}
		});

		// Lista de contactos 
		contactList = new ArrayList<HashMap<String,String>>();
		listPeople = (ListView) findViewById(R.id.listViewPeople);
		adapter = new SimpleAdapter(getApplicationContext(), contactList, R.layout.contact_main,
				new String[] {TAG_NAME, TAG_JOB},  new int[] {R.id.textName, R.id.textJob});
		activeId = "";


		// Query users of event
		new SearchPeopleEvent( ).execute( );


		listPeople.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	

				userInfo = listPeople.getItemAtPosition(position).toString();

				//CALL NEW CHAT
				CharSequence text = "Please wait...";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show();

				String idString = listPeople.getItemAtPosition(position).toString();
				String idChar[] = idString.split("Id=");
				activeId = idChar[1].replace("}", "");
				cambiarNombreTrabajo(activeId);

				Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
				Bundle b = new Bundle();
				b.putString("activeEventId", active_event);
				b.putString("chatUserId", activeId);
				b.putString("loggedUserId", loggedUserId);
				intent.putExtras(b);
				startActivity(intent);
				finish();


			}
		});
	}

	private void cambiarNombreTrabajo(String id)
	{
		ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
		query.whereEqualTo("objectId", id);
		try {
			List<ParseObject> postList = query.find();
			for(ParseObject post: postList)
			{
				String firstName = post.getString("name");	
				String lastName = post.getString("lastName");
				activeName = firstName+" "+lastName;
				String job = post.getString("career");
				activeJob = job;
			}

		} catch (ParseException e) {
			Log.d("Post retrieval", "Error: " + e.getMessage());
		}
	}



	/**
	 * Clase que busca los usuarios dado un evento
	 */
	private class SearchPeopleEvent extends AsyncTask<Void, Void, String> {	

		// Create query for objects of type "Post"
		ParseQuery<ParseObject> query = ParseQuery.getQuery("EventUser");

		@Override
		protected String doInBackground(Void... params) {
			return "Searching people in event...";
		}
		@Override
		protected void onPreExecute() {
			// Restrict to cases where the author is the current user.
			// Note that you should pass in a ParseUser and not the		
			// String representation of that user
			query.whereEqualTo("event", ParseObject.createWithoutData("Event", active_event));
		}
		@Override
		protected void onPostExecute(String result) {
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> list, ParseException e) {
					if (e == null) {
						postList = list;
						contactList.clear();
						for(ParseObject post: postList)
						{
							post.getParseObject("user").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
								public void done(ParseObject object, ParseException e) {
									// all fields of the object will now be available here.
									// creating new HashMap
									if (object!=null) {
										HashMap<String, String> map = new HashMap<String, String>();
										String firstName = object
												.getString("name");
										// adding each child node to HashMap key => value
										String lastName = object
												.getString("lastName");
										map.put(TAG_NAME, firstName + " "
												+ lastName);
										String job = object.getString("career");
										String company = object.getString("company");
										map.put(TAG_JOB, job + "   "+ company);
										String id = object.getObjectId()
												.toString();
										map.put(TAG_ID, id);
										// adding HashList to ArrayList
										if (!id.equals(loggedUserId))
											contactList.add(map);
										((BaseAdapter) adapter)
										.notifyDataSetChanged();
									}
								}
							});
						}


					} else {
						Log.d("BMATCH", "Error: " + e.getMessage());
					}
				}
			});
			listPeople.setAdapter(adapter);

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	/**
	 * Metodo que a traves de un EditText realiza la busqueda de los usuarios
	 */
	public void searchInList()
	{
		textSearch = (EditText) findViewById(R.id.EditTextSearch);
		textSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				((Filterable) adapter).getFilter().filter(s);

			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {				
			}
		});
	}

	public String getUserActiveId()	{
		return chat_user_id;
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

	public String getActiveUserInfo()
	{
		return userInfo;
	}
	public static PeopleListActivity darInstancia()
	{
		return instancia;
	}

	public ArrayList<HashMap<String, String>> getActivePeople()
	{
		return contactList;
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
