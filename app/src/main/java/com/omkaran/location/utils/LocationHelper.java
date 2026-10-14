package com.omkaran.location.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper
{
    public static String getAddress(Context context, Location location)
    {
        Geocoder geocoder = new Geocoder(context,Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if (addresses != null && !addresses.isEmpty())
            {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "Address not available";
    }
}