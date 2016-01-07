package com.example.jjamie.virtualadhoc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Broadcaster {

    private static final String TAG = "Broadcast";
    public static int PORT = 3333;

    public static void broadcast(Image image) {
        broadcastToNeighbor(image);
    }


    public static void broadcastToNeighbor(Image image) {
        Unicaster unicaster = new Unicaster();
        if (getNeighborList().size() == 0) return;
        for (int i = 0; i < getNeighborList().size(); i++) {
            unicaster.unicast(image, getNeighborList().get(i));
        }
    }

    public static ArrayList<String> getNeighborList() {
        ArrayList<String> clientList = new ArrayList<String>();
        int macCount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            System.out.println("-----------Neighbor list----------");
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        macCount++;
                        clientList.add(splitted[0]);
                        System.out.println("Count : " + macCount + " IP Address : " + splitted[0]);
                    }

                }
            }
        } catch (Exception e) {

        }
        return clientList;
    }

//    public String getIPAddressClient(){
//        DhcpInfo dhcp = mWifi.getDhcpInfo();
//        String address = Formatter.formatIpAddress(dhcp.gateway);
//        return address;
//    }


    public String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            System.out.println("WifiApIpAddress: " + inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

//    public String getIPAddressItSelf(){ //if it is hotspot, it will return 0.0.0.0
//        return Formatter.formatIpAddress(mWifi.getConnectionInfo().getIpAddress());
//    }

//    public void broadcast(Image image){
//        try {
//            System.out.println("BroadcastAddress: " + getBroadcastAddress());
//            if(image == null){
//                Log.d("Broadcast","No picture to broadcast");
//                return;
//            }
//
//            DatagramSocket socket = new DatagramSocket();
//            socket.setBroadcast(true);
//            DatagramPacket packet = new DatagramPacket(image.getBytes(), image.getBytes().length, getBroadcastAddress(), PORT);
//            socket.send(packet);
//
//        }catch(IOException ex){
//            Log.d("Broadcast",ex.toString());
//        }
//    }
//
//    private InetAddress getBroadcastAddress() throws IOException {
//        DhcpInfo dhcp = mWifi.getDhcpInfo();
//        if (dhcp == null) {
//            Log.d(TAG, "Could not get dhcp info");
//            return null;
//        }
//        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
//        byte[] quads = new byte[4];
//        for (int k = 0; k < 4; k++)
//            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
//        return InetAddress.getByAddress(quads);
//    }
}
