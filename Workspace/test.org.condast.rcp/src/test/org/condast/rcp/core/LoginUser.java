package test.org.condast.rcp.core;

import java.util.Date;

import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public class LoginUser implements ILoginUser{

	public LoginUser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getCreateDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreateDate(Date time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getUpdateDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdateDate(Date time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(ILoginUser o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSecurity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public LatLng getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocation(double latitude, double longitude) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCorrect(long userId, String token) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAdmin(String userName, long token) {
		// TODO Auto-generated method stub
		return false;
	}

}
