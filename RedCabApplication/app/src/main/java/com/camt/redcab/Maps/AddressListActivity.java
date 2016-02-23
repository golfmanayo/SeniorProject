package com.camt.redcab.Maps;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.camt.redcab.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressListActivity extends AppCompatActivity {
    private ListView listView;
    private String mInputAddress;
    public List<Address> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        listView = (ListView) findViewById(R.id.listview);
        mInputAddress = getIntent().getStringExtra("ADDRESS");
        Toast.makeText(getApplicationContext(), mInputAddress, Toast.LENGTH_LONG).show();
        //listView.setAdapter(new ListViewAdapter(getApplicationContext()));
        new GeocodingTask().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Address address = addressList.get(position);

                int maxAddressLines = address.getMaxAddressLineIndex();
                String locationDescription = "";
                for (int i = 0; i < maxAddressLines; i++) {
                    locationDescription = locationDescription + address.getAddressLine(i) + "\n";
                }

                double lat = address.getLatitude();
                double lng = address.getLongitude();

                Intent i = new Intent();
                i.putExtra("LAT", lat);
                i.putExtra("LNG", lng);
                i.putExtra("TITLE", locationDescription);

                setResult(RESULT_OK, i);
                finish();
            }
        });

    }

    public class GeocodingTask extends AsyncTask<String, Void, List<Address>> {


        @Override
        protected List<Address> doInBackground(String... params) {
            try {
                Geocoder geoCoder = new Geocoder(getApplicationContext(), new Locale("EN"));
                addressList = geoCoder.getFromLocationName("%"+mInputAddress+"%", 30);  // normal geocoding
                //addressList = geoCoder.getFromLocation(13.2581015, 100.920621, 30); // บางแสน 13.2581015, 100.920621 reverse geo coding
                return addressList;

            } catch (Exception e) {
                //Log.i("Error in Geocoder", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Address> s) {
            super.onPostExecute(s);

            if (addressList == null) {
                addressList = new ArrayList<Address>();
            }
            listView.setAdapter(new ListViewAdapter(getApplicationContext()));

        }
    }

    public class ListViewAdapter extends BaseAdapter {

        public LayoutInflater mInflater;
        public Context mContext;

        public ListViewAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);


        }

        @Override
        public int getCount() {
            return addressList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.address_list_item, null);
                holder = new ViewHolder();
                holder.titleTextView = (TextView) convertView.findViewById(R.id.addressTitleTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Address item = addressList.get(position);
            String title = item.getAddressLine(0);
            String lat = String.valueOf(item.getLatitude());
            String lng = String.valueOf(item.getLongitude());
            String index = String.valueOf(position + 1);
            String msg = String.format("<b><font color='#FEAA3F'>%s. %s </font></b><br/>%s°, %s°", index, title, lat, lng);
            holder.titleTextView.setText(Html.fromHtml(msg));

            return convertView;
        }

        public class ViewHolder {
            TextView titleTextView;
        }
    }
}
