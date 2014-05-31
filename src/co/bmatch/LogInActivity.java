package co.bmatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * MainActivity se realiza el registro de los datos
 * @author emilcamilonamenleon
 *
 */
public class LogInActivity extends Activity{


	private EditText firstName;
	private EditText lastName;
	private EditText email;
	private EditText job;
	private EditText company;

	public final static String EVENT_MESSAGE = "co.bmatch.event_message";
	public final static String CHAT_USER_ID = "co.bmatch.chat_user_id";
	private String chat_user_id;

	private ParseUser user;

	private ProgressDialog dialog;

	private String data;


	/**
	 * Project number
	 */
	String SENDER_ID = "163849948052"; 

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "BMATCH";

	
	private static LogInActivity instancia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		instancia = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		firstName = (EditText) findViewById(R.id.EditTextfirstName);
		lastName = (EditText) findViewById(R.id.EditTextlastName);
		email = (EditText) findViewById(R.id.EditTextemail);
		job = (EditText) findViewById(R.id.EditTextjob);
		company = (EditText) findViewById(R.id.EditTextCompany);
		if(firstName.getText().toString().isEmpty() && lastName.getText().toString().isEmpty() )
		{
			try{
				read();
			}
			catch(Exception e)
			{
				Log.d("Reading mydata", "error: "+e.getMessage());
			}
		}
		Button login = (Button) findViewById(R.id.buttonLOGIN);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CharSequence text = "Please wait...";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show();
				checkUserData();
				save();
				buttonLogIn();
			}
		});
	}


	@SuppressLint("WorldWriteableFiles")
	@SuppressWarnings("deprecation")
	public void save( ){
		data = 	firstName.getText().toString()+"-"+
				lastName.getText().toString()+"-"+
				email.getText().toString()+"-"+
				job.getText().toString()+"-"+
				company.getText().toString();
		try {
			FileOutputStream fOut = openFileOutput("mydata",MODE_WORLD_WRITEABLE);
			fOut.write(data.getBytes());
			fOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void read( ){
		try{
			FileInputStream fin = openFileInput("mydata");
			int c;
			String temp="";
			String datas[] = null;
			while( (c = fin.read()) != -1){
				temp = temp + Character.toString((char)c);
				datas = temp.split("-");
			}
			System.out.println(temp);
			firstName.setText(datas[0]);
			lastName.setText(datas[1]);
			email.setText(datas[2]);
			job.setText(datas[3]);
			company.setText(datas[4]);

		}catch(Exception e){

		}
	}
	/**
	 * Metodo que realiza la llamada a PeopleListActivity la cual desplega la lista de usuarios registrados
	 * en el evento dado por el codigo.
	 * Le pasa el codigo del evento
	 * Le pasa el codigo del usuario actual
	 */

	public void listPeopleView() { 	

		dialog = ProgressDialog.show(this, "BMATCH", "Validating Info...",true);
		new Thread(new Runnable() {	
			@Override
			public void run()
			{
				Intent intentList = new Intent( getApplicationContext(), PeopleListActivity.class );
				intentList.putExtra(EVENT_MESSAGE, getEventCode());
				intentList.putExtra(CHAT_USER_ID, chat_user_id);
				startActivity( intentList );
				dialog.dismiss();
			}
		}).start();
	}

	private String getEventCode()
	{
		return FirstActivity.darInstancia().getEventCode();
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
	public String getUserCompany()
	{
		return company.getText().toString();
	}
	private ParseUser getUser()
	{
		return user;
	}

	/**
	 * Metodo que revisa si los datos ingresados son validos para el registro
	 * @return True si son validos, False de lo contrario
	 */

	private boolean checkUserData()
	{

		boolean ok = false;
		//Reviso que sean entradas de texto validas

		String firstNameString = firstName.getText().toString();
		String lastNameString = lastName.getText().toString();
		String emailString = email.getText().toString();
		String jobString = job.getText().toString();
		String companyString = company.getText().toString();

		if(firstNameString.isEmpty() == true || firstNameString.length()>30)
		{
			firstName.setError("Nombre no valido");
		}
		else if(lastNameString.isEmpty() == true || lastNameString.length()>30)
		{
			lastName.setError("Apellido no valido");
		}
		else if(emailString.isEmpty() == true || emailString.contains("@") != true || emailString.contains(".") != true)
		{
			email.setError("Email no valido");
		}
		else if(jobString.isEmpty() == true)
		{
			job.setError("Profesion no valida");
		}
		else if(companyString.isEmpty() == true)
		{
			company.setError("Empresa no valida");
		}	
		else
		{
			ok = true;
		}
		return ok;
	}

	/**
	 * Metodo que realiza el registro con Parse.com
	 */

	public void buttonLogIn()
	{
		CharSequence text = "Doing the log-in";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(getApplicationContext(), text, duration);
		toast.show();
		// Se realiza el login a BMATCH

		if(checkUserData() == false)
		{
			Log.d("User Data", "Error");
		}
		else
		{
			//Realiza el registro
			userLogIn();

			// Reviso si existe el chatUser
			ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("ChatUser");
			queryUser.whereEqualTo("User", getUser());
			try {
				List<ParseObject> usersChat = queryUser.find();
				if(usersChat.isEmpty())
				{
					ParseObject chatUser = new ParseObject("ChatUser");
					chatUser.put("Nombre", getUserFirstName());
					chatUser.put("Apellido", getUserLastName());
					chatUser.put("Profesion", getUserJob());
					chatUser.put("Company", getUserCompany());
					chatUser.put("User", user);
					chatUser.put("Evento", ParseObject.createWithoutData("Evento", getEventCode()));

					chatUser.save();
					chat_user_id = chatUser.getObjectId().toString();
				}
				else
				{
					ParseObject pO = usersChat.get(0);
					chat_user_id = pO.getObjectId().toString();
					// Muestra la lista de los usuarios registrados
					listPeopleView();
				}

			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Metodo que realiza el log in es decir el registro si ya esta en la base de datos
	 */	
	private void userLogIn()
	{
		try {
			user = ParseUser.logIn(getUserEmail(), getUserFirstName()+getUserLastName());
		} catch (ParseException e) {
			e.printStackTrace();
			userSignUp();
		}		
	}

	/**
	 * Metodo que realiza el sign up es decir el registro no esta en la base de datos
	 */
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
	
	public static LogInActivity darInstancia()
	{
		return instancia;
	}
	
	@Override
	protected void onPause() {
        super.onPause();
    }
}
