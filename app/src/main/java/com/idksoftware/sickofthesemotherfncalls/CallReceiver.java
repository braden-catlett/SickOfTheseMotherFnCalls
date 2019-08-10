package com.idksoftware.sickofthesemotherfncalls;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class CallReceiver extends PhoneCallReceiver {
//    public static ArrayList<String> contacts = new ArrayList<>();
    private static int originalRingerMode = 0;
    
    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);
    }

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
//        if(contacts != null && contacts.size() == 0) {
//            contacts = getContactList(ctx);
//        }

        AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        originalRingerMode = audio.getRingerMode();

        if(contactExists(ctx, cleansePhoneNumberOfFormating(number))) {
            Toast.makeText(ctx, "This one has been validated", Toast.LENGTH_LONG).show();
        }
        else {
            audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        this.restoreOriginalRingerSettings(ctx);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        this.restoreOriginalRingerSettings(ctx);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {}

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {}

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        this.restoreOriginalRingerSettings(ctx);
    }

    public boolean contactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        try (Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null)) {
            if (cur.moveToFirst()) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> getContactList(Context context) {
        ContentResolver cr = context.getContentResolver();
        ArrayList<String> contacts = new ArrayList<String>();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.add(CallReceiver.cleansePhoneNumberOfFormating(phoneNo));
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        return contacts;
    }

    public static String cleansePhoneNumberOfFormating(String phoneNo) {
       return phoneNo.replace("(", "").replace(")", "").replace("-","").replace("+", "").replace(" ", "");
    }

    private void restoreOriginalRingerSettings(Context ctx) {
        AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        if(audio.getRingerMode() != originalRingerMode) {
            audio.setRingerMode(originalRingerMode);
        }
    }
}
