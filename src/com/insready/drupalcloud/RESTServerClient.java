package com.insready.drupalcloud;

public class RESTServerClient implements Client {

	@Override
	public int commentSave(String comment) {
		// TODO Auto-generated method stub
		return 0;
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

	@Override
	public String nodeGet(int nid, String fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean userLogin(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean userLogout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String viewsGet(String viewName, String args) {
		// TODO Auto-generated method stub
		return null;
	}

}
