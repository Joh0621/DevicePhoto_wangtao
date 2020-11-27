package com.demo;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

class G_ExceptionCallBack implements HCNetSDK.FExceptionCallBack {
    public void invoke(int dwType, NativeLong lUserID, NativeLong lHandle, Pointer pUser)
    {
        char[] tempbuf = new char['?'];
        switch (dwType)
        {
            case 32773:
                System.out.println("----------reconnect--------" + System.currentTimeMillis());
                break;
        }
    }
}
