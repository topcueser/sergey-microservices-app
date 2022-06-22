package com.topcueser.photoapp.api.users.ui.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.topcueser.photoapp.api.users.shared.UserDto;

//public interface UsersService extends UserDetailsService{
public interface UsersService {
    UserDto createUser(UserDto userDto);
//    UserDto getUserDetailsByEmail(String email);
}
