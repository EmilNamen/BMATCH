package co.bmatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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

	public static final String TAG_NAME = "Name";
	public static final String TAG_JOB = "Job";
	public static final String TAG_ID = "Id";


	public static final String TAG_NAME_ACTIVE = "Name";
	public static final String TAG_JOB_ACTIVE = "Job";
	public static final String TAG_ID_ACTIVE = "Id";



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
	private HashMap<String,String> map2;

	private List<ParseObject> postList;

	private static PeopleListActivity instancia;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		instancia = this;
		setContentView(R.layout.peoplelist_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		// Get chat_user_id
		Intent intent = getIntent();
		chat_user_id = intent.getStringExtra(LogInActivity.CHAT_USER_ID);


		//Parse push notifications
		ParseInstallation pi = ParseInstallation.getCurrentInstallation();
		System.out.println(chat_user_id);
		PushService.subscribe(getApplicationContext(), chat_user_id, PushHandler.class);		
		ParseAnalytics.trackAppOpened(getIntent());
		pi.saveEventually();

		// Activo la busqueda en la lista

		searchInList();	

		// Lista de contactos 
		contactList = new ArrayList<HashMap<String,String>>();
		listPeople = (ListView) findViewById(R.id.listViewPeople);
		adapter = new SimpleAdapter(getApplicationContext(), contactList, R.layout.contact_main,
				new String[] {TAG_NAME, TAG_JOB},  new int[] {R.id.textName, R.id.textJob});

		// Lista de contactos activos
		activeContactList = new ArrayList<HashMap<String,String>>();		
		listActivePeople = (ListView) findViewById(R.id.listViewActivePeople);
		setListVisible(false);
		activeAdapter = new SimpleAdapter(getApplicationContext(), activeContactList, R.layout.contact_main,
				new String[] {TAG_NAME_ACTIVE, TAG_JOB_ACTIVE},  new int[] {R.id.textName, R.id.textJob});


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
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					try{
						setListVisible(true);

						//Mensaje TOAST
						CharSequence text = "Please wait...";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(getApplicationContext(), text, duration);
						toast.show();

						//AGREGO A LISTA ARRIBA						
						activeName = contactList.get(position).get(TAG_NAME).toString();
						activeJob = contactList.get(position).get(TAG_JOB).toString();
						activeId = postList.get(position).getObjectId().toString();

						if(activeAdapter.isEmpty( )==true)
						{
							map2 = new HashMap<String, String>();
							map2.put(TAG_NAME_ACTIVE, activeName);
							map2.put(TAG_JOB_ACTIVE, activeJob);
							map2.put(TAG_ID_ACTIVE, activeId);
							activeContactList.add(map2);
						}
						else
						{
							map2 = new HashMap<String, String>();
							activeName = contactList.get(position).get(TAG_NAME).toString();
							activeJob = contactList.get(position).get(TAG_JOB).toString();
							activeId = postList.get(position).getObjectId().toString();
							boolean encontro = false;
							for (int i = 0; i < activeContactList.size(); i++) {
								HashMap<String, String> a = activeContactList.get(i);
								System.out.println("//////////----");
								System.out.println(a.get(TAG_ID_ACTIVE));
								System.out.println(activeId);
								if(a.get(TAG_ID_ACTIVE).equals(activeId)==true)
								{
									Log.d("EMIL", "URDMEN");
									i=activeContactList.size();
									encontro = true;
								}
							}
							if(encontro==false)
							{
								map2.put(TAG_NAME_ACTIVE, activeName);
								map2.put(TAG_JOB_ACTIVE, activeJob);
								map2.put(TAG_ID_ACTIVE, activeId);
								activeContactList.add(map2);
							}
						}
						((BaseAdapter) activeAdapter).notifyDataSetChanged();	
						Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
						startActivity(intent);
						listActivePeople.setAdapter(activeAdapter);

						//TODO Set a max heigh for the activeAdapter
						if(listActivePeople.getCount()>=5)
						{
							Display display = getWindowManager().getDefaultDisplay(); 
							int height = display.getHeight();  // deprecated
							listPeople.setMinimumHeight(height/2);
							listActivePeople.setFastScrollAlwaysVisible(true);								
						}
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

				CharSequence text = "Please wait...";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show();

				activeName = activeContactList.get(position).get(TAG_NAME_ACTIVE).toString();
				activeJob = activeContactList.get(position).get(TAG_JOB_ACTIVE).toString();
				activeId = activeContactList.get(position).get(TAG_ID_ACTIVE).toString();
				Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
				startActivity(intent);
			}	

		});


		//Long click para eliminar chat activo

		listActivePeople.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				String str=listActivePeople.getItemAtPosition(position).toString();
				Log.d("ITEM: ", "long click : " +str);
				dialog(position);
				return true;
			}

		});
	}

	/**
	 * Dialogo para eliminar un chat 
	 * @param nPosition la posicion de la lista de usuarios activos
	 */

	@SuppressWarnings("deprecation")
	private void dialog(final int nPosition) {
		AlertDialog dialogAlert = new AlertDialog.Builder(PeopleListActivity.this).create();
		dialogAlert.setMessage("Delete active chat ?");
		dialogAlert.setButton("Delete",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){
				PeopleListActivity.this.activeContactList.remove(nPosition);
				((BaseAdapter) PeopleListActivity.this.activeAdapter).notifyDataSetChanged();

			}
		});
		dialogAlert.setButton2("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){
				dialog.cancel();
			}
		});

		dialogAlert.show();
	}

	/**
	 * Metodo que busca los usuarios dado un evento
	 */
	public void searchUsersOfEvent()
	{
		Intent intent = getIntent();
		String event_message = intent.getStringExtra(LogInActivity.EVENT_MESSAGE);
		// Create query for objects of type "Post"
		ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatUser");
		query.orderByAscending("Nombre");
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

				// adding HashList to ArrayList
				contactList.add(map);
			}
			((BaseAdapter) adapter).notifyDataSetChanged();

		} catch (ParseException e) {
			Log.d("Post retrieval", "Error: " + e.getMessage());
		}

		listPeople.setAdapter(adapter);
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



	public String getActiveName()
	{
		return activeName;
	}

	public String getactiveJob()
	{
		return activeJob;
	}

	public String getActiveId()
	{
		return activeId;
	}
	public String getUserActiveId()
	{
		return chat_user_id;
	}
	
	/**
	 * Metodo encargado de no mostrar la lista de usuarios activos hasta que exista alguno
	 * @param isVisible Si exsite un usuario para agregar a la lista
	 */
	private void setListVisible ( boolean isVisible ) {
		listActivePeople.setVisibility( isVisible ? View.VISIBLE : View.INVISIBLE );
	}

	public static PeopleListActivity darInstancia()
	{
		return instancia;
	}
	
	@Override
	protected void onPause() {
        super.onPause();
    }
}
