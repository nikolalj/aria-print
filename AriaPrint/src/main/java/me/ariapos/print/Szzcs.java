package me.ariapos.print;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.widget.Toast;

import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;

import java.util.concurrent.ExecutorService;

public class Szzcs {
    private static final String TAG = "Szzcs";
    private static final String OUT_OF_PAPER = "Nema papira";

    private Context context;
    private DriverManager mDriverManager = DriverManager.getInstance();
    private Sys mSys = mDriverManager.getBaseSysDevice();
    private Printer mPrinter;
    private ExecutorService mSingleThreadExecutor;

    private int sdkInitCount = 0;

    public Szzcs(Context context) {
        this.context = context;

        mSingleThreadExecutor = mDriverManager.getSingleThreadExecutor();
        mDriverManager = DriverManager.getInstance();
        mPrinter = mDriverManager.getPrinter();

        initSdk(false);
    }

    /**
     * Initialize SDK
     * @param reset
     */
    private void initSdk(final boolean reset) {
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int statue = mSys.getFirmwareVer(new String[1]);
                if (statue != SdkResult.SDK_OK) {
                    int sysPowerOn = mSys.sysPowerOn();

                    Log.i(TAG, "sysPowerOn: " + sysPowerOn);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mSys.setUartSpeed(460800);
                final int i = mSys.sdkInit();

                if (reset && ++sdkInitCount < 2 && i == SdkResult.SDK_OK && mSys.getCurSpeed() != 460800) {
                    Log.d(TAG, "switch baud rate, cur speed = " + mSys.getCurSpeed());

                    int ret = mSys.setDeviceBaudRate();

                    if (ret != SdkResult.SDK_OK) {
                        Log.e(TAG, "SwitchBaudRate error: " + ret);
                    }

                    mSys.sysPowerOff();
                    initSdk(true);

                    return;
                }
            }
        });
    }

    /**
     * Print text and QR Code
     * @param text text to print
     * @param qrCode QR Code text
     * @param qrCodeSize QR Code size in mm
     */
    public void print(final String text, final String qrCode, final int qrCodeSize) {
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    toast(OUT_OF_PAPER);
                    return;
                }

                // Text
                PrnStrFormat format = new PrnStrFormat();
                format.setTextSize(22);
                format.setAli(Layout.Alignment.ALIGN_NORMAL);
                format.setFont(PrnTextFont.MONOSPACE);
                format.setStyle(PrnTextStyle.NORMAL);

                if(text != null && !text.trim().isEmpty()) {
                    mPrinter.setPrintAppendString(text, format);
                }

                // QR Code
                if(qrCode != null && !qrCode.trim().isEmpty()) {
                    mPrinter.setPrintAppendString("\n", format);
                    mPrinter.setPrintAppendQRCode(qrCode, qrCodeSize * 10, qrCodeSize * 10, Layout.Alignment.ALIGN_CENTER);
                }

                mPrinter.setPrintAppendString("\n\n\n", format);
                printStatus = mPrinter.setPrintStart();

                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    toast(OUT_OF_PAPER);
                    return;
                }
            }
        });
    }

    /**
     * Feed paper
     */
    public void feedPaper() {
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    toast(OUT_OF_PAPER);
                } else {
                    mPrinter.setPrintLine(120);
                }
            }
        });
    }

    /**
     * Show Toast
     * @param text
     */
    public void toast(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
