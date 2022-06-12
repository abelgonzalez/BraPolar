package br.example.com.brapolar;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CellResiver {
    private TelephonyManager telephony;
    Context context;
    private int CID;

    public CellResiver(Context context) {

        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.context = context;
    }

    public void Task() {
        int cid = 0, lac = 0;
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
            if (location != null) {
                cid = location.getCid();
                lac = location.getLac();

            } else {
                // Log.i("error","gsm");
            }
        } else if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            final CdmaCellLocation location = (CdmaCellLocation) telephony.getCellLocation();
            if (location != null) {
                cid = location.getBaseStationId();
                lac = location.getSystemId();
            } else {
                //Log.i("error","cdma");
            }
        }
        // Log.i("msg","ID=>"+cid +" LAC=>"+lac);

        if (CID != cid) {//insertar new y actualizar
            //insertar
            //Toast.makeText(context, R.string.app_tower_cell_change, Toast.LENGTH_SHORT).show();
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            String dateString = formater.format(new Date());
            EntityTower tower = new EntityTower(dateString, cid, lac);
            MySqliteHandler bd = new MySqliteHandler(context);
            bd.addTower(tower);
            int LAC = lac;
            CID = cid;
        }
        //si no esta en la misma celda id
    }
}
