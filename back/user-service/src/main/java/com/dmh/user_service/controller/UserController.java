package com.dmh.user_service.controller;

import com.dmh.user_service.dto.ResponseNewUser;
import com.dmh.user_service.dto.RequestNewUser;
import com.dmh.user_service.entity.User;
import com.dmh.user_service.exceptions.ErrorResponse;
import com.dmh.user_service.exceptions.ResourceNotFoundException;
import com.dmh.user_service.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RequestNewUser requestNewUser) {
        ResponseNewUser responseNewUser = userService.createUser(requestNewUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseNewUser);
    }


    @GetMapping("/{user_id}")
    public ResponseEntity<?> getUserById(@PathVariable("user_id") Integer user_id) {
        Optional<User> user = userService.getUserById(user_id);

        return ResponseEntity.ok(user);
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


