package com.example.assignment_2.Maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.ActivityManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.assignment_2.Personal.PersonalActivity;
import com.example.assignment_2.R;
import com.example.assignment_2.friendlist.FriendsListActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String username, email, password;
    private String age, gender;
    private DatabaseReference databaseRef;
    private ImageButton btn_main_friend;
    private ImageButton btn_main_person;

    //for test
    private ImageButton btn_main_map;

    //update location
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mlocationRequest;
    private LocationManager mlocationManager;
    private com.google.android.gms.location.LocationListener locationListener;
    private long UPDATE_INTERVAL =2000;
    private long FASTEST_INTERVAL = 5000;
    private LocationManager locationManager;
    private LatLng latLng;
    private boolean isPermission;

    // Friend Location
    private List<String> fNameList;
    private HashMap<String, Object> fProfiles;
    private HashMap<String, HashMap<String, Object>> fAll;

    private Marker marker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (!isMyServiceRunning(new LocationService().getClass())) {
            LocationService();
        }


        databaseRef = FirebaseDatabase.getInstance().getReference();
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");
        age = getIntent().getStringExtra("z_age");
        gender =getIntent().getStringExtra("z_gender");

        if(requestSinglePermission()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            checkLocation();

        }
        //button friends
        btn_main_friend = (ImageButton) findViewById(R.id.main_btn_friends);
        btn_main_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, FriendsListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //button presonal
        btn_main_person = (ImageButton) findViewById(R.id.main_btn_personal);
        btn_main_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, PersonalActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String f_name;
                if (snapshot.child("friends/"+username).getValue() != null){
                    Map<String, Map<?,?>> friendsList = (Map<String, Map<?, ?>>) snapshot.child("friends/"+username).getValue();
                    fNameList =new ArrayList<String>(friendsList.keySet());

                    for(int i=0; i<fNameList.size(); i++){
                        f_name = fNameList.get(i);

                        Map<String,Double> friendsLocations = (Map<String, Double>) snapshot.child("coordinates/"+f_name).getValue();
                        String friendsAvatar = (String) snapshot.child("users/"+fNameList.get(i)+"/profile/avatar").getValue();
                        if(friendsLocations != null){
                            Double f_latitue = friendsLocations.get("latitude");
                            Double f_longitude = friendsLocations.get("longitude");
                            String f_avatar;
                            if (friendsAvatar != null){
                                f_avatar = friendsAvatar;
                            } else{
                                f_avatar = null;
                            }

                            LatLng friendLo = new LatLng(f_latitue, f_longitude);
                            placeMarkerOnMap(friendLo, f_avatar);
//                            fProfiles.put("location", friendLo);
//                            fProfiles.put("avatar", f_avatar);
//                            fAll.put(f_name, fProfiles);
//                            fProfiles.clear();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO Auto-generated method stub
                System.out.println("This didn't work");
            }
        });


    }
    public void LocationService(){
        Intent intent = new Intent(this,LocationService.class);
        intent.putExtra("username", username);
        startService(intent);
    }

    private boolean checkLocation() {
        if(!isLocationEnabled()){
        }
        return isLocationEnabled();
    }

    private boolean requestSinglePermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        isPermission =true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            isPermission = false;
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        return isPermission;

    }

    private boolean isLocationEnabled(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 2
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // 3
        if(mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        btn_main_map = (ImageButton) findViewById(R.id.main_btn_map);
        btn_main_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latLng!= null)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                }

            }
        });
        //mMap.getMinZoomLevel();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

//        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setOnMarkerClickListener(this);

