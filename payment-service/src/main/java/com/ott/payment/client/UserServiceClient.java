package com.ott.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ott.payment.dto.UserDto;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient{

	@GetMapping("/me/{userId}") // <-- THIS is what Feign cares about!
    //public ResponseEntity<?> getMe(@PathVariable Long userId); //original method
	public UserDto anyMethodNameForRND(@PathVariable Long userId); //method as per our requirement can change name and return type
}