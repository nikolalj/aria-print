package me.ariapos.print;

import android.content.Context;

import com.dantsu.escposprinter.EscPosCharsetEncoding;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;

import android.os.Build;
import android.util.Log;

public class AriaPrint {

    private static final String TAG = "AriaPrint";
    private EscPosCharsetEncoding encoding = new EscPosCharsetEncoding("windows-1252", 16);

    private Context context;
    private int width = 48;
    private int charsPerLine = 32;
    private int dpi = 203;
    private String ipAddress = null;
    private int qrCodeSize = 30;

    private Szzcs szzcs;

    /**
     * AriaPrint Constructor
     */
    public AriaPrint() {
    }

    /**
     * AriaPrint Constructor
     *
     * @param context
     * @param width paper width in mm
     * @param charsPerLine number of characters per line
     * @param dpi printer dpi
     * @param ipAddress ip address with port number if we're printing over network
     * @param qrCodeSize QR Code size in mm
     */
    public AriaPrint(Context context, int width, int charsPerLine, int dpi, String ipAddress, int qrCodeSize) {
        this.context = context;
        this.width = width;
        this.charsPerLine = charsPerLine;
        this.dpi = dpi;
        this.ipAddress = ipAddress;
        this.qrCodeSize = qrCodeSize;

        if(Build.MANUFACTURER.equals("szzcs")) {
            szzcs = new Szzcs(context);
        }
    }

    /**
     * Print to printer
     *
     * @param text text to print
     * @param qrCode qr code to print
     */
    public void print(final String text, final String qrCode) {
        if(Build.MANUFACTURER.equals("szzcs")) {
            szzcs.print(text, qrCode, qrCodeSize);
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    DeviceConnection connection = getDeviceConnection();
                    EscPosPrinter printer = new EscPosPrinter(connection, dpi, width, charsPerLine, encoding);

                    String textToPrint = "";

                    if(text != null && !text.trim().isEmpty()) {
                        textToPrint += text + "[L]\n[L]\n[L]\n";
                    }

                    if(qrCode != null && !qrCode.trim().isEmpty()) {
                        textToPrint += "[L]<qrcode size='" + qrCodeSize + "'>" + qrCode + "</qrcode>\n[L]\n[L]\n[L]\n[L]\n[L]\n";
                    }

                    printer.printFormattedTextAndCut(textToPrint);

                    Thread.sleep(1000);
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Feed paper
     */
    public void feedPaper() {
        if(Build.MANUFACTURER.equals("szzcs")) {
            szzcs.feedPaper();
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    DeviceConnection connection = getDeviceConnection();
                    EscPosPrinter printer = new EscPosPrinter(connection, dpi, width, charsPerLine, encoding);
                    printer.printFormattedText("[L]\n[L]\n[L]\n[L]\n[L]\n");
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Get device connection
     * @return connection
     */
    private DeviceConnection getDeviceConnection() {
        DeviceConnection connection;
        if(ipAddress != null && !ipAddress.trim().isEmpty()) {
            String[] parts = ipAddress.split(":");
            connection = new TcpConnection(parts[0], Integer.valueOf(parts[1]));
        } else {
            connection = BluetoothPrintersConnections.selectFirstPaired();
        }

        return connection;
    }
}
