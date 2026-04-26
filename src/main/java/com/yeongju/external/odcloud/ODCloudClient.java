package com.yeongju.external.odcloud;

import com.yeongju.external.odcloud.dto.ODCloudResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ODCloud API Client
 * - 착한가격업소
 * - 지역사랑상품권
 * - 소수박물관 소장품
 */
@FeignClient(name = "odcloud", url = "${app.api.odcloud.base-url}")
public interface ODCloudClient {

    /**
     * 착한가격업소 조회
     */
    @GetMapping("/3079443/v1/uddi:2dd64b30-d069-4e2b-9938-ef5d3006c544_201705151432")
    ODCloudResponse getFairPriceStores(
            @RequestParam("page") Integer page,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("serviceKey") String serviceKey
    );

    /**
     * 지역사랑상품권 가맹점 조회
     */
    @GetMapping("/15149162/v1/uddi:d383a97e-3127-4ab0-9d44-deeb50511747")
    ODCloudResponse getGiftCertificateStores(
            @RequestParam("page") Integer page,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("serviceKey") String serviceKey
    );

    /**
     * 소수박물관 소장품 조회
     */
    @GetMapping("/3052016/v1/uddi:42c4c55b-225d-4c8f-985a-cdb61e02954c")
    ODCloudResponse getMuseumArtifacts(
            @RequestParam("page") Integer page,
            @RequestParam("perPage") Integer perPage,
            @RequestParam("serviceKey") String serviceKey
    );

}
