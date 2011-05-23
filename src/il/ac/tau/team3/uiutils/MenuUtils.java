package il.ac.tau.team3.uiutils;

import java.util.ArrayList;
import java.util.List;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.Pray;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.spcomm.ICommHandler;
import il.ac.tau.team3.spcomm.SPComm;

public class MenuUtils {
	
	// this boolean indicates rather the user want to show max prayers (true) or min prayers (flase)
	private static boolean showMax = true ;

	public static boolean isShowMax() {
		return showMax;
	}

	public static void setShowMax(boolean toShowMax) {
		showMax = toShowMax;
	}
	
	
	public static int chooseMaxOrMin(GeneralPlace place){
		if(showMax){
			return determinMax(place);
		}else{
			return determinMin(place);
		}
	}
	
	static int determinMax(GeneralPlace place){
		int max = 0;
		
		try	{
			for (Pray p : place.getPraysOfTheDay())	{
				if (max < p.numberOfJoiners())	{
					max = p.numberOfJoiners();
				}
			}
		} catch (NullPointerException e)	{
			// no prays for place
		}
		
		
		return max;
	}
	
	static int determinMin(GeneralPlace place){
		int min = (int) SPUtils.INFINITY;
		
		try	{
			for (Pray p : place.getPraysOfTheDay())	{
				if (min > p.numberOfJoiners())	{
					min = p.numberOfJoiners();
				}
			}
		} catch (NullPointerException e)	{
			// no prays for place
		}
		
		
		return min;
	}
	
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
	
	
}
