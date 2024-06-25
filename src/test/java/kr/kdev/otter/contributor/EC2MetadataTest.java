package kr.kdev.otter.contributor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.imds.Ec2MetadataResponse;

@Slf4j
@DisplayName("EC2 인스턴스 메타데이터 검색")
class EC2MetadataTest {

    @DisplayName("인스턴스 자격 증명 문서 파싱하기")
    @Test
    void TestParseInstanceIdentityDocument() {
        // NOTE: https://docs.aws.amazon.com/ko_kr/AWSEC2/latest/UserGuide/instance-identity-documents.html
        // given
        String body = """
                {
                    "accountId": "",
                    "architecture": "arm64",
                    "availabilityZone": "ap-northeast-2c",
                    "billingProducts": null,
                    "devpayProductCodes": null,
                    "marketplaceProductCodes": null,
                    "imageId": "ami-0331a5c9d849893dc",
                    "instanceId": "",
                    "instanceType": "c6g.xlarge",
                    "kernelId": null,
                    "pendingTime": "2024-06-23T01:01:23Z",
                    "privateIp": "192.169.44.196",
                    "ramdiskId": null,
                    "region": "ap-northeast-2",
                    "version": "2017-09-30"
                }
                """;

        // when
        Ec2MetadataResponse metadataResponse = Ec2MetadataResponse.create(body);

        // then
        Assertions.assertNotNull(metadataResponse);
        Assertions.assertNotNull(metadataResponse.asString());
        Assertions.assertNotNull(metadataResponse.asDocument().asMap());

        log.info("{}", metadataResponse.asDocument().asMap());
    }
}
