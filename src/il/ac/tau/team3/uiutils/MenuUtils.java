package il.ac.tau.team3.uiutils;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.Pray;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.shareaprayer.FindPrayer;
import il.ac.tau.team3.shareaprayer.ServiceConnector;
import il.ac.tau.team3.shareaprayer.ServiceNotConnected;
import il.ac.tau.team3.spcomm.ICommHandler;
import il.ac.tau.team3.spcomm.SPComm;

public class MenuUtils {
	

	
	public static List<String> getOwnerPlaces(GeneralUser user){
		SPComm comm = new SPComm();
		final List<String> returnVal = new ArrayList<String>();
		comm.requestGetOwnerPlaces(user.getId(),new ICommHandler<GeneralPlace[]> (){
			
			public void onRecv(GeneralPlace[] places) {
				for (GeneralPlace place : places){
					if(place.getAddress() != null){
						returnVal.add(place.getAddress());
					}
					
				}
				
			}

			public void onTimeout(GeneralPlace[] Obj) {
				// TODO Auto-generated method stub
				
			}

			public void onError(GeneralPlace[] Obj) {
				// TODO Auto-generated method stub
				
			}
			
		});
		return returnVal;
	}
	
	public static List<String> getJoinerPlaces(GeneralUser user){
		SPComm comm = new SPComm();
		final List<String> returnVal = new ArrayList<String>();
		comm.requestGetJoinerPlaces(user.getId(),new ICommHandler<GeneralPlace[]> (){
			
			public void onRecv(GeneralPlace[] places) {
				for (GeneralPlace place : places){
					if(place.getAddress() != null){
						returnVal.add(place.getAddress());
					}
					
				}
				
			}

			public void onTimeout(GeneralPlace[] Obj) {
				// TODO Auto-generated method stub
				
			}

			public void onError(GeneralPlace[] Obj) {
				// TODO Auto-generated method stub
				
			}
			
		});
		return returnVal;
	}
	
	public void UpdateServerWithNewNames(final GeneralUser user, final String newFirstName, final String newLastName){
		user.setFirstName(newFirstName);
		user.setLastName(newLastName);
		SPComm comm = new SPComm();
		
		
		comm.updateUserByName(user, new ICommHandler<Long>(){

			public void onRecv(Long Obj) {
				if (null != Obj && -1 != Obj)	{
					ServiceConnector  svcGetter = new ServiceConnector();
					try {
						svcGetter.getService().setNames(new String[]{newFirstName, newLastName});
					} catch (ServiceNotConnected e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}

			public void onTimeout(Long Obj) {
				// TODO Auto-generated method stub
				
			}

			public void onError(Long Obj) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}

	protected SharedPreferences getSharedPreferences(String string, int i) {
		return getSharedPreferences(string, i);
	}
	
	
}
