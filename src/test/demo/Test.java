package test.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.insready.drupalcloud.RESTServerClient;
import com.insready.drupalcloud.ServiceNotAvailableException;

public class Test extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView tv = (TextView) findViewById(R.id.text);
		RESTServerClient sandbox = new RESTServerClient(this,
				getString(R.string.sharedpreferences_name),
				getString(R.string.SERVER), getString(R.string.DOMAIN),
				Long.valueOf(getString(R.string.SESSION_LIFETIME)));
		String result = null;
		try {
			// result = sandbox.nodeGet(2,"");
			Boolean haha = sandbox.flagIsFlagged("spam", 2, 2);
			if (haha = false) {
				result = "this content is flagged";
			} else {
				result = "this content is not flagged";
			}
		} catch (ServiceNotAvailableException e) {
			e.printStackTrace();
		}
		tv.setText(result);
	}
}
