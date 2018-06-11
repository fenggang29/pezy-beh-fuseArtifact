package com.pezy.datafuse.license.license3j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;


public class LOCALMAC {
    /**
     * @param args
     * @throws UnknownHostException
     * @throws SocketException
     */
    public static void main(String[] args) throws UnknownHostException, SocketException {

        System.out.println(GetAddressMac());
    /*	try {
			getAllMac();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

    public static String GetAddressMac() {

        InetAddress lanIp = null;
        List<InetAddress> ipS = new ArrayList<InetAddress>();
        try {

            String ipAddress = null;
            Enumeration<NetworkInterface> net = null;
            net = NetworkInterface.getNetworkInterfaces();

            while (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();

                while (addresses.hasMoreElements()/* && !isVMMac(element.getHardwareAddress())*/) {
                    InetAddress ip = addresses.nextElement();
                    ipAddress = ip.getHostAddress();
                    lanIp = InetAddress.getByName(ipAddress);
                    if (lanIp != null && !"".equals(lanIp)) {
                        ipS.add(lanIp);
                    }
                }
            }

            Set<String> setStr = new HashSet<String>();
            for (InetAddress tempLan : ipS) {
                setStr.add(getMacAddress(tempLan));
            }
            return setStr.toString();


        } catch (UnknownHostException ex) {

            ex.printStackTrace();

        } catch (SocketException ex) {

            ex.printStackTrace();

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        return "";

    }

    private static String getMacAddress(InetAddress ip) {
        String address = null;
        try {

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            if( mac == null ) return address;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            address = sb.toString();

        } catch (SocketException ex) {

            ex.printStackTrace();

        }

        return address;
    }

}