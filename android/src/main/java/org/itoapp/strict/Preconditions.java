package org.itoapp.strict;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.ContextCompat;


public class Preconditions {
    private static final String LOG_TAG = "Preconditions";

    public static boolean isLocationServiceEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;

            //I have read this is not actually required on all devices, but I have not found a way
            //to check if it is required.
            //If location is not enabled the BLE scan fails silently (scan callback is never called)
            if (!locationManager.isLocationEnabled()) {
                Log.i(LOG_TAG, "Location not enabled (API>=P check)");
                return false;
            }
        } else {
            int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
                Log.i(LOG_TAG, "Location not enabled (API<P check)");
                return false;
            }
        }
        return true;
    }

    public static boolean isBluetoothEnabled(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Log.i(LOG_TAG, "Bluetooth not enabled");
            return false;
        }
        return true;
    }

    public static boolean hasLocationPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean canScanBluetooth(Context context) {
        if (!isLocationServiceEnabled(context)) {
            return false;
        }
        if (!isBluetoothEnabled(context)) {
            return false;
        }
        if (!hasLocationPermissions(context)) {
            return false;
        }

        return true;
    }
}
