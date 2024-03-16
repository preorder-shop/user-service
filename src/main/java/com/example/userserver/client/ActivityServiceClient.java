package com.example.userserver.client;

import com.example.userserver.domain.dto.response.GetFollowerRes;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "activity") // 호출하려는 마이크로 서비스의 이름
public interface ActivityServiceClient {

    @GetMapping("/activity/internal/follow/{userId}")
    List<GetFollowerRes> getFollowers(@PathVariable String userId);




}
