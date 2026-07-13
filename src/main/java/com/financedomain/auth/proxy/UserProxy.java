package com.financedomain.auth.proxy;

import com.financedomain.auth.dto.UserDto;
import com.financedomain.auth.proxy.fallback.UserProxyFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallback = UserProxyFallback.class)
public interface UserProxy {

    @GetMapping("/users/admin/username/{username}")
    ResponseEntity<UserDto> getAdminByUsername(@PathVariable("username") String username);

    @GetMapping("/users/client/number/{number}")
    ResponseEntity<UserDto> getClientByNumber(@PathVariable("number") String number);
}
