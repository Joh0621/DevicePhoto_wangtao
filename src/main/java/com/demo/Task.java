package com.demo;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class Task extends TimerTask {
    @Override
    public void run() {
        // 读取文件参数
        String path = System.getProperty("user.dir");
        System.out.println(path);
        List<String> lines = null;
        try {
            lines = Files.readAllLines((new File("src/main/resources/properties.txt")).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap();
        lines.stream().filter((x) -> {
            return !x.equals("") && !x.contains("#");
        }).forEach((x) -> {
            String var10000 = (String)map.put(x.split("=")[0].trim(), x.split("=")[1].trim());
        });
        String deviceIP = (String)map.get("deviceIP");
        int iPort = Integer.valueOf((String)map.get("port"));
        String userName = (String)map.get("username");
        String password = (String)map.get("password");

        System.out.println("抓取时间:" + LocalDateTime.now().toString());

        // 调用海康SDK
        HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
        new FRealDataCallBack();
        hCNetSDK.NET_DVR_Init();
        hCNetSDK.NET_DVR_SetConnectTime(2000, 1);
        hCNetSDK.NET_DVR_SetReconnect(10000, true);
        hCNetSDK.NET_DVR_SetExceptionCallBack_V30(0, 0, new G_ExceptionCallBack(), (Pointer)null);
        HCNetSDK.NET_DVR_DEVICEINFO_V30 info = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        //设置图片质量和大小
        HCNetSDK.NET_DVR_JPEGPARA  JpegPara = new HCNetSDK.NET_DVR_JPEGPARA();
        JpegPara.wPicQuality=0;
        JpegPara.wPicSize=5;

       // ByteBuffer jpegBuffer = ByteBuffer.allocate(1024 * 1024);

        NativeLong lUserID = hCNetSDK.NET_DVR_Login_V30(deviceIP, iPort, userName, password, info);
        if (lUserID.intValue() < 0) {
            System.out.println("注册失败;错误码：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            System.out.println("注册成功");
            // 遍历六个摄像头通道
            for(int iChannelNum = 33;iChannelNum <= 44; iChannelNum++){
                HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
                m_strClientInfo.lChannel = new NativeLong((long)iChannelNum);
                m_strClientInfo.lLinkMode = new NativeLong();
                m_strClientInfo.hPlayWnd = null;
                NativeLong lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID, m_strClientInfo, (HCNetSDK.FRealDataCallBack_V30)null, (Pointer)null, true);
                long previewSucValue = lPreviewHandle.longValue();
                if (previewSucValue >= 0L) {
                    System.out.println(iChannelNum + "通道预览成功");
                    String savePath = (String)map.get("savePath");
                    if (savePath.equals(".")) {
                        savePath = path;
                    }
                    savePath = ((new File(savePath + File.separator)).getAbsolutePath() + File.separator).replace("\\", "\\\\");
                    System.out.println(iChannelNum + "通道设备抓图--------");

                  //  String fileName=String.valueOf((int)(Math.random()*100+1));
                    String fileName=DateTime.now().toString("MMddHHmmss")
                    



                    boolean res2 = hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, new NativeLong((long)iChannelNum),JpegPara, savePath + "photo"+fileName+ (iChannelNum == 33 ? "" : iChannelNum - 33) +".jpg");
                    if (res2) {
                        System.out.println(iChannelNum + "通道抓取成功");
                    } else {
                        System.out.println(iChannelNum + "通道抓取失败；错误码：" + hCNetSDK.NET_DVR_GetLastError());
                    }
                }
                //关闭预览
                hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle);
                System.out.println(iChannelNum + "通道关闭预览");
            }
        }
        //注销用户、释放资源
        hCNetSDK.NET_DVR_Logout(lUserID);
        System.out.println("注销用户设备");
        hCNetSDK.NET_DVR_Cleanup();
        System.out.println("释放SDK资源");
    }
}
