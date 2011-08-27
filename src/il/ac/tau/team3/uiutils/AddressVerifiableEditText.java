package il.ac.tau.team3.uiutils;

import java.util.LinkedList;
import java.util.List;

import il.ac.tau.team3.addressQuery.MapsQueryLocation;
import il.ac.tau.team3.addressQuery.MapsQueryLonLat;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.shareaprayer.R;
import il.ac.tau.team3.spcomm.ACommHandler;
import il.ac.tau.team3.spcomm.SPComm;
import il.ac.tau.team3.uiutils.UIUtils;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class AddressVerifiableEditText extends EditText {

	private SPComm comm;
	private Activity activity;
	private List<IAddressVerify> handlers = new LinkedList<IAddressVerify>();
	private String lastText = "";
	private String lastVerifiedText = null;
	
	public void addHandler(IAddressVerify handler)	{
		handlers.add(handler);
	}
	
	public void removeHandler(IAddressVerify handler)	{
		handlers.remove(handler);
	}
	
	protected void handlersFoundAddress(final SPGeoPoint geopoint, final String address)	{
		for (final IAddressVerify handler : handlers)	{
			getActivity().runOnUiThread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					handler.geopointFound(geopoint, address);
				}
				
			});
		}
	}
	
	protected void handlersNotFoundAddress(final String address)	{
		for (final IAddressVerify handler : handlers)	{
			getActivity().runOnUiThread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					handler.geopointNotFound(address);
				}
				
			});
			
		}
	}
	
	protected void handlersValidating()	{
		for (final IAddressVerify handler : handlers)	{
			getActivity().runOnUiThread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					handler.validatingProcess();
				}
				
			});
			
		}
	}
	
	@Override
	protected void onTextChanged (CharSequence text, int start, int before, int after)	{
		try	{ 
			String currentText = text.toString();
			if (!lastText.equals(currentText))	{
				lastText = currentText;
				validateAddress(currentText);
			}
		} catch (NullPointerException e)	{
			
		}
	}
	
	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public void setLongLatAddress(final SPGeoPoint location)	{
		if (null == location)	{
			setText("");
			return;
		}
		
		comm.getAddressObj(location.getLatitudeInDegrees(), location.getLongitudeInDegrees(), new ACommHandler<MapsQueryLocation>() {
			@Override
			public void onRecv(final MapsQueryLocation Obj) {
					activity.runOnUiThread(new Runnable()	{

						public void run() {
							// TODO Auto-generated method stub
							try	{
							 lastVerifiedText = Obj.getDisplay_name(); 
							 setText(Obj.getDisplay_name());
							 validatedAddress();

							 handlersFoundAddress(location, Obj.getDisplay_name());
							 
							} catch (Exception e)	{
								invalidatedAddress();
							}
						}
						
					});
			}
			
			@Override
			public void onError(MapsQueryLocation Obj) {
				setText("Error fetching address");
			}
		});
	}

	private class MapsQueryAddress extends ACommHandler<MapsQueryLonLat[]>	{
    	private String typed_address;
    	public MapsQueryAddress(String address)	{
    		this.typed_address = address;
    	}
    
    	@Override
		public void onRecv(final MapsQueryLonLat[] Obj) {
				activity.runOnUiThread(new Runnable()	{

					public void run() {
						try	{
						 if (! AddressVerifiableEditText.this.lastText.equals(typed_address))	
						 {
							 return;
						 }
						 
						 if ((null != lastVerifiedText) && (lastVerifiedText.equals(typed_address)))	{
							 return;
						 }
						 
						 lastVerifiedText = typed_address;
						 
						 SPGeoPoint location = new SPGeoPoint(Obj[0].getLat(), 
								 Obj[0].getLon());
						 
						 validatedAddress();
						 
						 handlersFoundAddress(location, typed_address);
						 
						 
						} catch (Exception e)	{
							if (!AddressVerifiableEditText.this.getText().toString().equals(typed_address))	{
								 return;
							 }
							invalidatedAddress();
							
							handlersNotFoundAddress(typed_address);
							
						}
					}
					
				});
		}
    	
    	@Override
    	public void onError(final MapsQueryLonLat[] Obj)	{
    		activity.runOnUiThread(new Runnable()	{

				public void run() {
	
	        		if (!AddressVerifiableEditText.this.getText().toString().equals(typed_address))	{
						 return;
					 }
	        		invalidatedAddress();
	        		
	        		handlersNotFoundAddress(typed_address);
	        		
	        	}
    		});
    	}
    	
    }


	public SPComm getComm() {
		return comm;
	}

	public void setComm(SPComm comm) {
		this.comm = comm;
	}
	
	
	
	public AddressVerifiableEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		postConstruct();
	}

	public AddressVerifiableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		postConstruct();
	}

	public AddressVerifiableEditText(Context context) {
		super(context);
		postConstruct();	
}
	
	private void postConstruct()	{
		validatingAddress();
		UIUtils.initSearchBar(this);
	}
	
	protected void validatingAddress()	{
		setBackgroundResource(R.drawable.selector_edittext_yellow);
		handlersValidating();
	}
	
	protected void validatedAddress()	{
		setBackgroundResource(R.drawable.selector_edittext_green);
	}
	
	protected void invalidatedAddress()	{
		setBackgroundResource(R.drawable.selector_edittext_red);
	}
	
	public void validateAddress(String address)	{
		validatingAddress();
		if (null != comm)	{
			comm.searchForAddress(address, new MapsQueryAddress(address));
		}
	}

}
