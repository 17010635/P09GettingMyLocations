package sg.edu.rp.c347.p09_gettingmylocations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {
    public MyService() {
    }
    boolean started;
    FusedLocationProviderClient client;
    LocationCallback mLocationCallback;
    String folderLocation;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        //create FusedLocationProviderClient
        client = LocationServices.getFusedLocationProviderClient(MyService.this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    try {
                        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ProblemStatement";
                        File targetFile = new File(folderLocation, "data.txt");
                        FileWriter writer = new FileWriter(targetFile, true);
                        writer.write( lat + ", " + lng + "\n");
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                        Toast.makeText(MyService.this, "Failed to write!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    String msg = "Lat :" + lat +
                            " Lng : " + lng;
                    Toast.makeText(MyService.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        };

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false){
            started = true;
            Log.d("Service", "Service started");
            if (checkPermission()) {

                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);

                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

                Toast.makeText(MyService.this, "Added", Toast.LENGTH_SHORT).show();

            } else {
                String msg = "Permission not granted to be retrieve location info";
                Toast.makeText(MyService.this, msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("Service", "Service is still running");
            Toast.makeText(MyService.this, "Service is still running", Toast.LENGTH_SHORT).show();
        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        Log.d("Service", "Service exited");
        client.removeLocationUpdates(mLocationCallback);
        Toast.makeText(MyService.this, "Service exited", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_Write = ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Read = ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.READ_EXTERNAL_STORAGE);


        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED && (permissionCheck_Write == PermissionChecker.PERMISSION_GRANTED && permissionCheck_Read == PermissionChecker.PERMISSION_GRANTED) ) {
            return true;
        } else {
            Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
