package co.bmatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EventsHistoryActivity extends Activity{
	
	
	private ParseUser currentUser;
	private ArrayList<HashMap<String, String>> eventsArraylist;	
	private ListAdapter adapter;
	private ListView events;
	
	public static final String TAG_NAME_EVENT = "Name";
	public static final String TAG_DATE_EVENT = "Date";
	public static final String TAG_ID_EVENT = "Id";
	
	private String idEventSelected;
	
	private static EventsHistoryActivity instancia;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventshistory_main);
		
		instancia = this;
		
		try {
			currentUser = ParseUser.getCurrentUser();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		events = (ListView) findViewById(R.id.listViewEventsHistory);
		events.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				//GET THE SELECTED EVENT ID
				
				String idString = events.getItemAtPosition(position).toString();
				String idChar[] = idString.split("Id=");
				idEventSelected = idChar[1].replace("}", "");
				
				Intent intentMain = new Intent(getApplicationContext(), TabBar.class );
				startActivity( intentMain );
				
			}
		});
		
		
		eventsArraylist = new ArrayList<HashMap<String, String>>();		
		adapter = new SimpleAdapter(getApplicationContext(), eventsArraylist , R.layout.event_main,
				new String[] {TAG_NAME_EVENT,TAG_DATE_EVENT},  new int[] {R.id.textEventName, R.id.textEventJob});
		events.setAdapter(adapter);
		
		CharSequence text = "Loading events...";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText( getApplicationContext( ), text, duration );
		toast.show( );

		
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("EventUser");
			query.whereEqualTo("user", currentUser);
			query.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(List<ParseObject> events, ParseException e) {
					if(e==null)
					{
						for(ParseObject event: events)
						{
							event.getParseObject("event").fetchIfNeededInBackground(new GetCallback<ParseObject>() {

								@SuppressWarnings("deprecation")
								@Override
								public void done(ParseObject object, ParseException e) {
									
									HashMap<String, String> map = new HashMap<String, String>();	
									String eventName = object.getString("name");
									System.out.println(eventName);
									map.put(TAG_NAME_EVENT, eventName);
									Date eventDate = object.getCreatedAt();
									System.out.println(eventDate);
									map.put(TAG_DATE_EVENT, eventDate.toGMTString());
									String eventId = object.getObjectId();
									map.put(TAG_ID_EVENT, eventId);
									eventsArraylist.add(map);
									((BaseAdapter) adapter).notifyDataSetChanged();
								}
								
								
							});
						}

					}
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static EventsHistoryActivity darInstancia( ){
		return instancia;
	}
	
	public String getIdSelectedEventId( ){
		
		return idEventSelected;
	}

}
