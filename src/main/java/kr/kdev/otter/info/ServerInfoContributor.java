package kr.kdev.otter.info;

import com.amazonaws.util.EC2MetadataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerInfoContributor implements InfoContributor {
    private static final String SERVER = "server";
    private final boolean isRunningAwsEC2;

    public ServerInfoContributor() {
        isRunningAwsEC2 = EC2MetadataUtils.getInstanceId() != null;
    }

    @Override
    public void contribute(Info.Builder builder) {
        if (isRunningAwsEC2) {
            builder.withDetail(SERVER, EC2MetadataUtils.getInstanceInfo());
        } else {
            builder.withDetail(SERVER, ServerInfoUtils.getServerInfo());
        }
    }
}
