/**
 * 
 */
package com.insready.drupalcloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Services Server output formats that are currently supported:
 * <ul>
 * <li>JSON</li>
 * <li>XMLRPC (in the future)</li>
 * <li>JSON-RPC (when Services 3.x releases)</li>
 * </ul>
 * 
 * @author Jingsheng Wang A library on Android to communicate with Drupal
 */
public class JSONServerClient implements Client{
	public HttpPost mSERVER;
	public static String mAPI_KEY;
	public static String mDOMAIN;
	public static String mALGORITHM;
	public static Long mSESSION_LIFETIME;
	private HttpClient mClient = new DefaultHttpClient();
	private List<NameValuePair> mPairs = new ArrayList<NameValuePair>(15);
	private Context mCtx;
	private final String mPREFS_AUTH;

	/**
	 * 
	 * @param _ctx
	 *            Context
	 * @param _prefs_auth
	 *            Preference storage
	 * @param _server
	 *            Server address
	 * @param _api_key
	 *            API_Key
	 * @param _domain
	 *            Domain name
	 * @param _algorithm
	 *            Encrypition algorithm
	 * @param _session_lifetime
	 *            Session lifetime
	 */
	public JSONServerClient(Context _ctx, String _prefs_auth, String _server,
			String _api_key, String _domain, String _algorithm,
			Long _session_lifetime) {
		mPREFS_AUTH = _prefs_auth;
		mSERVER = new HttpPost(_server);
		mSERVER.setHeader("User-Agent", "DrupalCloud-1.x");
		mAPI_KEY = _api_key;
		mDOMAIN = _domain;
		mALGORITHM = _algorithm;
		mSESSION_LIFETIME = _session_lifetime;
		mCtx = _ctx;
	}

	private String getSessionID() {
		SharedPreferences auth = mCtx.getSharedPreferences(mPREFS_AUTH, 0);
		Long timestamp = auth.getLong("sessionid_timestamp", 0);
		Long currenttime = new Date().getTime() / 100;
		String sessionid = auth.getString("sessionid", null);
		if (sessionid == null || (currenttime - timestamp) >= mSESSION_LIFETIME) {
			systemConnect();
			return getSessionID();
		} else
			return sessionid;
	}

