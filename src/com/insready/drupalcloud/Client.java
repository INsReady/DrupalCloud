package com.insready.drupalcloud;

/**
 * @author Jingsheng Wang (skyred at gmail dot com)
 * 
 */
public interface Client {
	/**
	 * This method adds a comment to a node and returns a comment id. If the
	 * comment object contains a numeric "cid", then the comment will be
	 * updated. Required fields in the comment object: nid, comment. Optional
	 * fields: cid (comment id), pid (parent comment), subject, mail, homepage
	 * 
	 * @param comment
	 *            A comment object.
	 * @return
	 * @throws ServiceNotAvailableException 
	 */
	int commentSave(String comment) throws ServiceNotAvailableException;
	
	/**
	 * This method returns all or part of the comments on a give node.
	 * 
	 * @param nid
	 *            A node id.
	 * @param count
	 *            Number of comments to load.
	 * @param start
	 *            If count is set to non-zero value, you can pass also
	 *            non-negative value for start. For example to get comments from
	 *            5 to 15, pass count=10 and start=4.
	 * @return
	 * @throws ServiceNotAvailableException 
	 */
	String commentLoadNodeComments(int nid, int count, int start) throws ServiceNotAvailableException;
	
	/**
	 * This method returns a single comment object
	 * 
	 * @param cid
	 *            a comment id
	 * @return
	 * @throws ServiceNotAvailableException
	 */
	String commentLoad(int cid) throws ServiceNotAvailableException;

	/**
	 * Flags (or unflags) a content.
	 * 
	 * @param flag_name
	 *            The name of the flag.
	 * @param content_id
	 *            The content ID.
	 * @param uid
	 *            The user ID for which to flag.
	 * @param action
	 *            Optional; The action to perform, default is "flag". Should be
	 *            "flag" or "unflag"/
	 * @param skip_permission_check
	 *            Optional; Falg the content even if the user does not have
	 *            permission to do so. FALSE by default
	 * @return
	 * @throws ServiceNotAvailableException 
	 */
	boolean flagFlag(String flag_name, int content_id, int uid, boolean action,
			boolean skip_permission_check) throws ServiceNotAvailableException;

	/**
	 * Check if a content was flagged by a user.
	 * 
	 * @param flag_name
	 *            The name of the flag.
	 * @param content_id
	 *            the content ID.
	 * @param uid
	 *            Optional; The user ID that might have flagged the content.
	 * @return
	 * @throws ServiceNotAvailableException 
	 */
	boolean flagIsFlagged(String flag_name, int content_id, int uid) throws ServiceNotAvailableException;

	/**
	 * Returns a node data.
	 * 
	 * @param nid
	 *            A node ID.
	 * @param fields
	 *            A list of fields to return.
	 * @return
	 * @throws ServiceNotAvailableException 
	 */
	String nodeGet(int nid, String fields) throws ServiceNotAvailableException;

	/**
	 * Logs in a user.
	 * 
	 * @param username
	 *            a valid username.
	 * @param password
	 *            a valid password.
	 * @return
	 * @throws ServiceNotAvailableException 
	 */
	String userLogin(String username, String passwogrd) throws ServiceNotAvailableException;

	/**
	 * Logs out a user.
	 * 
	 * @return
	 * @throws ServiceNotAvailableException 
	 */
	String userLogout(String sessionID) throws ServiceNotAvailableException;

	/**
	 * Retrieves a view defined in views.module.
	 * 
	 * @param view_name
	 * @param args
	 * @return
	 * @throws ServiceNotAvailableException 
	 */
	String viewsGet(String view_name, String display_id, String args,
			int offset, int limit) throws ServiceNotAvailableException;
}
