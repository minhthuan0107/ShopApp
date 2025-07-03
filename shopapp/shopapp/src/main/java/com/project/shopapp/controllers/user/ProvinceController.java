package com.project.shopapp.controllers.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("${api.prefix}/provinces")
public class ProvinceController {

    @GetMapping
    public ResponseEntity<?> getAllProvinces() {
        String url = "https://provinces.open-api.vn/api/?depth=1";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/p/{provinceCode}")
    public ResponseEntity<?> getProvinceWithDistricts(@PathVariable String provinceCode) {
        String url = "https://provinces.open-api.vn/api/p/" + provinceCode + "?depth=2";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/d/{districtCode}")
    public ResponseEntity<?> getDistrictWithWards(@PathVariable String districtCode) {
        String url = "https://provinces.open-api.vn/api/d/" + districtCode + "?depth=2";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response.getBody());
    }
}