	/**
	 * Generic request
	 * 
	 * @param method
	 *            Request name
	 * @param parameters
	 *            Parameters
	 * @return result string
	 */
	public String call(String method, BasicNameValuePair[] parameters) {
		String sessid = this.getSessionID();
		mPairs.clear();
		String nonce = Integer.toString(new Random().nextInt());
		Mac hmac;

		try {
			hmac = Mac.getInstance(JSONServerClient.mALGORITHM);
			final Long timestamp = new Date().getTime() / 100;
			final String time = timestamp.toString();
			hmac.init(new SecretKeySpec(JSONServerClient.mAPI_KEY.getBytes(),
					JSONServerClient.mALGORITHM));
			String message = time + ";" + JSONServerClient.mDOMAIN + ";" + nonce + ";"
					+ method;
			hmac.update(message.getBytes());
			String hmac_value = new String(Hex.encodeHex(hmac.doFinal()));
			mPairs.add(new BasicNameValuePair("hash", hmac_value));
			mPairs.add(new BasicNameValuePair("domain_name", JSONServerClient.mDOMAIN));
			mPairs.add(new BasicNameValuePair("domain_time_stamp", time));
			mPairs.add(new BasicNameValuePair("nonce", nonce));
			mPairs.add(new BasicNameValuePair("method", method));
			mPairs.add(new BasicNameValuePair("api_key", JSONServerClient.mAPI_KEY));
			mPairs.add(new BasicNameValuePair("sessid", sessid));
			for (int i = 0; i < parameters.length; i++) {
				mPairs.add(parameters[i]);
			}
			mSERVER.setEntity(new UrlEncodedFormEntity(mPairs));
			HttpResponse response = mClient.execute(mSERVER);
			InputStream result = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(result));
			return br.readLine();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Toast
					.makeText(
							mCtx,
							"NoSuchAlgorithmException: The configuration file is corrupted.",
							Toast.LENGTH_LONG).show();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			Toast
					.makeText(
							mCtx,
							"InvalidKeyException: The configuration file is corrupted.",
							Toast.LENGTH_LONG).show();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Toast.makeText(mCtx, "UnsupportedEncodingException",
					Toast.LENGTH_LONG).show();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Toast.makeText(mCtx, "ClientProtocolException", Toast.LENGTH_LONG)
					.show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mCtx, "IOException", Toast.LENGTH_LONG).show();
		}
		return null;
	}

	/**
	 * system.connect request for Key Auth
	 */
	private void systemConnect() {
		// Cloud server hand shake
		mPairs.add(new BasicNameValuePair("method", "system.connect"));
		try {
			mSERVER.setEntity(new UrlEncodedFormEntity(mPairs));
			HttpResponse response = mClient.execute(mSERVER);
			InputStream result = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(result));
			JSONObject jso = new JSONObject(br.readLine());
			jso = new JSONObject(jso.getString("#data"));

			// Save the sessionid to storage
			SharedPreferences auth = mCtx.getSharedPreferences(mPREFS_AUTH, 0);
			SharedPreferences.Editor editor = auth.edit();
			editor.putString("sessionid", jso.getString("sessid"));
			editor.putLong("sessionid_timestamp", new Date().getTime() / 100);
			editor.commit();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean userLogin(String username, String password) {
		BasicNameValuePair[] parameters = new BasicNameValuePair[2];
		parameters[0] = new BasicNameValuePair("username", username);
		parameters[1] = new BasicNameValuePair("password", password);

		String result = call("user.login", parameters);
		JSONObject jso;
		try {
			jso = new JSONObject(result);
			jso = new JSONObject(jso.getString("#data"));

			// Save user data to storage
			SharedPreferences auth = mCtx.getSharedPreferences(mPREFS_AUTH, 0);
			SharedPreferences.Editor editor = auth.edit();
			editor.putString("sessionid", jso.getString("sessid"));
			editor.putLong("sessionid_timestamp", new Date().getTime() / 100);
			jso = new JSONObject(jso.getString("user"));
			editor.putInt("uid", jso.getInt("uid"));
			editor.putString("username", jso.getString("name"));
			editor.putString("mail", jso.getString("mail"));
			editor.commit();
			return true;
		} catch (JSONException e) {

			try {
				jso = new JSONObject(result);
				jso = new JSONObject(jso.getString("#data"));
				return jso.getBoolean("#error");
			} catch (JSONException e1) {
				e1.printStackTrace();
				Toast
						.makeText(
								mCtx,
								"JSONException Error: The connection to the remote server is corrupted. Please try it later. Make sure you have the latest client application installed.",
								Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}

	@Override
	public Boolean userLogout() {
		SharedPreferences auth = mCtx.getSharedPreferences(mPREFS_AUTH, 0);
		SharedPreferences.Editor editor = auth.edit();

		BasicNameValuePair[] parameters = new BasicNameValuePair[1];
		String sessionid = auth.getString("sessionid", null);
		String result = null;
		if (sessionid != null) {
			parameters[0] = new BasicNameValuePair("sessid", sessionid);
			result = call("user.logout", parameters);
			JSONObject jso;
			try {
				jso = new JSONObject(result);
				if (jso.getBoolean("#data")) {
					editor.remove("username");
					editor.remove("uid");
					editor.remove("mail");
					editor.remove("sessionid");
					editor.remove("sessionid_timestamp");
					editor.commit();
					return true;
				} else
					return false;
			} catch (JSONException e) {
				e.printStackTrace();
				Toast
						.makeText(
								mCtx,
								"JSONException Error: The connection to the remote server is corrupted. Please try it later. Make sure you have the latest client application installed.",
								Toast.LENGTH_LONG).show();
			}
		}
		return false;

	}

	@Override
	public String nodeGet(int nid, String fields) {
		BasicNameValuePair[] parameters = new BasicNameValuePair[2];
		parameters[0] = new BasicNameValuePair("nid", String.valueOf(nid));
		parameters[1] = new BasicNameValuePair("fields", fields);
		String result = call("node.get", parameters);
		/*try {
			JSONObject jso = new JSONObject(temp);
			jso = new JSONObject(jso.getString("#data"));
			JSONArray nameArray = jso.names();
			JSONArray valArray = jso.toJSONArray(nameArray);
			for (int i=0;i<valArray.length();i++){
				Log.i("Testing","<jsonmae"+i+">\n"+nameArray.getString(i)+"\n</jsonname"+i+">\n"
						+"<jsonvalue"+i+">\n"+valArray.getString(i)+"\n</jsonvalue"+i+">");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		result=result.replaceAll("(\\\\r\\\\n|\\\\r)", "\\\\n");
		return result;
	}

	@Override
	public String viewsGet(String view_name, String args) {
		BasicNameValuePair[] parameters = new BasicNameValuePair[2];
		parameters[0] = new BasicNameValuePair("view_name", view_name);
		parameters[1] = new BasicNameValuePair("args", args);
		return call("views.get", parameters);
	}

	@Override
	public int commentSave(String comment) {
		BasicNameValuePair[] parameters = new BasicNameValuePair[1];
		parameters[0] = new BasicNameValuePair("comment", comment);
		return Integer.valueOf(call("comment.save", parameters));
	}

	
	@Override
	public String commentLoadNodeComments(int nid, int count, int start) {
		BasicNameValuePair[] parameters = new BasicNameValuePair[3];
		parameters[0] = new BasicNameValuePair("nid", String.valueOf(nid));
		parameters[1] = new BasicNameValuePair("count", String.valueOf(count));
		parameters[2] = new BasicNameValuePair("start", String.valueOf(start));
		String result = call("comment.loadNodeComments", parameters);
		// Convert other line breaks to Unix line breaks
		result=result.replaceAll("(\\\\r\\\\n|\\\\r)", "\\\\n");
		return result;
	}

	@Override
	public String flagFlag(String flagName, int contentId, int uid,
			boolean action, boolean skipPermissionCheck) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean flagIsFlagged(String flagName, int contentId, int uid) {
		// TODO Auto-generated method stub
		return false;
	}

}
