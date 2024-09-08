package com.dmh.auth_service.controller;

import com.dmh.auth_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    


    private final RoleService roleService;


    @PutMapping("/assign/users/{userId}")
    public ResponseEntity<?> assignRole(@PathVariable String userId, @RequestParam String roleName) {

        roleService.assignRole(userId, roleName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @DeleteMapping("/remove/users/{userId}")
    public ResponseEntity<?> unAssignRole(@PathVariable String userId, @RequestParam String roleName) {

        roleService.deleteRoleFromUser(userId, roleName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

