package il.ac.tau.team3.shareaprayer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {
    if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
    {
      Log.i("Startup manager", "Starting up...");

      context.startService(new Intent(LocServ.ACTION_SERVICE));
    }
  }
}
