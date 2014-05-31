package co.bmatch;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PushHandler extends Activity{

	private Context activityContext;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityContext = this;

		// Set the user interface layout for this Activity
		// The layout file is defined in the project res/layout/main_activity.xml file
		setContentView(R.layout.pushhandler_main);
		
//		Intent intentList = new Intent( activityContext, PeopleListActivity.class );
//		startActivity( intentList );

	}

}
