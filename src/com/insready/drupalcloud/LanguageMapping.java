package com.insready.drupalcloud;

/**
 * 
 * Drupal uses RFC 4646 as the language tags standards, {@link https
 * ://www.drupal.org/node/923304#comment-3496864} while Android uses ISO 639-1
 * and ISO 3166-1 as the standard {@link http
 * ://developer.android.com/reference/java/util/Locale.html} Therefore, there
 * exists a need for such mapping.
 */
public final class LanguageMapping {

	public final static String[] ANDROIDLOCALES = { "en_CA", "fr_CA", "zh_CN",
			"zh", "en", "fr_FR", "fr", "de", "de_DE", "it", "it_IT", "ja_JP",
			"ja", "ko_KR", "ko", "zh_CN", "zh_TW", "en_GB", "en_US" };

	/**
	 * DrupalLocales try to match AndroidLocales
	 */
	public final static String[] DRUPALLOCALES = { "en", "fr", "zh-hans",
			"zh-hans", "en", "fr", "fr", "de", "de", "it", "it", "ja", "ja",
			"ko", "zh-hans", "zh-hant", "en-gb", "en" };

	public static int findAndroidLocaleIndex(String locale_code) {
		int size = ANDROIDLOCALES.length;
		for (int i = 0; i < size; i++) {
			if (ANDROIDLOCALES[i].equals(locale_code))
				return i;
		}
		return -1;
	}
}
