
package waylay.client.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import waylay.client.data.MachineInfo;


public class MachineFactory {
	
	private static final Set<MachineInfo> machineList = new HashSet<MachineInfo>();
	
	public static ArrayList<MachineInfo> getMachines() {
		return new ArrayList<MachineInfo>(machineList);
	}
	
	public static void addMachine(MachineInfo machineInfo){
		machineList.add(machineInfo);
	}
	
	public static void removeMachine(MachineInfo machineInfo){
		machineList.remove(machineInfo);
	}

}
