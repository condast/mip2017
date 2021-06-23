package test.org.condast.rcp.store;

import org.condast.commons.authentication.session.DefaultSessionStore;

public class SessionStore extends DefaultSessionStore{

	public static final String S_ARNAC_ID = "org.satr.arnac";

	public static final String S_ERR_NO_STORE_FOUND = "The session store is not active";
	
	private boolean useHome;
	
	public SessionStore() {
		super( S_ARNAC_ID);
		this.useHome = false;
	}

	public void clear() {
		this.useHome = false;
		super.clear();
	}

	public boolean useHome() {
		return useHome;
	}

	public void setUseHome(boolean useHome) {
		this.useHome = useHome;
	}
}