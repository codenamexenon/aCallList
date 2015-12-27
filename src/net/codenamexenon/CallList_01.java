package net.codenamexenon;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;

import android.content.ContentResolver;
import android.database.Cursor;

import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;

public class CallList_01 extends Activity
{
	// コンタクトIDを保存する一時的なリスト
	private ArrayList<String> contactIds = new ArrayList<String>();

	// コンテンツリゾルバにクエリする際に用いる検索対象
	// コンタクトID、コンタクト名、コンタクト番号保持情報
	private String[] mProjContacts = new String[] {
		Contacts._ID,
		Contacts.DISPLAY_NAME,
		Contacts.HAS_PHONE_NUMBER
	};

	// コンタクトID,コンタクト番号
	private String[] mProjDataKindsPhones = new String[] {
		CommonDataKinds.Phone.CONTACT_ID,
		CommonDataKinds.Phone.NUMBER
	};

	// コンタクトEMail
	private String[] mProjDataKindsEmails = new String[] {
		CommonDataKinds.Email.DATA
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calllist01);

		// コンタクト名一覧表示用
		ListView listContacts;

		// コンテンツリソルバのクエリ結果を保持するカーソル
		Cursor cursor = null;

		ContentResolver contentResolver = getContentResolver();

		ArrayAdapter<String> aAdapter = 
		new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		// コンタクト情報を取得(全件)
		cursor = contentResolver.query(
			Contacts.CONTENT_URI, mProjContacts, null, null, null);

		while (cursor.moveToNext()) {
			aAdapter.add(cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME)));
			contactIds.add(cursor.getString(cursor.getColumnIndex(Contacts._ID)));
		}

		// コンタクト名一覧テキストビューを操作するため、コンテキスト取得
		listContacts = (ListView) findViewById(R.id.listView1);

		// コンテンツリゾルバへの検索結果をコンタクト名一覧にバインド
		listContacts.setAdapter(aAdapter);

		// コンタクト名一覧を選択した際に、コールされるイベントハンドラ
		listContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String contactName = null; // コンタクト名保持用
				String contactNumber = null; // コンタクト番号保持用
				String contactEmail = null; // コンタクトEMail保持用
				
				String contactId = contactIds.get(position); // 選択されたポジションを保持
				
				TextView tmpTextView;
				
				ContentResolver contentResolver = getContentResolver();
				
				Cursor cursor = contentResolver.query(
					Contacts.CONTENT_URI, mProjContacts,
					ContactsContract.Contacts._ID+"="+contactId, // コンタクト名一覧で選択されたIDを条件に抽出
					null, null);

				tmpTextView = (TextView) findViewById(R.id.text_contactname);

				// 検索結果０件でない場合処理を継続
				if(cursor.getCount() != 0) {
					cursor.moveToFirst();

					// コンタクト名を取得
					contactName = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));

					// コンタクト番号保持状況を取得
					contactNumber = cursor.getString(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER));

					// 詳細ページのコンタクト名にコンタクト名を設定
					tmpTextView.setText(contactName);
				}
				tmpTextView = (TextView) findViewById(R.id.text_contactnumber);

				// contact番号を保持している場合
				if(contactNumber.equals("1")) {
					cursor = contentResolver.query(
					CommonDataKinds.Phone.CONTENT_URI, mProjDataKindsPhones,
					CommonDataKinds.Phone.CONTACT_ID+"="+contactId, // コンタクトIDに紐づくコンタクト番号を抽出
					null, null);

					if(cursor.getCount() != 0) {
						cursor.moveToFirst();
						contactNumber = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
						tmpTextView.setText(contactNumber);
					}
				} else {
					tmpTextView.setText("データなし");
				}
				cursor.close();

				cursor = contentResolver.query(CommonDataKinds.Email.CONTENT_URI, mProjDataKindsEmails,
					CommonDataKinds.Email.CONTACT_ID+"="+contactId, // コンタクトIDに紐づくEMailを抽出
					null, null);

				tmpTextView = (TextView) findViewById(R.id.text_contactemail);
				if(cursor.getCount() != 0) {
					cursor.moveToFirst();
					contactEmail = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.DATA));
					tmpTextView.setText(contactEmail);
				} else {
					tmpTextView.setText("データなし");
				}
				cursor.close();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
