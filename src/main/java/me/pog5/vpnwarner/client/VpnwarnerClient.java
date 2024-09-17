package me.pog5.vpnwarner.client;

import net.fabricmc.api.ClientModInitializer;

public class VpnwarnerClient implements ClientModInitializer {
    public static boolean DISMISSED_WARNING = false;
    public static String DETECTED_VPN = "";

    @Override
    public void onInitializeClient() {
    }
}
