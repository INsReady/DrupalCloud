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

/**
 * Services Server output formats that are currently supported:
 * 
 * @author Jingsheng Wang
 */
public class JSONServerClient implements Client {
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

	private String getSessionID() throws ServiceNotAvailableException {
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
	public String call(String method, BasicNameValuePair[] parameters)
			throws ServiceNotAvailableException {
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
			String message = time + ";" + JSONServerClient.mDOMAIN + ";"
					+ nonce + ";" + method;
			hmac.update(message.getBytes());
			String hmac_value = new String(Hex.encodeHex(hmac.doFinal()));
			mPairs.add(new BasicNameValuePair("hash", hmac_value));
			mPairs.add(new BasicNameValuePair("domain_name",
					JSONServerClient.mDOMAIN));
			mPairs.add(new BasicNameValuePair("domain_time_stamp", time));
			mPairs.add(new BasicNameValuePair("nonce", nonce));
			mPairs.add(new BasicNameValuePair("method", method));
			mPairs.add(new BasicNameValuePair("api_key",
					JSONServerClient.mAPI_KEY));
			mPairs.add(new BasicNameValuePair("sessid", sessid));
			for (int i = 0; i < parameters.length; i++) {
				mPairs.add(parameters[i]);
			}
			mSERVER.setEntity(new UrlEncodedFormEntity(mPairs));
			HttpResponse response = mClient.execute(mSERVER);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String result = br.readLine();
			JSONObject jso;
			jso = new JSONObject(result);
			boolean error = jso.getBoolean("#error");
			if (error) {
				String errorMsg = jso.getString("#data");
				throw new ServiceNotAvailableException(this, errorMsg);
			}
			return result;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServiceNotAvailableException("Remote server is not available");
		}
		return null;
	}

	/**
	 * system.connect request for Key Auth
	 */
	private void systemConnect() throws ServiceNotAvailableException {
		// Cloud server hand shake
		mPairs.add(new BasicNameValuePair("method", "system.connect"));
		try {
			mSERVER.setEntity(new UrlEncodedFormEntity(mPairs));
			HttpResponse response = mClient.execute(mSERVER);
			InputStream result = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(result));
			JSONObject jso = new JSONObject(br.readLine());
			boolean error = jso.getBoolean("#error");
			String data = jso.getString("#data");
			if (error) {
				throw new ServiceNotAvailableException(this, data);
			}

			jso = new JSONObject(data);
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
	public String userLogin(String username, String password)
			throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[2];
		parameters[0] = new BasicNameValuePair("username", username);
		parameters[1] = new BasicNameValuePair("password", password);
		return call("user.login", parameters);
	}

	@Override
	public String userLogout(String sessionID)
			throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[1];
		parameters[0] = new BasicNameValuePair("sessid", sessionID);
		return call("user.logout", parameters);
	}

	@Override
	public String nodeGet(int nid, String fields)
			throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[2];
		parameters[0] = new BasicNameValuePair("nid", String.valueOf(nid));
		parameters[1] = new BasicNameValuePair("fields", fields);
		String result = call("node.get", parameters);
		/*
		 * try { JSONObject jso = new JSONObject(temp); jso = new
		 * JSONObject(jso.getString("#data")); JSONArray nameArray =
		 * jso.names(); JSONArray valArray = jso.toJSONArray(nameArray); for
		 * (int i=0;i<valArray.length();i++){
		 * Log.i("Testing","<jsonmae"+i+">\n"+
		 * nameArray.getString(i)+"\n</jsonname"+i+">\n"
		 * +"<jsonvalue"+i+">\n"+valArray.getString(i)+"\n</jsonvalue"+i+">"); }
		 * } catch (JSONException e) { e.printStackTrace(); }
		 */
		result = result.replaceAll("(\\\\r\\\\n|\\\\r)", "\\\\n");
		return result;
	}

	@Override
	public String viewsGet(String view_name, String display_id, String args,
			int offset, int limit) throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[5];
		parameters[0] = new BasicNameValuePair("view_name", view_name);
		parameters[1] = new BasicNameValuePair("args", args);
		parameters[2] = new BasicNameValuePair("display_id", display_id);
		parameters[3] = new BasicNameValuePair("offset", offset + "");
		parameters[4] = new BasicNameValuePair("limit", limit + "");
		return call("views.get", parameters);
	}

	@Override
	public int commentSave(String comment) throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[1];
		parameters[0] = new BasicNameValuePair("comment", comment);
		String result = call("comment.save", parameters);

		JSONObject jso;
		try {
			jso = new JSONObject(result);
			int cid = jso.getInt("#data");
			return cid;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public String commentLoadNodeComments(int nid, int count, int start)
			throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[3];
		parameters[0] = new BasicNameValuePair("nid", String.valueOf(nid));
		parameters[1] = new BasicNameValuePair("count", String.valueOf(count));
		parameters[2] = new BasicNameValuePair("start", String.valueOf(start));
		String result = call("comment.loadNodeComments", parameters);
		// Convert other line breaks to Unix line breaks
		result = result.replaceAll("(\\\\r\\\\n|\\\\r)", "\\\\n");
		return result;
	}
	
	@Override
	public String commentLoad(int cid) throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[1];
		parameters[0] = new BasicNameValuePair("cid", String.valueOf(cid));
		String result = call("comment.load", parameters);
		return result;
	}

	@Override
	public boolean flagFlag(String flagName, int contentId, int uid,
			boolean action, boolean skipPermissionCheck)
			throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[5];
		parameters[0] = new BasicNameValuePair("flag_name", flagName);
		parameters[1] = new BasicNameValuePair("content_id", String
				.valueOf(contentId));
		parameters[2] = new BasicNameValuePair("uid", String.valueOf(uid));
		String actionName = (action) ? "flag" : "unflag";
		parameters[3] = new BasicNameValuePair("action", actionName);
		String skipPermissionCheckName = (skipPermissionCheck) ? "TRUE"
				: "FALSE";
		parameters[4] = new BasicNameValuePair("skip_permission_check",
				skipPermissionCheckName);
		String result = call("flag.flag", parameters);
		JSONObject jso;
		try {
			jso = new JSONObject(result);
			boolean flag = jso.getBoolean("#data");
			return flag;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean flagIsFlagged(String flagName, int contentId, int uid)
			throws ServiceNotAvailableException {
		BasicNameValuePair[] parameters = new BasicNameValuePair[3];
		parameters[0] = new BasicNameValuePair("flag_name", flagName);
		parameters[1] = new BasicNameValuePair("content_id", String
				.valueOf(contentId));
		parameters[2] = new BasicNameValuePair("uid", String.valueOf(uid));
		String result = call("flag.is_flagged", parameters);

		JSONObject jso;
		try {
			jso = new JSONObject(result);
			boolean flag = jso.getBoolean("#data");
			return flag;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

}
