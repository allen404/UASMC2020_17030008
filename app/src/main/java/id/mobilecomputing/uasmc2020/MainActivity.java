package id.mobilecomputing.uasmc2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvContacts;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvContacts = (RecyclerView) findViewById(R.id.recViewContacts);

        getAllContacts();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_READ_CONTACTS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getAllContacts();
            }
        }else{
            Toast.makeText(this, "Izin akses kontak harus di izinkan terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllContacts(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        List<ContactsGetSet> contactsGetSetList = new ArrayList();
        ContactsGetSet contactsGetSet;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0 ){
            while (cursor.moveToNext()){
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0){
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactsGetSet = new ContactsGetSet();
                    contactsGetSet.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null
                    , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if(phoneCursor.moveToNext()){
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactsGetSet.setContactNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                             new String[]{id}, null);
                    while(emailCursor.moveToNext()){
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactsGetSetList.add(contactsGetSet);
                }
            }
            ContactsAdapter contactsAdapter = new ContactsAdapter(contactsGetSetList, getApplicationContext());
            rvContacts.setLayoutManager(new LinearLayoutManager(this));
            rvContacts.setAdapter(contactsAdapter);
        }
    }
}