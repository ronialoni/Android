package il.ac.tau.team3.shareaprayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;

public class FacebookConnector {
	private Facebook facebook = new Facebook("187659291286244");
	private boolean facebookConnected = false;
	private final String desc_footer = "<br>For futher details: download http://share-a-prayer.googlecode.com/files/ShareAPrayer.apk";
	
	private final String FACEBOOK_STARTUP_KEY = "FACEBOOK_STARTUP";
	private final String FACEBOOK_CONFIGURED_KEY = "FACEBOOK_CONFIGURED";
	private final String FACEBOOK_SHARE_KEY = "FACEBOOK_SHARE";
	
	private boolean facebook_connectStartup = false;
	private boolean facebook_share = false;
	private boolean facebook_configured = false;
	
	
	public boolean isFacebook_connectStartup() {
		return facebook_connectStartup;
	}
	
	public boolean isFacebook_configured() {
		return facebook_configured;
	}



	private FindPrayer activity;
	
	private SharedPreferences settings;
	
	public void setConnectOnStartup(boolean startup)	{
		facebook_connectStartup = startup;
		SharedPreferences.Editor edit = settings.edit();
		edit.putBoolean(FACEBOOK_STARTUP_KEY, facebook_connectStartup);
		edit.commit();
	}
	
	public void disconnect()	{
		try {
			facebook.logout(activity);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connect()	{
			facebook.authorize(activity, new String[]{"publish_stream"/*,"read_stream","offline_access"*/},0, new DialogListener() {
        	
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			public void onComplete(Bundle values) {
				// TODO Auto-generated method stub
				if(!facebook_configured){
					publishOnFacebook("Started using Share-A-Prayer",
							"Welcome to Share-A-Prayer. <br>" + "This application will help to find the closest minyan for the next pray.");
				}
				facebookConnected = true;
				facebook_configured = true;
				SharedPreferences.Editor edit = settings.edit();
				edit.putBoolean(FACEBOOK_CONFIGURED_KEY, facebook_configured);
				edit.commit();
				setConnectOnStartup(true);
				
				
				
				
				
			}

			public void onError(DialogError e) {
				// TODO Auto-generated method stub
				
			}

			public void onFacebookError(FacebookError e) {
				// TODO Auto-generated method stub
				
			}

        });

	}
	
	public void logout()	{
		try {
			facebook.logout(activity);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally	{
			facebookConnected = false;
		}
	}

    public FacebookConnector(FindPrayer a)	{
    	activity = a;
    	
    	settings = a.getSharedPreferences("ShareAPrayer", 0);
    	
    	facebook_connectStartup = settings.getBoolean(FACEBOOK_STARTUP_KEY, false);
    	facebook_configured = settings.getBoolean(FACEBOOK_CONFIGURED_KEY, false);
    	facebook_share = settings.getBoolean(FACEBOOK_SHARE_KEY, false);
    	
    	if (facebook_connectStartup)	{
    		connect();
    	}
    		
      }

	
	
	public void publishOnFacebook(final String headline, final String description)	{
		if ((!facebookConnected) || (!facebook_share) || (!facebook_configured))	{
			return;
		}
		
		AsyncFacebookRunner fbrunner = new AsyncFacebookRunner(facebook);
		Bundle params = new Bundle();
		params.putString("message", headline);
		params.putString("description", desc_footer);
		params.putString("picture", "http://share-a-prayer.appspot.com/logo.png");
		params.putString("icon", "http://share-a-prayer.appspot.com/logo.png");
		params.putString("application", "Share-A-Prayer");
		params.putString("link", "http://www.facebook.com/apps/application.php?id=187659291286244");
		
		params.putString("caption", description);
		params.putString("access_token", facebook.getAccessToken());
		
		fbrunner.request("me/feed", params, "POST", new RequestListener() {

			public void onComplete(String response, Object state) {
				Log.e("Share-A-Prayer", "Facebook post response: " + response);
				activity.getStatusBar().write("Facebook: Posted On Wall!", R.drawable.fb_icon, 2000);
				
			}

			public void onFacebookError(FacebookError e, Object state) {
				activity.getStatusBar().write("Facebook: Limit of posts reached", R.drawable.fb_icon, 2000);
				
			}

			public void onFileNotFoundException(
					FileNotFoundException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onMalformedURLException(
					MalformedURLException e, Object state) {
				// TODO Auto-generated method stub
				
			}
			
		}, null);
	};
	
    

    public void autherizeCallback(int requestCode, int resultCode, Intent data)	{
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public void setFacebook_share(boolean facebook_share) {
		this.facebook_share = facebook_share;
		SharedPreferences.Editor edit = settings.edit();
		edit.putBoolean(FACEBOOK_SHARE_KEY, facebook_share);
		edit.commit();
	}

	public boolean isFacebook_share() {
		return facebook_share;
	}
	
	
}
