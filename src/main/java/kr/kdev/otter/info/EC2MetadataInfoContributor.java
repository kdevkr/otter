package kr.kdev.otter.info;

import com.amazonaws.util.EC2MetadataUtils;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.imds.Ec2MetadataClient;
import software.amazon.awssdk.imds.Ec2MetadataResponse;

@Component
public class EC2MetadataInfoContributor implements InfoContributor {
    private final boolean isRunningAwsEC2;

    public EC2MetadataInfoContributor() {
        isRunningAwsEC2 = EC2MetadataUtils.getInstanceId() != null;
    }

    @Override
    public void contribute(Info.Builder builder) {
        if (isRunningAwsEC2) {
            try (Ec2MetadataClient client = Ec2MetadataClient.create()) {
                Ec2MetadataResponse metadataResponse = client.get("/latest/dynamic/instance-identity/document");
                Document document = metadataResponse.asDocument();
                builder.withDetail("ec2", document.asMap());
            } catch (Exception ignored) {
                EC2MetadataUtils.InstanceInfo instanceInfo = EC2MetadataUtils.getInstanceInfo();
                builder.withDetail("ec2", instanceInfo);
            }
        }
    }
}
