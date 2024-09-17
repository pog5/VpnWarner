package me.pog5.vpnwarner.client.utils;

import me.pog5.vpnwarner.client.VpnwarnerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Utility class for VPN detection.
 */
public final class VpnDetection {
    private static final Set<String> VPN_NAMES;

    static {
        VPN_NAMES = new HashSet<>(Arrays.asList(
                "openvpn", "nordvpn", "expressvpn", "tunnelbear",
                "windscribe", "protonvpn", "cyberghost", "surfshark",
                "vyprvpn", "ipvanish", "hidemyass", "privateinternetaccess",
                "purevpn", "strongvpn", "hotspotshield", "privatevpn",
                "torguard", "avastsecureline", "avgsecurevpn", "avirasecureline",
                "bitdefenderpremiumvpn", "bullguardvpn", "ciscovpn", "fsecurefreedom",
                "kasperskysecureconnection", "mcafeesafeconnect", "nortonsecurevpn", "pandasecurity",
                "totalavpn", "webrootwifi", "zenmate", "hideallip",
                "exitlag", "wireguard", "cloudflarewarp", "mullvad",
                "ivpn", "mozillavpn", "azirevpn", "airvpn",
                "privadovpn", "speedify", "atlasvpn", "surfeasy",
                "wevpn", "malwarebytesprivacyvpn", "ghostpath", "vpnunlimited",
                "vpnbook", "safenetvpn", "xvpn", "urbanvpn",
                "supervpn", "rocketvpn", "internetprivacynow",
                "cryptostorm", "perfectprivacy", "shellfire", "hola",
                "betternet", "safetynet", "vpnsecure", "upvpn",
                "vpnht", "unlocator", "ibvpn", "strongswan",
                "onetunnel", "fastestvpn", "tapvpn", "vpnmonster",
                "freevpn", "hideman", "vpnify", "vpn360",
                "vpnhub", "browsec", "dashvpn", "hma", "pia"
        ));
    }

    private VpnDetection() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static boolean isVpnEnabled() {
        return checkRunningProcessesOrServices() || isOpenVPNConnected();
    }

    private static boolean checkRunningProcessesOrServices() {
        try {
            String osName = System.getProperty("os.name").toLowerCase(Locale.US);
            List<String> commands = new ArrayList<>();

            if (osName.contains("win")) {
                commands.add("tasklist"); // List running processes
                commands.add("sc query"); // List services
            } else {
                commands.add("ps -e"); // List running processes
                commands.add("systemctl list-units --type=service --all"); // List services
            }

            for (String command : commands) {
                if (isVpnProcessRunning(command)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isVpnProcessRunning(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String normalizedLine = line.toLowerCase(Locale.US);
                for (String vpnName : VPN_NAMES) {
                    if (normalizedLine.contains(vpnName)) {
                        VpnwarnerClient.DETECTED_VPN = vpnName;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isOpenVPNConnected() {
        try {
            boolean isConnectedViaOpenVPN = false;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = networkInterfaces.nextElement();
                String name = netInterface.getName();
                String displayName = netInterface.getDisplayName();

                // Debug output to see interface names
//                System.out.println("Interface Name: " + name);
//                System.out.println("Display Name: " + displayName);

                // Check for typical VPN interface names
                if (name != null && (name.toLowerCase().contains("tun") || name.toLowerCase().contains("tap") || name.toLowerCase().contains("vpn"))) {
                    System.out.println("Potential VPN interface found: " + name);
                    VpnwarnerClient.DETECTED_VPN = name + " (OpenVPN)";
                    isConnectedViaOpenVPN = true;
                    break;
                }

                if (displayName != null && (displayName.toLowerCase().contains("tun") || displayName.toLowerCase().contains("tap") || displayName.toLowerCase().contains("vpn"))) {
                    System.out.println("Potential VPN interface found: " + displayName);
                    VpnwarnerClient.DETECTED_VPN = displayName + " (OpenVPN)";
                    isConnectedViaOpenVPN = true;
                    break;
                }
            }

            return isConnectedViaOpenVPN;

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }
}