package andresperezl.com.vitalsignmonitor;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import zephyr.android.BioHarnessBT.BTClient;


public class VitalSignMonitorActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, Response.Listener, Response.ErrorListener {
    private final static int REQUEST_BT = 1;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BTClient btClient;
    public static BioHarnessListener bhListener;
    private ArrayList<String> mDataset;
    private MyAdapter mAdapter;
    private PlaceholderFragment placeholder;
    private SwipeRefreshLayout mSwipeLayout;
    private BroadcastReceiver mReceiver;
    private VitalSignsHandler vsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_sign_monitor);
        
        //Initialize the Hanlder and Listener
        vsHandler = new VitalSignsHandler(this);
        bhListener = new BioHarnessListener(vsHandler);

        if (savedInstanceState == null) {
            mDataset = new ArrayList<String>();
            placeholder = PlaceholderFragment.newInstance(mDataset);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, placeholder)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter = placeholder.getAdapter();
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.contentView);
        mSwipeLayout.setOnRefreshListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vital_sign_monitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            mAdapter.addItem("Test " + new Random().nextInt());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Refresh Bluetooth devices on swipe down on the list
    @Override
    public void onRefresh() {
        mDataset.clear();
        mAdapter.notifyDataSetChanged();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btIntent, REQUEST_BT);
        } else {
            refreshDevices();
        }
    }
    
    //Only get the devices that start with "BH" (BioHarnesses)
    public void refreshDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName();
                if (name.startsWith("BH")) {
                    // Add the name and address to an array adapter to show in a ListView
                    mAdapter.addItem(device.getName() + "\n" + device.getAddress());
                }
            }
        }
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            refreshDevices();
        } else {
            Toast.makeText(this, "You need to enable the Bluetooth", Toast.LENGTH_LONG).show();
            mSwipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(Object o) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private ArrayList<String> myDataset;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(ArrayList<String> dataset) {
            PlaceholderFragment f = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("dataset", dataset);
            f.setArguments(args);

            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            myDataset = getArguments().getStringArrayList("dataset");
            View rootView = inflater.inflate(R.layout.fragment_vital_sign_monitor, container, false);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            // specify an adapter (see also next example)
            mAdapter = new MyAdapter(myDataset);
            mRecyclerView.setAdapter(mAdapter);
            return rootView;
        }

        public MyAdapter getAdapter() {
            return (MyAdapter) mAdapter;
        }
    }
}
