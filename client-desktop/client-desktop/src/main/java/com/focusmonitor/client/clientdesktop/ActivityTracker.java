package com.focusmonitor.client.clientdesktop;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

public class ActivityTracker implements Runnable{

    private final HomepageController homepageController;
    private boolean running = false;
    private static final int MAX_PATH = 1024;

    public ActivityTracker(HomepageController homepageController) {
        this.homepageController = homepageController;
    }
    public static String getCurrentActivity() throws InterruptedException {
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) {
            return null;
        }

        char[] windowText = new char[MAX_PATH];
        User32.INSTANCE.GetWindowText(hwnd, windowText, MAX_PATH);
        String windowTitle = Native.toString(windowText);

        // Get Window Class Name
        char[] className = new char[MAX_PATH];
        User32.INSTANCE.GetClassName(hwnd, className, MAX_PATH);
        String windowClassName = Native.toString(className);

        // Get Process ID (PID)
        IntByReference pid = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);

        // Get Executable Path from PID
        String executablePath = getProcessPath(pid.getValue());

        return String.format(
                "Title: %s\nClass: %s\nExecutable: %s",
                windowTitle,
                windowClassName,
                executablePath
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
        String lastActivity = "";
        this.running = true;
        while (running) {
            try {
                String currentActivity = getCurrentActivity();
                if (currentActivity != null && !currentActivity.equals(lastActivity)) {
                    System.out.println("Aktívne okno: " + currentActivity);
                    lastActivity = currentActivity;

                    if (homepageController != null) {
                        homepageController.updateActivity(currentActivity);
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
}
