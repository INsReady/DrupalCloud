package com.insready.drupalcloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;

@SuppressLint("NewApi")
public class RESTServerClient {
	public HttpGet mSERVERGET;
	public HttpPost mSERVERPOST;
	public String mENDPOIN;
	public static String mDOMAIN;
	private HttpClient mClient = new DefaultHttpClient();
	private List<NameValuePair> mPairs = new ArrayList<NameValuePair>(15);
	private Context mCtx;
	private final String mPREFS_AUTH;
	public static Long mSESSION_LIFETIME;

	public RESTServerClient(Context _ctx, String _prefs_auth, String _server,
			String _domain, Long _session_lifetime) {
		mPREFS_AUTH = _prefs_auth;
		mDOMAIN = _domain;
		mENDPOIN = _server;
		mCtx = _ctx;
		mSESSION_LIFETIME = _session_lifetime;
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

	public String call(String url, BasicNameValuePair[] parameters)
			throws ServiceNotAvailableException {
		mSERVERPOST = new HttpPost(url);
		String sessid = this.getSessionID();
		mPairs.clear();
		try {
			final Long timestamp = new Date().getTime() / 100;
			final String time = timestamp.toString();
			mPairs.add(new BasicNameValuePair("domain_time_stamp", time));
			mPairs.add(new BasicNameValuePair("sessid", sessid));
			for (int i = 0; i < parameters.length; i++) {
				mPairs.add(parameters[i]);
			}
			mSERVERPOST.setEntity(new UrlEncodedFormEntity(mPairs));
			HttpResponse response = mClient.execute(mSERVERPOST);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String result = br.readLine();
			/*
			 * JSONObject jso; jso = new JSONObject(result); boolean error =
			 * jso.getBoolean("#error"); if (error) { String errorMsg =
			 * jso.getString("#data"); throw new
			 * ServiceNotAvailableException(this, errorMsg); }
			 */
			return result;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStreamReader callGet(String url)
			throws ServiceNotAvailableException {
		mSERVERGET = new HttpGet(url);

		try {
			HttpResponse response = mClient.execute(mSERVERGET);
			InputStream is = response.getEntity().getContent();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			return isr;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void systemConnect() throws ServiceNotAvailableException {
		// Cloud server hand shake
		String uri = mENDPOIN + "system/connect";
		mSERVERPOST = new HttpPost(uri);
		try {
			HttpResponse response = mClient.execute(mSERVERPOST);
			InputStream result = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(result));
			String tmp = br.readLine();
			if (tmp == null) {
				// TODO throw new ServiceNotAvailableException(this,
				// "Invalid method");
			}
			JSONObject jso = new JSONObject(tmp);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JsonReader nodeGet(int nid, String fields)
			throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		String uri = mENDPOIN + "node/" + nid;
		JsonReader jsr = new JsonReader(callGet(uri));
		return jsr;
	}

	public int commentSave(String comment) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String commentLoadNodeComments(int nid, int count, int start) {
		// TODO Auto-generated method stub
		return null;
	}

	public String commentLoad(int cid) throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean flagFlag(String flagName, int contentId, int uid,
			boolean action, boolean skipPermissionCheck)
			throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		String uri = mENDPOIN + "flag/flag";
		BasicNameValuePair[] parameters = new BasicNameValuePair[5];
		parameters[0] = new BasicNameValuePair("flag_name", flagName);
		parameters[1] = new BasicNameValuePair("content_id",
				String.valueOf(contentId));
		parameters[2] = new BasicNameValuePair("uid", String.valueOf(uid));
		String actionName = (action) ? "flag" : "unflag";
		parameters[3] = new BasicNameValuePair("action", actionName);
		String skipPermissionCheckName = (skipPermissionCheck) ? "TRUE"
				: "FALSE";
		parameters[4] = new BasicNameValuePair("skip_permission_check",
				skipPermissionCheckName);
		String result = call(uri, parameters);
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

	public boolean flagIsFlagged(String flagName, int contentId, int uid)
			throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		String uri = mENDPOIN + "flag/flag_isflaged";
		BasicNameValuePair[] parameters = new BasicNameValuePair[3];
		parameters[0] = new BasicNameValuePair("flag_name", flagName);
		parameters[1] = new BasicNameValuePair("content_id",
				String.valueOf(contentId));
		parameters[2] = new BasicNameValuePair("uid", String.valueOf(uid));
		String result = call(uri, parameters);

		JSONObject jso;
		try {
			jso = new JSONObject(result);
			boolean flag = jso.getBoolean("");
			return flag;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String userLogin(String username, String password)
			throws ServiceNotAvailableException {
		// TODO Auto-generated method stub<?xml version="1.0" encoding="utf-8"?>
		String uri = mENDPOIN + "user/login";
		BasicNameValuePair[] parameters = new BasicNameValuePair[2];
		parameters[0] = new BasicNameValuePair("username", username);
		parameters[1] = new BasicNameValuePair("password", password);
		return call(uri, parameters);
	}

	public String userLogout(String sessionID)
			throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		BasicNameValuePair[] parameters = new BasicNameValuePair[0];
		// parameters[0] = new BasicNameValuePair("sessid", sessionID);
		String uri = mENDPOIN + "user/logout";
		return call(uri, parameters);
	}

	public String viewsGet(String view_name, String display_id, String args,
			int offset, int limit) throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		String uri = mENDPOIN + "views/" + view_name + "?";
		String[] ary = args.split("/");
		int arylength = ary.length;
		String arg = "args[0]=" + ary[0] + "&";
		for (int i = 1; i < arylength; i++) {
			arg += "args[" + i + "]=" + ary[i] + "&";
		}
		uri = uri + arg;
		// TODO String result = callGet(uri);
		return null;
	}

}
