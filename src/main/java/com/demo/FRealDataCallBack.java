package com.demo;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;

class FRealDataCallBack
        implements HCNetSDK.FRealDataCallBack_V30
{
    public void invoke(NativeLong lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser)
    {
        System.out.println("回调中");
    }
}
