package com.dmh.user_service.controller;


import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.dto.RequestUpdateUser;
import com.dmh.user_service.dto.ResponseGetUser;
import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody RequestNewUser requestNewUser) {
        ResponseNewUser responseNewUser = userService.createUser(requestNewUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseNewUser);
    }


    @GetMapping("/{user_id}")
    public ResponseEntity<?> getUserById(@PathVariable("user_id") Integer user_id) {
        ResponseGetUser user = userService.getUserById(user_id);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<ResponseGetUser> updateUser(
            @PathVariable Integer user_id,
            @RequestBody RequestUpdateUser updateUserRequest) {
        ResponseGetUser updatedUser = userService.updateUser(user_id, updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

}

/*
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
            List<RequestNewUser> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
    }*/

   /* @PatchMapping("/{id}")
    public ResponseEntity<RequestNewUser> updateUser(@PathVariable Integer id, @RequestBody RequestNewUser userDTO, @RequestHeader("Authorization") String token) {
        RequestNewUser updatedUser = userService.updateUser(id, userDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }*/


