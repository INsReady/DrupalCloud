package test.demo;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.insready.drupalcloud.JSONServerClient;

public class Test extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView tv = (TextView) findViewById(R.id.text);
		JSONServerClient sandbox = new JSONServerClient(this,
				getString(R.string.sharedpreferences_name),
				getString(R.string.SERVER), getString(R.string.API_KEY),
				getString(R.string.DOMAIN), getString(R.string.ALGORITHM), Long
						.parseLong(getString(R.string.SESSION_LIFETIME)));
		BasicNameValuePair[] bnvp = new BasicNameValuePair[1];
		bnvp[0] = new BasicNameValuePair("nid", "43672");
		String result = sandbox.call("node.get", bnvp);
		tv.setText(result);
	}
}