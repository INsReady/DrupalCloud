/**
 * 
 */
package com.insready.drupalcloud;

/**
 * @author Jingsheng Wang
 * 
 */
public class ServiceNotAvailableException extends Exception {

	public ServiceNotAvailableException(String msg) {
		super(msg);
	}

	public ServiceNotAvailableException(Client client, String msg) {
		super(client.toString() + " " + msg);
	}

}
