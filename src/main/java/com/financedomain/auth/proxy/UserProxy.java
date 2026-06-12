package com.financedomain.auth.proxy;

import com.financedomain.auth.proxy.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserProxy {

    @GetMapping("/users/admin/username/{username}")
    ResponseEntity<UserDto> getAdminByUsername(@PathVariable("username") String username);

    @GetMapping("/users/client/number/{number}")
    ResponseEntity<UserDto> getClientByNumber(@PathVariable("number") String number);
}
