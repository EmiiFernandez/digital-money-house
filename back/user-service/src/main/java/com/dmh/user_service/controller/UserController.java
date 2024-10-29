package com.dmh.user_service.controller;


import com.dmh.user_service.entity.User;
import com.dmh.user_service.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    // Endpoint para obtener un usuario por su ID
    @GetMapping("user/{id}")
    public User getById(@PathVariable String id) {
        User user = userService.findById(id);
        // Agregar log para verificar el usuario
        System.out.println("User returned: " + user);
        return user;
    }

    // Endpoint para obtener una lista de usuarios por su nombre
    @GetMapping("users/{name}")
    public List<User> getByName(@PathVariable String name) {
        return userService.findByName(name);
    }


    /*@PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RequestNewUser requestNewUser) {
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

    */
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


