package com.project.shopapp.controller;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        try {
            if(result.hasErrors()){
                List<String> errorMessages =  result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages.toString());
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword()))
            {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password and retype password are not the same");
            }
            userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Register successfully");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO userLoginDTO ) {
        //Kiêểm tra thoong tin đăng nhap va sinh token
        //tra ve token trong response
       try
       {
           String token = userService.login(userLoginDTO.getPhoneNumber(),userLoginDTO.getPassword());
           return ResponseEntity.ok(token);
       }catch (Exception e)
       {
         return ResponseEntity.badRequest().body("Error: " + e.getMessage());
       }
    }
}
