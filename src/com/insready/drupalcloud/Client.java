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
	 */
	int commentSave(String comment);
	
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
	 */
	String commentLoadNodeComments(int nid, int count, int start);

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
	 */
	boolean flagFlag(String flag_name, int content_id, int uid, boolean action,
			boolean skip_permission_check);

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
	 */
	boolean flagIsFlagged(String flag_name, int content_id, int uid);

	/**
	 * Returns a node data.
	 * 
	 * @param nid
	 *            A node ID.
	 * @param fields
	 *            A list of fields to return.
	 * @return
	 */
	String nodeGet(int nid, String fields);

	/**
	 * Logs in a user.
	 * 
	 * @param username
	 *            a valid username.
	 * @param password
	 *            a valid password.
	 * @return
	 */
	Boolean userLogin(String username, String password);

	/**
	 * Logs out a user.
	 * 
	 * @return
	 */
	Boolean userLogout();

	/**
	 * Retrieves a view defined in views.module.
	 * 
	 * @param view_name
	 * @param args
	 * @return
	 */
	String viewsGet(String view_name, String args);
}
