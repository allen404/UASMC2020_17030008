package id.mobilecomputing.uasmc2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

        getAllContactsWrapper();
    }

    //Wrapper untuk menjalankan fungsi getAllContacts, dimana wrapper ini akan mengecek terlebih dahulu apakah sudah mendapatkan izin akses contact atau belum
    private void getAllContactsWrapper(){
        int hasReadContactPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasReadContactPermission != PackageManager.PERMISSION_GRANTED){
            //memunculkan pesan alasan kenapa harus memberikan izin akses ke contact
            if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
                showMessageOKCancel("Izin akses kontak harus diberikan untuk me-load data kontak dan menggunakan aplikasi", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                });
                return;
            }
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
        //Jika izin udah didapatkan, maka akan menjalankan fungsi getAllContacts
        getAllContacts();
    }

    //Dialog box permission rationale
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",okListener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    //Mengecek hak akses setiap aplikasi dijalankan
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getAllContacts();
                }else{
                    Toast.makeText(MainActivity.this,"Permission READ_CONTACTS Ditolak",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //fungsi untuk menarik data contact dari contact storage
    private void getAllContacts(){


        List<ContactsGetSet> contactsGetSetList = new ArrayList();
        List<String> phonesList = new ArrayList<String>();
        ContactsGetSet contactsGetSet;

        //content resolver digunakan sebagai fungsi bantu untuk menarik data dari data storage kedalam aplikasi,content resolver ini berjalan layaknya aplikasi CRUD
        //ContentResolver dipanggil dikarenakan fungsi Cursor yang membutuhkan ContentResolver
        ContentResolver contentResolver = getContentResolver();

        //Pemanggilan fungsi cursor yang memilah data dari query yang dimasukkan kedalam ContentResolver
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        //Looping dimulai, jika cursor mendeteksi data display name lebih dari 0, maka akan dilakukan fungsi pemanggilan data lebih lanjut untuk menarik data nomor telepon
        if (cursor.getCount() > 0 ){
            while (cursor.moveToNext()){
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0){

                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null
                    , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while(phoneCursor.moveToNext()){
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactsGetSet = new ContactsGetSet();
                        contactsGetSet.setContactName(name);
                        contactsGetSet.setContactNumber(phoneNumber);
                        contactsGetSetList.add(contactsGetSet);
                    }

                    phoneCursor.close();

                }
            }

            ContactsAdapter contactsAdapter = new ContactsAdapter(contactsGetSetList, getApplicationContext());
            rvContacts.setLayoutManager(new LinearLayoutManager(this));
            rvContacts.setAdapter(contactsAdapter);

        }
        cursor.close();
    }
}