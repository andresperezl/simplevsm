package andresperezl.com.vitalsignmonitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VitalSignsHandler extends Handler {
    private static RequestQueue requestQueue;
    private VitalSignMonitorActivity vsMonitor;

    public VitalSignsHandler(VitalSignMonitorActivity vsMonitor) {
        this.vsMonitor = vsMonitor;
        requestQueue = Volley.newRequestQueue(vsMonitor);
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle data = msg.getData();
        int hr = data.getInt(BioHarnessListener.HEART_RATE);
        int hrv = data.getInt(BioHarnessListener.HRV);
        int rr = (int) data.getDouble(BioHarnessListener.RESPIRATION_RATE);
        int ps = data.getInt(BioHarnessListener.POSTURE);
        int et = (int) data.getDouble(BioHarnessListener.TEMPERATYRE);
        StringRequest request = new StringRequest(Request.Method.POST, "http://10.2.10.176/vs/" + hr + "/" + hrv + "/" + rr + "/" + ps + "/" + et, vsMonitor, vsMonitor);
        requestQueue.add(request);
    }
}
