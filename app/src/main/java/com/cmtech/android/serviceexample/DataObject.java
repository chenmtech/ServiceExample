package com.cmtech.android.serviceexample;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataObject {
    private List<Integer> dataList = new ArrayList<>();

    private class WorkRunnable implements Runnable {

        WorkRunnable() {

        }

        @Override
        public void run() {
            try {
                int i = 0;
                while(!Thread.currentThread().isInterrupted()) {
                    addData(i++);
                    Log.e("DataObject", ""+i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Log.e("DataObject", Thread.currentThread() + " catch " + e);
            } finally {
                dataList = null;
                Log.e("DataObject", Thread.currentThread() + " is interrupted.");
            }
        }
    }

    private Thread workThread;

    public void start() {
        if(workThread == null || !workThread.isAlive()) {
            workThread = new Thread(new WorkRunnable());
            workThread.start();
        }
    }


    public synchronized void stop() {
        if(workThread != null && workThread.isAlive()) {
            workThread.interrupt();
            try {
                workThread.join();
                Log.e("DataObject", "join is executed.");
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private synchronized void addData(int i) {
        dataList.add(i);
    }

}
