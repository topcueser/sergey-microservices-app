package com.topcueser.photoapp.api.users.ui.controllers;

import com.topcueser.photoapp.api.users.shared.UserDto;
import com.topcueser.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.topcueser.photoapp.api.users.ui.model.CreateUserResponseModel;
import com.topcueser.photoapp.api.users.ui.service.UsersService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    Environment environment;

    @Autowired
    UsersService usersService;

    @GetMapping("/status/check")
    public String status() {
        return "User Service Working on port: " + environment.getProperty("local.server.port");
    }

    @PostMapping
    public ResponseEntity<CreateUserResponseModel>createUser(@RequestBody CreateUserRequestModel userRequestModel) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userRequestModel, UserDto.class);

        UserDto createdUser = usersService.createUser(userDto);

        CreateUserResponseModel userResponseModel = modelMapper.map(createdUser, CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseModel);
    }
}
