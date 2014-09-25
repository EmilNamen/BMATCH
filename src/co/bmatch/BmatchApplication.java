package co.bmatch;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class BmatchApplication extends Application  {
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Parse.enableLocalDatastore(getApplicationContext());
		Parse.initialize(this, "9dP58JXjR2kZ4YYOtxqAg6wvTmQQjK4dHXwVZBAs", "gxSDz7iOaxd7pZQHmSaTm8S0TLEms2qkLeduTeEO");

	}

}
