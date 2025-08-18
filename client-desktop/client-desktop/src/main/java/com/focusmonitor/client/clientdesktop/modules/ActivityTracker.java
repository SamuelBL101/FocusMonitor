package com.focusmonitor.client.clientdesktop.modules;

import com.focusmonitor.client.clientdesktop.HomepageController;
import com.focusmonitor.client.clientdesktop.WelcomeController;
import com.focusmonitor.client.clientdesktop.communication.UsageSender;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.prefs.Preferences;

public class ActivityTracker implements Runnable{

    private final HomepageController homepageController;
    private boolean running = false;
    private static final int MAX_PATH = 1024;
    private final ConcurrentLinkedQueue<UsageSession> queue = new ConcurrentLinkedQueue<>();

    public ActivityTracker(HomepageController homepageController) {
        this.homepageController = homepageController;
    }
    public static Activity getCurrentActivity() throws InterruptedException {
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) {
            return null;
        }

        char[] windowText = new char[MAX_PATH];
        User32.INSTANCE.GetWindowText(hwnd, windowText, MAX_PATH);
        String windowTitle = Native.toString(windowText);

        char[] className = new char[MAX_PATH];
        User32.INSTANCE.GetClassName(hwnd, className, MAX_PATH);
        String windowClassName = Native.toString(className);

        IntByReference pid = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);

        String executablePath = getProcessPath(pid.getValue());

        return new Activity(
                windowTitle.isEmpty() ? "Unknown Window" : windowTitle,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );

    }

    private static String getProcessPath(int value) {
        WinNT.HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(
                WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ, false, value);
        if (processHandle == null) {
            return "Unknown Process";
        }
        try {
            Memory path = new Memory(MAX_PATH * Native.WCHAR_SIZE);
            int result = Psapi.INSTANCE.GetModuleFileNameEx(processHandle, null, path, MAX_PATH);
            return path.getWideString(0);
        } finally {
            Kernel32.INSTANCE.CloseHandle(processHandle);
        }
    }

    @Override
    public void run() {
        Activity lastActivity = null;
        long millis = System.currentTimeMillis();
        System.out.println("Starting activity tracking..." + millis);
        this.running = true;
        while (running) {
            try {
                Activity currentActivity = getCurrentActivity();
                if (currentActivity != null && !currentActivity.equals(lastActivity)) {
                    String endtime = java.time.LocalTime.now().toString();
                    String starttime = java.time.LocalTime.now().toString();
                    Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);

                    String userId = prefs.get("userID", null);
                    System.out.println("Current activity: " + currentActivity.getAppName() + "userid: " + userId);
                    if (userId != null) {
                        //UsageSession usageSession = new UsageSession(Long.parseLong(userId), currentActivity);
                        queue.add(new UsageSession(Long.parseLong(userId), currentActivity));
                        //System.out.println("fe" + System.currentTimeMillis());
                        if (millis + 10000 < System.currentTimeMillis()) {
                            System.out.println("Sending usage data for: " + currentActivity.getAppName());
                            millis = System.currentTimeMillis();
                            sendUsageData();
                        }
                    }
                    System.out.println("Aktívne okno: " + currentActivity);
                    lastActivity = currentActivity;

                    if (homepageController != null) {
                    }

                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("Activity tracking interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("Error while tracking activity: " + e.getMessage());

            }
        }
    }

    public void stopRunning(){
        this.running = false;
    }
    public void sendUsageData() {
        System.out.println("Sending usage data...");
        while (!queue.isEmpty()) {
            System.out.println("Processing usage session from queue...");
            UsageSession usageSession = queue.poll();
            if (usageSession != null) {
                UsageSender.sendUsage(usageSession);
            }
        }
    }
}
