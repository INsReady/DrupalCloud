package com.insready.drupalcloud;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageLoader {
	final String mDOMAIN;

	public ImageLoader(String _domain) {
		mDOMAIN = _domain;
	}

	/**
	 * 
	 * @param _uri
	 *            : it should look like "public://IMG_1706.jpg"
	 * @param style
	 *            : Drupal image style
	 * @return
	 * @throws IOException
	 */
	public Bitmap download(String _uri, String style) throws IOException {

		_uri = mDOMAIN + "sites/default/files/styles/" + style + "/public/"
				+ URLEncoder.encode(_uri.substring(9), "UTF-8");

		URL url;
		url = new URL(_uri);

		InputStream is = null;
		is = url.openStream();

		return BitmapFactory.decodeStream(is);
	}

	public Bitmap download(String _uri, String _filesystem, String style)
			throws IOException {

		_uri = mDOMAIN + _filesystem + "/styles/" + style + "/public/"
				+ URLEncoder.encode(_uri.substring(9), "UTF-8");

		URL url;
		url = new URL(_uri);

		InputStream is = null;
		is = url.openStream();

		return BitmapFactory.decodeStream(is);
	}
}
