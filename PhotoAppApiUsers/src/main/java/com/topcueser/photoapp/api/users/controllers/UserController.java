package com.topcueser.photoapp.api.users.controllers;

import com.topcueser.photoapp.api.users.shared.UserDto;
import com.topcueser.photoapp.api.users.models.CreateUserRequestModel;
import com.topcueser.photoapp.api.users.models.CreateUserResponseModel;
import com.topcueser.photoapp.api.users.services.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final Environment environment;
    private final UserService userService;

    public UsersController(Environment environment, UserService userService) {
        this.environment = environment;
        this.userService = userService;
    }

    @GetMapping("/status/check")
    public String status() {
        return "User Service Working on port: " + environment.getProperty("local.server.port");
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<CreateUserResponseModel>createUser(@RequestBody CreateUserRequestModel userRequestModel) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userRequestModel, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        CreateUserResponseModel userResponseModel = modelMapper.map(createdUser, CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseModel);
    }
}
