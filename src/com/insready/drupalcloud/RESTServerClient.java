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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class RESTServerClient implements Client {
	public HttpGet mSERVERGET;
	public HttpPost mSERVERPOST;
	public String url;
	public static String mDOMAIN;
	private HttpClient mClient = new DefaultHttpClient();
	private List<NameValuePair> mPairs = new ArrayList<NameValuePair>(15);
	

	public RESTServerClient(String _server,	String _domain) {
		mDOMAIN = _domain;
		url = _server;
	}
	
	public String call(String method, BasicNameValuePair[] parameters)
			throws ServiceNotAvailableException {

		try {
			mPairs.add(new BasicNameValuePair("domain_name",
					RESTServerClient.mDOMAIN));
			mPairs.add(new BasicNameValuePair("method", method));
			for (int i = 0; i < parameters.length; i++) {
				mPairs.add(parameters[i]);
			}
			mSERVERPOST.setEntity(new UrlEncodedFormEntity(mPairs));
			HttpResponse response = mClient.execute(mSERVERPOST);
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

		}  catch (UnsupportedEncodingException e) {
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
	
	public String callGET(String url) throws ServiceNotAvailableException {
   	    mSERVERGET = new HttpGet(url);
        
		try {
			HttpResponse response = mClient.execute(mSERVERGET);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String result = br.readLine();
//		JSONObject jso;
/*			jso = new JSONObject(result);
			String error = jso.getString("Null");
			if (error == "NULL") {
				String errorMsg = "Node does not exist";
				throw new ServiceNotAvailableException(this, errorMsg);
			}*/
			return result;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} /*catch (JSONException e) {
			e.printStackTrace();
			throw new ServiceNotAvailableException("Remote server is not available");
		}*/
		return null;
	}
	
	@Override
	public String nodeGet(int nid, String fields) throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		url = url + "node" + "/" + nid;
		String result = callGET(url);
		return result;
	}
	
	@Override
	public int commentSave(String comment) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String commentLoadNodeComments(int nid, int count, int start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String commentLoad(int cid) throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean flagFlag(String flagName, int contentId, int uid,
			boolean action, boolean skipPermissionCheck) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean flagIsFlagged(String flagName, int contentId, int uid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String userLogin(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String userLogout(String sessionID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String viewsGet(String view_name, String display_id, String args,
			int offset, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

}