//        //friends
//        if (fAll != null){
//            Iterator it=fAll.keySet().iterator();
//            while(it.hasNext()){
//                String f_name;
//                f_name = it.next().toString();
//                HashMap<String, Object> value = fAll.get(f_name);
//                LatLng f_location = (LatLng) value.get("location");
//                String f_avatar = (String) value.get("avatar");
//                placeMarkerOnMap(f_location, f_avatar);
//            }
//        }
    }

    protected  void placeMarkerOnMap(LatLng location, String avatar) {



        if (marker != null) {
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions().position(location);

        Bitmap icon;
        if (avatar != null){
            icon = base64ToBitmap(avatar);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        } else{
            icon = base64ToBitmap("iVBORw0KGgoAAAANSUhEUgAAAJYAAACWCAYAAAA8AXHiAAAAAXNSR0IArs4c6QAAAARzQklUCAgI\nCHwIZIgAABofSURBVHic7V15kBx3df5eX3OsZEuWy7EJMjJg65idGa0sYo5AspiE4CTgcBicBAIJ\n4UjFFCGpIgRSEQ6pggJSNjF_BCgwoaCwqsDGBCcVwDLBB5DNamdGa8kYZINs4yJeraQ95ujj5Y_d\n1_vb3rmv7h7tV7Wl1U4fr6e_fu_93tWETVCpVNrDzG9g5hcR0TYAKQBJAClmThFREsDW1e0XmLlC\nRGUAZQCV1X_nieh-wzC-snfv3sfDuJAogcIWYJiYmpq63LKsGwG8lJnHieg5Az7lSQAlZv6vSqVy\nxzXXXDM34PNFBiNLLGam48ePP99xnDcz8-8CyBORHrJMLhHNENF9S0tLN19zzTULRMRhyjQojAyx\nDh8-rGcymQs9z3sxM_8TgCyif30MYIqZP2Lb9vdOnjy5eMMNN7hhC9UPRP2Lb4kjR44Yl1xyyW7X\ndT8L4EVhy9MLmPluZj40Pz9fmpycdMKWpxfEklirZu5Sx3FuBfAaAFbYMvUZNWb-sGmaX9i7d-_T\ncTSXsSIWM2vFYnEfEX0RwIGw5RkGmPk_iehDX__6148eOnTIC1uedhELYk1NTZmWZb0CwCcA7Atb\nnjBARI8w8zuy2eyDRBR5MxlpYs3Ozlq2bV9PRDcT0e6w5YkCmPkpZv7T-fn5701OTlbClqcRIkus\nYrH4CiL6ODPvD1uWKIKIniCiN42Pjz8Qtiz1EDliTU9PP8cwjA8AeGfYssQEn_U878P79-9_MmxB\nVESGWFNTU2YikXgXM98CQAtbnjiBmRc1TXuvpmlfymQytbDlASJCrEKhcC0RfRTAwbBliTEYwDEi\nemM2mz0etjChEuvw4cP67t27P0xEHwxTjhHDPIDb5ubmbg4zyBoasY4ePbpL1_XDAF4QlgwjDAZw\n1HXdaycmJs6EIUAovsz09PSrNU07hk1SDQoE4ICmaT8tFotXhyHAUInFzHqhULjVMIxvENHYMM99\nPoKILgIwVSwW72Dmod7roZnCxx57bNvCwsJ9APLDOucm1sDMs4ZhHBjWqnEoxJqenn6WYRg_BPDs\nYZxvEw3xxPbt26_auXNnedAnGjixZmZmMkT0ABFdOOhzbaI1mPlsOp2-_Morrzw3yPMMlFiFQuGF\nAO4Pu3JzExtQZebL8_n8Lwd1goE5dMVi8VVE9NAmqSKJBBE9PTMzc9WgTjAQYk1PT78KwD2DOPYm\n-gYiopmHH354IHVtfTeFx44de4nneff3-7ibGBhOA3hBLpc72c-D9pVYhUJhnIgK2Ewixw1PENGL\ns9nsqX4dsG8EKBQKu4nof_p5zKiAiEAUiXz9oPBsAPc-8sgjF_frgH35tmZmZn5V07TjWOsWHgkQ\nESzLgmmaYGZUKhW47kh0ZzVCAcBkLpeb7_VAPWuXqakpk4jux4iRCgB0XYdlWf5PMpkcdc2VZ-Zv\nzs7O9tz11DOxLMv6DBHt6vU4UYSu69D1lWiJqr1GGUT0Es_zPtLrcXoiVrFYfBuAt_YqRFShado6\nDUVE54PWAjO_p1AovLSXY3RNrOnp6Twzf6aXk0cZmqZB1_UNJBLzOOJIENFds7Ozl3Z7gK6IVSgU\nxnRdv5OIjG5PHHWoZlAFESGRSIQg0dBxkeM4Dz366KNdXWy3GutWIrqiy31jgUbEAjDqK0MfRLSr\nXC5_qKt9O92hVCrdxMyf6uZkcYGmaUilUus0EzPDcRwsLy-fN8QSuK47MTExMdPJPh0Rq1AoXEJE\nxwFc1JFkMYOu60in0_4KkJlRrVZRLpfBHLv5HP1AlZl35PP5pXZ36MgUMvNfY8RJBaw57sCapqpW\nq-crqYAVZ76jBuK2NdZqUf5UxyLFEJZlYWxsDEQEz_OwvLyMWi0SfaChwnGcXQcOHPhZO9u2pbGY\nWV-dkndeQI1f2bYNx4n8cJehwDCM77e7bVvEKpVKbyGiV3YvUjzheR5s24bnrYyl0rSRy693ip0z\nMzOT7WzY8psqFovbsTKX6rwBM8O2bVQqFV9bmaaJZDIJwxjZ0F1b0DTtP9ppJWu5ATO_ByPksBNR\nS81j2zaWl5d9hz2RSPjhh1QqdT5E3pshUSqV3tJqo6bO-1NPPZV-5pln5rAyTH8kkEgkYJomPM-D\n67pwXRee54GZ_VWfkM8wDP9H9btc10W1Wj1vV4rM7ORyOavZbNSmen1ubu7dGCFSASsmTY1PBUkF\nrBX2CZnq5QuTySRM00StVlvnh9WDkNQ0zZGo6SIio1Qq_TmAhrnihhprdnbWchzn9Ci1wpumiXQ6\nvaFqoRd4nuf_uK7rk1SIqf4QERYWFkZilcnM5Xw-n270eUNng5lfO0qkkuRxP0kFrAVTxblPJpNI\npVJIJpO-2Q2a0lEAEaWKxeIfNfq8LrGYWfM8758HJ9ZwQURIpVIwTXMgN1fMZVBL1TOjIxay-LtG\nH9S9ykKh8HwAlw1MnCFC13WMjY11XOpi2zYWFhZQqVSa-k_dyCMQ3yuRSMS1gHBPoVB4Xb0P6jrv\nmqbFvoBPbphpmh1rCdd1UalUYNu2_5NOpxuW0XQCy7JQqVTAzBgbG_MXErLSjBk0Zn4DgK8FP9jw\niMzOzlqu61bqfRZFbNmyBYZhrFvV9eLPeJ6HSqWCWq3mazm54RLDanXseiEIdZ9yuYxyeWXgi_hl\nnufh3LmBzukYFH4xNzd3eXAs5QaNxcxvQkxIBaw86f0KWDIzXNdFrVaDaZq-s2-aJqrValNCCZlk\nVei6LmzbrrtoSKVSfilOpVJBpVLpizYMCZft2LHjjQC-rP5xA7E8z3vf0ETqA_odoHRdF7qu-2QA\n4AdJW8G2bdRqNTiO44ccmBmapm3w8aTeS-Jasj2wXrv1078bFIjoDxAg1rpHcDUveHqYQvWKRCKB\nsbH6URE1pgSgLfMo-3Tjly0vL4OZ_fADEaFarcLzPN9HC8qgajg18k9Evq8Xg4DqnK7rV2UyGZ87\n6x5DInp33FIUzcgiZo2IoOu6b46ENPX27badXtM0pNPpDblI8dls267r-6lhinryx0FjAdjhuu6r\nAdwuf1h3Ncx8w7Al6gRE5Ps-4pM00yy2bfs5vXK5jOXlZVQqlYGsvlTyqlBTSJ1C0k1xADP_pvp_\n_1tYbfPJDVugTiBNDuL8Amjo-6gmTaLguq77ZmeYMicSibZWk3EGEU2qrfn-XVlcXHyZruuRvnLx\nlyTp26xFS6BWKUjSedjodsUnpjMmWutyAL8G4H5A0Vi6rjfM-0QFom3ElHUarRZzVc-JjiLaqR2L\nEhzH8acD-lIHbWQUIaOEHMdpy3fRdd1PAMcR7WjkiOFl8gsBwIMPPpjasmXLcnjydAbJ_7WKLYnZ\ni-vgNDWIGpPV4VnXdXdNTEyc0QBg69atvxO2RO3CMIy283ZqlUHUwcx-0aAghqOTLiSicWDVFDLz\na8KVpzUkNZJOp2EYRizI0g6EUAsLC1haWlpXaw_AX9XGhVy6ru8G1laFu8ITpTUsy_LDBXE1a_Wg\nzoIQAjmOg3K5DM_z_MWJpmkYGxtDuVyOfAUEM18MrBFrT4iy1IVhGL4ZUJ3vUSEVsFbWrA4g8TwP\n5XIZlcrKC-rVlW86nYZlWSiXy5EtbyaiFwIAMbNWKpUilYySyXmdhBPUhohBrQLVc0hMbRAa1PM8\n1Go1aJrWsHLDcZxIltkws5tIJLYb09PTz42a_ZbVkPhV9W6czFSQ5K2smizLwpYtWzo6V_D_0ham\n_rQKUsoiQUIEhmFsiJe1S0Axi81kXl6O5iKeiHTXdZ9nJBKJg1FcykryVlZGwZsiT7Uaq5Ioe7sQ\nDSSt9LZtt0Uigaqt1GPJABHJbVqWtS6P2IuGY-ZIm0IAYOY9huu6e6Lqt3ieh2q16hOmnpyddiYL\nAWQ1VqvV_NyhatqCvYb1YJomxsbG1pFK1XJCWHkAxGdUJzF3ijiUMLuuu8eI-ihtx3FQqVQ29AOq\nmqJdyI2XqgdJUovZkn8BtKXBgqtUqcMSueRcjuPAdV1_VSe1-N2kluLQfU1EuwxmvjSqGkvgOI5f\ng96NrOpNlsI7IZJ0KAcdflmV1mo1fx9gTatJs0YjyDaGYcDzPP8aJJxg27Y_ZKTdxYYcB1ghcVQL\nAJn5UgPAtrAFaQa5MdVq1TeHQd-mGeRmSI5Ryo7biWjLtq7r-vsKITvpUZTVnWmaPrkdx8HS0pJf\n0tMOuUR7yijLarUK27ajqMG2GUQU2dkM0l0sWkN8Fbmh4hQ3gjjSonHk1SWdNq7KfmoVaiuoZdGq\n-bYsC4ZhrCtAlGBoqzSVEEu6q3Vd93OJUSIXESUNRHTohzzlsspzHAe2bfvmUC0FrgchlQQaE4lE\nVy32srLrxgRLADRISAknaJq2LpqeSqWaaq7gADh1VmrEkNQQQVMo8Su5oXJj5EYJGpGkHqlEIwS3\nb-WndBsAVWupRGtKgaJoMsl9aprmb9NM8wSbQ8Rfi5K2WsU2jZkjp7HEcQ4-mdK50gzqlGNgvaYK\nYtArLLV6VWQXEygrTtM0kUqlAMB37htB9SvVAHHUwMzJSPpYqgkRCDHUJ77RvuJTNSKVBF-HYUok\ncFtvuJtcgwRRxY9sFLMTDS35xKgGScXHihwk5iOOaif5Qok_qZ3MAjVg2Y0T3wtarfoSiYQf85IK\n2SBEa7ca9BYFGADOAPiVsAUJQvwHAP5oRwlotnLYxfEXTaFGwml1pFGU6t7FHzMMY52JDEJiXlHV\nVArOGMxcicoXHIRoLvkdaN7upT7tqjYKvtBS_TcqkIBqrVZrqI0kXNFJPjMMMHPFIKJK2II0g-u6\nWFpaAjM3jVILseqFB6JKpiBEvmZmTkymWsIcNRBRxWDmM1H_wuXpFPNWT17xr4IJ6078M4mVSSig\n27ouSd-I495uYLUdWYkIW7ZsifQsU2Y-Iz5W5BE0bypk-e15XstBa6oJESJJkhhYC8z2AnUckhp7\nUlNC3bSlybEkJRRhnIm8KQTWR6obQY1KB7dTqz5VMnmet25cYzfT_-pB7XkMLh7k_JLzk7yjtP8D\n9TWXaORyuRzJ2FUAZwwAT4ctRTNImXKrzhxZ8dUrCFQL-SQ-lkgk2p571QuE6OoqTy3fEbOprlLV\na1C1VNRygk3wtMHMJ6LsY4kGkU6WRimWek-7POUShRdNEnaoQSWbGnuTxYdcs5hUGYMUIzxuENHj\nYUvRDDLQTC09ES3Tbj252u3TT0J1O6RNhYQQdF3H4uKi7-wLqaR2SxYAMi0wyiCixw0AxbAFaQfq\nALN6Y4HqlRPL6q4XqAuDYH07UN-n6-Yc4oeJRhVN5TgODMNAKpXy43QxwAktl8s9xsyR9wYFaomv\nSiLVZPY73aGu8uRHNEc_co1Sfy9BUhl-Ytu2T6q4DDdhZiebzf7YAMAAngDwnJBlahvSqSKOPbBW\nASHVnv2apCw3W00kA6g7va8bqCtVuQaZ4SAvgwpWzkbZFGqa9gAReRqwYhNDlqcjiI8l_oc4vJZl\n-fnCfi_J1Td49SssAayZeAD-ylG0l6xc1dRU1OF53reBtflYJ0KUpWNIybJoLvF_xLFXmyaiDLX8\nRWrQJCRSr_qi26LDYYKIHgPWps08FK44nUGtcpA4kES2pXRZ4j5RDSZKoZ6kfsSki0kUUolvt7y8\njKWlpTg8LD8HVollGMaGd6FEGcFgoTjysnRPpVI-uUQjRMkvkQ4dIdXY2Bh0XfezAfLALC4uYnFx\n0R9tFNGOnHVwHOcEsEqsTCazyMw_C1ek9qHOagDW99sJuWQ4W61Ww9LSUiRMo5i-paUl3zlXJxPK\nNUi7mtRmxWUsNzPfffDgwWcAZQYpEd0XmkQ9QqLXAvG3ZOyP3NDFxUU_MTxMSLn0wsKCn-uzLAtb\nt271nXMJaajXFEPcK7-oibL7APzJ0EXpE4K-lPhcY2NjfseOmBdJOvcaPG0GtftaXaVKCKHRoJOY\noyS_-MTSdf0rrut-IRx5eke9myI3TioXarWa73PJND1ZffVa_y7ReDm2mDGB-iKDRrnOmGopAQM4\nJv9Zd4WFQuFxIopNoFSFruu44IILNsy3Urt6JAApsa4gGdXBIM0G46ppnmBZjAoxydJ424y4ruti\nYWEhtuRi5q_m8_kb5f_BlzR9FcD7hy5VHyDDyNrpYFFLcdTaLCFcJzdXJYsQVy3mE9mkmFB1xCUu\npVZtxJhYD6j_X0csXddvcV03lsQSB7kR5GaLdimXy_7qUXWeg9quEUnVQW9CJvmbmDU1vxhXwrSJ\nimmad6t_WJeXyGQyTwN4ZKgiDRFCJDV1IukUIYkMAAmaLjVXB6w1zqqDP9QKi2q16oc5RpxUIKJv\nZTKZn6t_21A-SUSfZuZPDU-s4UBycuITCSqVip8DDE6nEaiaSY4lsTNpmVdHhksAF0CszVu7YOY7\ng3_bkElNJpNfxIqHP1KQWJdKKvGBqtWqbx7VJlk1xiQlMjJgRJ0tQUR-2kXehyh-XC_dPjHBQq1W\n20CsusuUYrF4H4DfGLREYcKyLCSTSZw7d84vjZH6J5lMI2SUEIJUHKgDbKUwL-j4q-PExeRGNW_Z\nCzzPu2P__v1vCv69bieB4zj_YBjGfQOXKiRI8Zw6HknmPQRfL6KSRs1Rqv6YOgVH8n9qYZ7Uho0a\nuVYLRD9f77O6OnpiYuL7zPzUQKUKCfKW1qB5avTOGrVqQio55XfRSFJGLFF1qaNSjyE-WNTLXjoB\nET121113fafeZ3WJRUSepml_OVixwoFaPCcxLzWu1QhSSCivDla3FY2m1uMHiSvkitrLGnoBM3_6\n0KFDdeMxDb1KTdO-BWB-YFKFgGB_nzjq7b6zWS1TVreVdjJZLTZy1kUrjoLWYuYzp0-fvq3R5w2J\nlclkasx8aCBShQSVFOKUt9Nl3QwSv1LHbjdDv2rlI4BbJycnG15s0ys0DOPzzFzuv0zhQL2hsqqr\nZ7Y6hZg5WUU2SymJzxZnMHM5l8vd3Gybpt9oJpNZ1DTtb_srVnhQb7jUb_XL55EAqpRKN8MIBEw_\nQ0RNE7ItH9Vqtfo5AAt9EylEqJNl5Ob2Y3aDJJKlwUNmidZDsKAvhli4884739dqo7a8yGKxeD2A\nDdHVuELeEGGaJrZu3dq340raSAbnygukVGddovNxBTP_VT6fv6XVdm0Z-8nJyUcvvvji6wA8q2fJ\nIgAxiVIr1S-o_pOYRKldlyLDmA33COJoPp9_Wzsbtr3uLRaLWcRkzkO7SCaTSKfTfT-uOPFSDj0q\nYOZsPp8_1nrLNnwsQS6XKzHzl7oXK3oYlBOtRtpHJLQAZr6lXVIBHRALAGzbvgkRH9TWCQbdvDAC\ndeyCypNPPtlRdKAjYh08ePAsETWNX8QJ9VZn_SKD1MWPArE0TXvndddd19FrXTvOLTCzViwW7yGi\nV3a6bxSxbdu2da9Tkfp3SdOopkxIp5YsN0osy8C4mDvrAHBbLpe7qdOdOg7iEJF34sSJN1Sr1WNE\ndHmn-0cNkjgG4Meg1Al6QWKJFnJd1_elgsRSmydijqeZuasAeVee5Z49exY8z3s7RqDSVK1J1zTN\nT0hLjZaUHkvQU-ZAyCvh6mkr2XcEGlB_L5_PL3WzY9dLlomJiW8z8ye63T8qkDkJ0hShzjhtBLVM\nOThMV527EGcw89tzudz_drt_T_UbzEzFYrFERJlejhM21AGzom1amTG1wUIdUxlMGcURzHxrPp9_\nby_H6LkwqFAojAH4JRH1P9I4ZKjDazvZp97Q27iCmU-m0-mJK6-88lwvx-k5erdqgw8wc-xDzN0Q\nQ10ljgCpHMMwXt8rqYA-EAsA8vn8I7quvxZA7L3V8xXM7BDRtZlM5mg_jte3fMP4-Pg3AfxFv463\niaHCA_DKXC733_06YF8TWblc7l8BfKCfx9zE4EFEr8_n8_e23rJ99D1DmsvlPgrgH_t93E0MBExE\nf5bNZvteazewdpFisfg-AJ8c1PE30Re8a9XK9B0D7UMqFAo3Avg3Ihrsu9s20RGY2SWi1-VyuW8M\n6hwDb3A7duzYpOd59wBIDvpcm2gNZl4G8PJ8Pv_DQZ5n4FVo4-PjRwD8OoAnB32uTTQHM5_1PG__\noEkFDEFjCR5--OHLHMe5G8DBYZ1zE2vwPO8nuq6_PJvNnhrG-YZWN7tv375fLC4uvgzAl4d1zk2s\ngIg-Pj8_PzEsUgFD1FgqisXiO5n5X4hodCZkRBDMbBPRDblc7q5hnzu06RSlUul5zPzvAPaEJcOI\n4ydYiaafDOPkoY49OXLkiLFjx44vAPjjMOUYQfx9Npv9KBGFVhgQiXk6hULhdUR0O4AtYcsScywy\n8_X5fP67YQsSiaa3fD7_tVqtdhEzf3IUym-GjdXv7P1zc3Pbo0AqICIaS8WPfvSjnYlE4nYiennY\nssQE37Vt-11XX331T8IWREXkiCUoFot_iJVc46VhyxJRnFod0BHJl5hGllgAMDs7a3medxMz_w02\nCSY4RUQfHx8fv42IIluyGmliCaampkzLst6BlRdI7QxbnpBwEsDHstnsZ6NMKEEsiCU4cuSIsX37\n9huJ6INEdBViJn83YOYZAB_L5XJ3xIFQgljeGGbWSqXS7wO4CcC1YcszAFSJ6PsAbslms_cgho3B\nsSSWih_84AcXpFKp3yKiNwN4NWJ6Tczsapp2j-d5X7zgggu-dcUVV8R37B9iehMaoVAoXMLMv61p\n2lsRE03GzPcC-Jzrut85cODA_4UtT78wUsRScfz48R21Wm0fgJdqmnY9Mx9E-NfLAGaY-XYimjFN\nc3bv3r1zIcs0EIT9RQ8Np06dSp09e_Yq13X3aZr2CmbOMfPziWjbIM7HzGeI6KcAHmLmB3RdP37h\nhRf-eOfOnSMzN78ZzhtiNcLRo0e3WZZ1leM4zwPwXCK6mJkvArAdwHYikt8vW93lFwDmmXkewGkA\n80R0mpmfAXDSMIyf1mq1H09MTJwJ43qigv8H1gGDUZjtPZUAAAAASUVORK5CYII=\n");
        }

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

        marker = mMap.addMarker(markerOptions);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14));
                return false;
            }
        });
        //String titleStr = getAddress(location);  // add these two lines
        //markerOptions.title(titleStr);

    }

    // for icon of marker
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void startLocationUpdates() {

        mlocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mlocationRequest, (com.google.android.gms.location.LocationListener) this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "update Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();

        latLng = new LatLng(location.getLatitude(),location.getLongitude());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Log.i ("Service status", "Running");
                return true;
            }
        }
//        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }



}