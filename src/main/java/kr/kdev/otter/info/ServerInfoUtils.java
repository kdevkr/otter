package kr.kdev.otter.info;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

import java.net.InetAddress;
import java.net.UnknownHostException;

@UtilityClass
public class ServerInfoUtils {

    @Getter
    private static final ServerInfo serverInfo;

    static {
        String os = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        String arch = System.getProperty("os.arch");
        String privateIp = "";
        try {
            privateIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
            // ignored
        }
        serverInfo = new ServerInfo()
                .setOs(os).setVersion(version).setArchitecture(arch)
                .setPrivateIp(privateIp);
    }

    @Accessors(chain = true)
    @Data
    public static class ServerInfo {
        private String os;
        private String version;
        private String architecture;
        private String privateIp;
    }
}
