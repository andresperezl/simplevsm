package andresperezl.com.vitalsignmonitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ConnectListenerImpl;
import zephyr.android.BioHarnessBT.ConnectedEvent;
import zephyr.android.BioHarnessBT.PacketTypeRequest;
import zephyr.android.BioHarnessBT.ZephyrPacketArgs;
import zephyr.android.BioHarnessBT.ZephyrPacketEvent;
import zephyr.android.BioHarnessBT.ZephyrPacketListener;
import zephyr.android.BioHarnessBT.ZephyrProtocol;

public class BioHarnessListener extends ConnectListenerImpl {

    //Data Tags
    public final static String HEART_RATE = "hr";
    public final static String HRV = "hrv";
    public final static String RESPIRATION_RATE = "rr";
    public final static String POSTURE = "ps";
    public final static String TEMPERATYRE = "et"; //Estimated Temperature
    private final static String TAG = "BioHarnessListener";
    //Message ID from the BioHarness
    public final int SUMMARY_MSG_ID = 0x2B;
    private final Handler handler;
    private final SummaryPacketInfo sumPktInfo;

    public BioHarnessListener(Handler handler) {
        super(handler, null);
        this.handler = handler;
        sumPktInfo = new SummaryPacketInfo();
    }

    @Override
    public void Connected(ConnectedEvent<BTClient> eventArgs) {
        //Packets Settings
        PacketTypeRequest rqPacketType = new PacketTypeRequest();
        rqPacketType.EnableLogging(false);
        rqPacketType.EnableSummary(true);
        ZephyrProtocol protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), rqPacketType);
        protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
            @Override
            public void ReceivedPacket(ZephyrPacketEvent zephyrPacketEvent) {
                ZephyrPacketArgs msg = zephyrPacketEvent.getPacket();
                int id = msg.getMsgID();
                byte[] dataArray = msg.getBytes();

                Message message = handler.obtainMessage();
                Bundle data = message.getData();
                //I'm only interested in the SUMMARY message, which is the one that contains the main vital signs values
                if (id == SUMMARY_MSG_ID) {
                    data.putInt(HEART_RATE, sumPktInfo.GetHeartRate(dataArray));
                    data.putInt(HRV, sumPktInfo.GetHearRateVariability(dataArray));
                    data.putDouble(RESPIRATION_RATE, sumPktInfo.GetRespirationRate(dataArray));
                    data.putInt(POSTURE, sumPktInfo.GetPosture(dataArray));
                    data.putDouble(TEMPERATYRE, sumPktInfo.GetCoreTemperature(dataArray));
                    handler.sendMessage(message);
                }
            }
        });

    }

}
