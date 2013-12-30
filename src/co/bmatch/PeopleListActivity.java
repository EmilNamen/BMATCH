package co.bmatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PeopleListActivity extends Activity{


	private Context activityContext;
	private EditText textSearch;
	
	private String chat_user_id;

	public static final String TAG_NAME = "Name";
	public static final String TAG_JOB = "Job";
	public static final String TAG_ID = "Id";
	
	

	public static final String TAG_NAME_ACTIVE = "Name";
	public static final String TAG_JOB_ACTIVE = "Job";
	public static final String TAG_ID_ACTIVE = "Id";
	
	
	public final static String CHAT_USER_NAME = "co.bmatch.user_name";
	public final static String CHAT_USER_JOB = "co.bmatch.user_job";
	public final static String CHAT_USER_ID = "co.bmatch.user_id";
	public final static String CHAT_USER_ID_MASTER = "co.bmatch.user_id_master";
	
	


	private static PeopleListActivity instancia;

	private String activeName;
	private String activeJob;
	private String activeId;

	public String activeSelectedName;
	public String activeSelectedJob;

	private ListView listPeople = null;
	private ArrayList<HashMap<String, String>> contactList;	
	private ListAdapter adapter;


	private ListView listActivePeople = null;
	private ArrayList<HashMap<String, String>> activeContactList;	
	private ListAdapter activeAdapter;
	
	private List<ParseObject> postList;

	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.peoplelist_main);
		super.onCreate(savedInstanceState);

		activityContext = getApplicationContext();
		instancia = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Get chat_user_id
		Intent intent = getIntent();
		chat_user_id = intent.getStringExtra(LogInActivity.CHAT_USER_ID);

		// Activo la busqueda en la lista

		searchInList();	

		// Lista de contactos 
		contactList = new ArrayList<HashMap<String,String>>();
		listPeople = (ListView) findViewById(R.id.listViewPeople);
		adapter = new SimpleAdapter(activityContext, contactList, R.layout.contact_main,
				new String[] {TAG_NAME, TAG_JOB},  new int[] {R.id.textName, R.id.textJob});

		// Lista de contactos activos
		activeContactList = new ArrayList<HashMap<String,String>>();		
		listActivePeople = (ListView) findViewById(R.id.listViewActivePeople);
		setListVisible(false);
		activeAdapter = new SimpleAdapter(activityContext, activeContactList, R.layout.contact_main,
				new String[] {TAG_NAME, TAG_JOB},  new int[] {R.id.textName, R.id.textJob});


		// Query users of event
		searchUsersOfEvent();
		
		// Call chat		
		callUserChat();
	}
	
	public void callUserChat()
	{
		// CONTACT`S LIST
		try{
			listPeople.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					try{
						setListVisible(true);
						HashMap<String,String> map2 = new HashMap<String, String>();
						activeName = contactList.get(position).get(TAG_NAME).toString();
						activeJob = contactList.get(position).get(TAG_JOB).toString();
						
						activeId = postList.get(position).getObjectId();
						System.out.println(activeId);
						map2.put(TAG_NAME_ACTIVE, activeName);
						map2.put(TAG_JOB_ACTIVE, activeJob);
//						map2.put(TAG_ID_ACTIVE, activeId);

						// adding HashList to ArrayList
						activeContactList.add(map2);
						((BaseAdapter) activeAdapter).notifyDataSetChanged();
						Intent intent = new Intent(activityContext, ChatActivity.class);
						intent.putExtra(CHAT_USER_NAME, getActiveName());
						intent.putExtra(CHAT_USER_JOB, getactiveJob());
						intent.putExtra(CHAT_USER_ID, getactiveId());
						intent.putExtra(CHAT_USER_ID_MASTER, chat_user_id);
						
						startActivity(intent);
						listActivePeople.setAdapter(activeAdapter);
					}
					catch (Exception e)
					{
						Log.d("TAG_CHAT", "Error: " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
		}
		catch(Exception e)
		{
			Log.d("TAG_NAME", "Error: " + e.getMessage());
			e.printStackTrace();
		}
		
		// ACTIVE CONTACT`S LIST
		
		listActivePeople.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activeName = activeContactList.get(position).get(TAG_NAME_ACTIVE).toString();
				activeJob = activeContactList.get(position).get(TAG_JOB_ACTIVE).toString();
//				activeId = activeContactList.get(position).get(TAG_ID_ACTIVE).toString();
				Intent intent = new Intent(activityContext, ChatActivity.class);
				startActivity(intent);
			}	

		});
	}
	

	public void searchUsersOfEvent()
	{
		Intent intent = getIntent();
		String event_message = intent.getStringExtra(LogInActivity.EVENT_MESSAGE);
		// Create query for objects of type "Post"
		ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatUser");

		// Restrict to cases where the author is the current user.
		// Note that you should pass in a ParseUser and not the		
		// String representation of that user
		query.whereEqualTo("Evento", ParseObject.createWithoutData("Evento", event_message));
		try {
			postList = query.find();
			contactList.clear();
			for(ParseObject post: postList)
			{
				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();
				String firstName = post.getString("Nombre");	
				// adding each child node to HashMap key => value
				String lastName = post.getString("Apellido");
				map.put(TAG_NAME, firstName + " " + lastName);
				String job = post.getString("Profesion");
				map.put(TAG_JOB, job);
//				String id = post.getObjectId();
//				map.put(TAG_ID, id);
				

				// adding HashList to ArrayList
				contactList.add(map);
			}
			((BaseAdapter) adapter).notifyDataSetChanged();

		} catch (ParseException e) {
			Log.d("Post retrieval", "Error: " + e.getMessage());
		}
		
		listPeople.setAdapter(adapter);
	}

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

	public static PeopleListActivity darInstancia ( )	{
		return instancia;
	}

	private String getActiveName()
	{
		return activeName;
	}

	private String getactiveJob()
	{
		return activeJob;
	}
	
	private String getactiveId()
	{
		return activeId;
	}

	private void setListVisible ( boolean isVisible ) {
		listActivePeople.setVisibility( isVisible ? View.VISIBLE : View.INVISIBLE );
	}
}
