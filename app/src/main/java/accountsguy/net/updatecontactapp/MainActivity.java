package accountsguy.net.updatecontactapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText nameeditText, phoneeditText, emaileditText;
    Button button;

    int contactsPermission;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsPermission = checkSelfPermission( Manifest.permission.WRITE_CONTACTS );

        if(contactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                    1);
        }

        nameeditText = (EditText) findViewById(R.id.name);
        phoneeditText = (EditText) findViewById(R.id.phone);
        emaileditText = (EditText) findViewById(R.id.mail);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList  ops = new ArrayList ();

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                //------------------------------------------------------ Names
                if (nameeditText.getText().length() > 0) {
                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    nameeditText.getText().toString()).build());
                }

                //------------------------------------------------------ Mobile Number
                if (phoneeditText.getText().length() > 0) {
                    ops.add(ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    phoneeditText.getText().toString())
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }

//                //------------------------------------------------------ Home Numbers
//                if (HomeNumber != null) {
//                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                            .withValue(ContactsContract.Data.MIMETYPE,
//                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
//                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
//                            .build());
//                }
//
//                //------------------------------------------------------ Work Numbers
//                if (WorkNumber != null) {
//                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                            .withValue(ContactsContract.Data.MIMETYPE,
//                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
//                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
//                            .build());
//                }

                //------------------------------------------------------ Email
                if (emaileditText.getText().length() > 0) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, emaileditText
                                    .getText().toString())
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());
                }

//                //------------------------------------------------------ Organization
//                if (!company.equals("") && !jobTitle.equals("")) {
//                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                            .withValue(ContactsContract.Data.MIMETYPE,
//                                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//                            .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
//                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                            .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
//                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                            .build());
//                }

                try {
                    getApplicationContext().getContentResolver().
                            applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(MainActivity.this, "Contact Inserted", Toast
                            .LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast
                            .LENGTH_SHORT).show();
                } catch (OperationApplicationException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast
                            .LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast
                            .LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Contacts Permission Granted", Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            Toast.makeText(this, "Contacts Permission Denaied", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
