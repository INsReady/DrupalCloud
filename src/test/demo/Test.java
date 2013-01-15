package test.demo;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.widget.TextView;

import com.insready.drupalcloud.RESTServerClient;
import com.insready.drupalcloud.ServiceNotAvailableException;

@SuppressLint("NewApi")
public class Test extends Activity {
	TextView mText = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mText = (TextView) findViewById(R.id.text);

		// Create a sandbox connection setup from value.xml
		RESTServerClient sandbox = new RESTServerClient(this,
				getString(R.string.sharedpreferences_name),
				getString(R.string.SERVER), getString(R.string.ENDPOINT),
				Long.valueOf(getString(R.string.SESSION_LIFETIME)));

		new ServiceWrapper().execute(sandbox);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private class ServiceWrapper extends
			AsyncTask<RESTServerClient, Void, String> {

		@Override
		protected String doInBackground(RESTServerClient... rsc) {
			String result = "";
			try {
				// load node 1
				JsonReader jsr = rsc[0].nodeGet(1, "");
				jsr.beginObject();
				while (jsr.hasNext()) {
					String name = jsr.nextName();
					// read the title of the node
					if (name.equals("title")) {
						result = jsr.nextString();
					} else {
						jsr.skipValue();
					}
				}

			} catch (ServiceNotAvailableException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String result) {
			mText.setText(result);
		}
	}
}
