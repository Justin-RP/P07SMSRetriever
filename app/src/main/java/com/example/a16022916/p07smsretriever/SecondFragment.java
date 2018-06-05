package com.example.a16022916.p07smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SecondFragment extends Fragment {

    TextView tvResult;
    Button btnSend;
    EditText etQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container,false);

        tvResult = (TextView) view.findViewById(R.id.secTvMessage);
        btnSend = (Button) view.findViewById(R.id.secBtnRet);
        etQuery = view.findViewById(R.id.secEtWord);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }

                String strQuery = etQuery.getText().toString();

                String[] separated = strQuery.split(" ");
                String filter = "";
                String strFilterArgs = "";
                String[] theArgs = new String[separated.length];

                Log.d("SecondFragment length:",String.valueOf(separated.length));
                if(separated.length == 1){
                    filter += "body LIKE ?";
                    strFilterArgs = "%"+separated[0]+"%";
                    theArgs[0] = strFilterArgs;
                } else {
                    for (int i = 0; i < separated.length; i++) {
                        Log.d("SecondFragment i",String.valueOf(i) + " : " +String.valueOf(separated[i]));
                        strFilterArgs = "%"+separated[i]+"%";
                        theArgs[i] = strFilterArgs;
                    }
                    for (int i = 0; i < separated.length-1; i++){
                        filter += "body LIKE ? AND ";
                    }
                    filter += "body LIKE ? ";

                }

                Log.d("SecondFragment filter ",String.valueOf(filter));
                for(int i = 0; i < theArgs.length; i ++ ) {
                    Log.d("SecondFragment strArgs", String.valueOf(theArgs[i]));
                }



                // Create all messages URI
                Uri uri = Uri.parse("content://sms");

                // The columns we want
                //  date is when the message took place
                //  address is the number of the other party
                //  body is the message content
                //  type 1 is received, type 2 sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                // Fetch SMS Message from Built-in Content Provider

                Cursor cursor = cr.query(uri,reqCols,filter,theArgs,null);
//                Cursor cursor = cr.query(uri, reqCols, null, null, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvResult.setText(smsBody);


            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnSend.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getActivity(), "Permission not grant-ed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
