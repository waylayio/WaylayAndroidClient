package waylay.client.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import waylay.client.data.MachineInfo;
import waylay.client.data.UserInfo;


public class UserFactory {
	
	private static final HashSet<UserInfo> userSet = new HashSet<UserInfo>();
	
	public static ArrayList<UserInfo> getUsers() {
		return new ArrayList<UserInfo>(userSet);
	}
	
	public static void addUser(UserInfo userInfo){
		userSet.add(userInfo);
	}
	
	public static void removeUser(UserInfo userInfo){
		userSet.remove(userInfo);
	}
	
	public static boolean existConnection(String name, MachineInfo machine){
		for(UserInfo info: getUsers()){
			if(info.getName().equals(name) && info.getMachine()!= null && info.getMachine().equals(machine)){
				return true;
			}
		}
		return false;
	}
	
	

}
