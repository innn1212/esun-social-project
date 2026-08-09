package com.esun.social_media.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.esun.social_media.dto.LoginRequest;
import com.esun.social_media.dto.RegisterRequest;
import com.esun.social_media.service.UserService;

import java.util.Map;
import java.util.HashMap;

// @RestController 代表這是一個提供 RESTful API 的控制器
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 建立註冊 API，Method 為 POST
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok("註冊成功！");
        } catch (Exception e) {
            // 如果手機號碼重複或發生其他錯誤，會進入這裡
            return ResponseEntity.badRequest().body("註冊失敗：" + e.getMessage());
        }
    }

    /* 
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            String message = userService.login(request);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("登入失敗：" + e.getMessage());
        }
    }
    */

    @PostMapping("/login")
    // 回傳型別從 String 改成 Map<String, String>，Spring 會自動把它轉成 JSON
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            // 拿到 Token
            String token = userService.login(request);
            // 放進 Map 裡，標籤叫 "token"
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "登入失敗：" + e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }
}