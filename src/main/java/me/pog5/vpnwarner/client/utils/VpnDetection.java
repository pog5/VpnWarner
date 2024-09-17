package me.pog5.vpnwarner.client.utils;

import me.pog5.vpnwarner.client.VpnwarnerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for VPN detection.
 */
public final class VpnDetection {
    private static final Set<String> VPN_NAMES;
    private static final Pattern VPN_REGEX_PATTERN;
    private static final Set<String> WHITELISTED_VPN_NAMES;

    static {
        VPN_NAMES = new HashSet<>(Arrays.asList(
                "openvpn", "nordvpn", "expressvpn", "tunnelbear", "windscribe", "protonvpn", "cyberghost", "surfshark",
                "vyprvpn", "ipvanish", "hidemyass", "privateinternetaccess", "purevpn", "strongvpn", "hotspotshield",
                "privatevpn", "torguard", "avastsecureline", "avgsecurevpn", "avirasecureline", "bitdefenderpremiumvpn",
                "bullguardvpn", "ciscovpn", "fsecurefreedom", "kasperskysecureconnection", "mcafeesafeconnect",
                "nortonsecurevpn", "pandasecurity", "totalavpn", "webrootwifi", "zenmate", "hideallip", "exitlag",
                "wireguard", "cloudflarewarp", "mullvad", "ivpn", "mozillavpn", "azirevpn", "airvpn", "privadovpn",
                "speedify", "atlasvpn", "surfeasy", "wevpn", "malwarebytesprivacyvpn", "ghostpath", "vpnunlimited",
                "vpnbook", "safenetvpn", "xvpn", "urbanvpn", "supervpn", "rocketvpn", "internetprivacynow",
                "cryptostorm", "perfectprivacy", "shellfire", "hola", "betternet", "safetynet", "vpnsecure", "upvpn",
                "vpnht", "unlocator", "ibvpn", "strongswan", "onetunnel", "fastestvpn", "tapvpn", "vpnmonster",
                "freevpn", "hideman", "vpnify", "vpn360", "vpnhub", "browsec", "dashvpn"
        ));

        WHITELISTED_VPN_NAMES = new HashSet<>(Arrays.asList(
                "exitlagpmservice"
        ));

        // Build the regex pattern from VPN_NAMES
        StringBuilder regexBuilder = new StringBuilder();
        for (String vpnName : VPN_NAMES) {
            if (!regexBuilder.isEmpty()) {
                regexBuilder.append("|");
            }
            regexBuilder.append(Pattern.quote(vpnName));
        }
        VPN_REGEX_PATTERN = Pattern.compile(regexBuilder.toString(), Pattern.CASE_INSENSITIVE);
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
                Matcher matcher = VPN_REGEX_PATTERN.matcher(line);
                if (matcher.find()) {
                    String detectedVpn = matcher.group();
                    if (WHITELISTED_VPN_NAMES.contains(detectedVpn)) {
                        continue;
                    }
                    VpnwarnerClient.DETECTED_VPN = VpnwarnerClient.DETECTED_VPN.concat(detectedVpn + " (Process), ");
                }
            }
        }
        return !VpnwarnerClient.DETECTED_VPN.isEmpty();
    }

    private static boolean isOpenVPNConnected() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = networkInterfaces.nextElement();
                String name = netInterface.getName();
                String displayName = netInterface.getDisplayName();
                if (!netInterface.isUp())
                    continue;


                // Check for typical VPN interface names
                if (name != null && (name.toLowerCase().contains("tun") || name.toLowerCase().contains("tap") || name.toLowerCase().contains("vpn"))) {
                    VpnwarnerClient.DETECTED_VPN = VpnwarnerClient.DETECTED_VPN.concat(name + " (OpenVPN), ");
                }

                if (displayName != null && (displayName.toLowerCase().contains("tun") || displayName.toLowerCase().contains("tap") || displayName.toLowerCase().contains("vpn"))) {
                    VpnwarnerClient.DETECTED_VPN = VpnwarnerClient.DETECTED_VPN.concat(displayName + " (OpenVPN), ");
                }
            }

            return !VpnwarnerClient.DETECTED_VPN.contains("OpenVPN");

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }
}