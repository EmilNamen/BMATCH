package co.bmatch;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LogInActivity extends Activity{

	/**
	 * GCM CONSTANTS
	 */
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";

	private static LogInActivity instancia;


	private EditText firstName;
	private EditText lastName;
	private EditText email;
	private EditText job;
	private EditText code;
	
	public final static String EVENT_MESSAGE = "co.bmatch.event_message";
	
	public final static String CHAT_USER_ID = "co.bmatch.chat_user_id";
	private String chat_user_id;

	private ParseUser user;

	/**
	 * Project number
	 */
	String SENDER_ID = "163849948052"; 

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "BMATCH";


	private Context activityContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activityContext = getApplicationContext();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//PARSE
		Parse.initialize(this, "STb6FUhezrSjIdBuTdt7RXeKIRZ3uFZv2CXBKqit", "xXKovwAw1g0rkiThTXkOgPOz8OzI9CnhmqgNwWE6"); 
		
		firstName = (EditText) findViewById(R.id.EditTextfirstName);
		lastName = (EditText) findViewById(R.id.EditTextlastName);
		email = (EditText) findViewById(R.id.EditTextemail);
		job = (EditText) findViewById(R.id.EditTextjob);
		code = (EditText) findViewById(R.id.EditTextcode);
		
		Button login = (Button) findViewById(R.id.buttonLOGIN);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkUserData();
				buttonLogIn();			
			}
		});

	}


	public void listPeopleView() {    	
		Intent intentList = new Intent( activityContext, PeopleListActivity.class );
		intentList.putExtra(EVENT_MESSAGE, getEventCode());
		intentList.putExtra(CHAT_USER_ID, chat_user_id);
		startActivity( intentList );	
	
			
	}

	private String getEventCode()
	{
		return code.getText().toString();
	}
	public String getUserFirstName()
	{
		return firstName.getText().toString();
	}
	public String getUserLastName()
	{
		return lastName.getText().toString();
	}
	public String getUserJob()
	{
		return job.getText().toString();
	}
	public String getUserEmail()
	{
		return email.getText().toString();
	}
	
	private boolean checkUserData()
	{
		boolean ok = false;
		//Reviso que sean entradas de texto v‡lidas

		String firstNameString = firstName.getText().toString();
		String lastNameString = lastName.getText().toString();
		String emailString = email.getText().toString();
		String jobString = job.getText().toString();
		String codeString = code.getText().toString();

		if(firstNameString.isEmpty() == true || firstNameString.length()>30)
		{
			firstName.setError("Nombre no v‡lido");
		}
		else if(lastNameString.isEmpty() == true || lastNameString.length()>30)
		{
			lastName.setError("Apellido no v‡lido");
		}
		else if(emailString.isEmpty() == true || emailString.contains("@") != true || emailString.contains(".") != true)
		{
			email.setError("Email no v‡lido");
		}
		else if(jobString.isEmpty() == true)
		{
			job.setError("Profesi—n no v‡lida");
		}
		else if(codeString.isEmpty() == true)
		{
			code.setError("C—digo no v‡lido");
		}
		else
		{
			ok = true;
		}
		return ok;
	}


	public void buttonLogIn()
	{
		// Se realiza el login a BMATCH

		if(checkUserData() == false)
		{
			Log.d("User Data", "Error");
		}
		else
		{
			//Realiza el registro
			userLogIn();

			// Create query for objects of type "Post"
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Evento");

			// Restrict to cases where the author is the current user.
			// Note that you should pass in a ParseUser and not the
			// String representation of that user
			query.whereEqualTo("objectId", getEventCode());
			// Run the query
			ParseObject event =  null;
			try {
				List<ParseObject> events = query.find();
				for(ParseObject i: events)
				{
					event = i;
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			if( event == null )
			{
				Log.d("BMATCH", "Error: No se encontr— el evento");
				code.setError("C—digo no v‡lido");
			}
			else
			{
				ParseObject chatUser = new ParseObject("ChatUser");
				chatUser.put("Nombre", getUserFirstName());
				chatUser.put("Apellido", getUserLastName());
				chatUser.put("Profesion", getUserJob());
				chatUser.put("User", user);
				chatUser.put("Evento", ParseObject.createWithoutData("Evento", getEventCode()));

				try {
					chatUser.save();
					chat_user_id = chatUser.getObjectId().toString();
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// Muestra la lista de los usuarios registrados
				listPeopleView();
			}

		}
	}

	
	
	private void userLogIn()
	{
		try {
			user = ParseUser.logIn(getUserEmail(), getUserFirstName()+getUserLastName());
		} catch (ParseException e) {
			e.printStackTrace();
			userSignUp();
		}		
	}

	private void userSignUp()
	{
		user = new ParseUser();
		user.setUsername(getUserEmail());
		user.setPassword(getUserFirstName()+getUserLastName());
		user.setEmail(getUserEmail());
		try {
			user.signUp();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
