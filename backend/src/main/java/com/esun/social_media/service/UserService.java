package com.esun.social_media.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esun.social_media.dto.LoginRequest;
import com.esun.social_media.dto.RegisterRequest;
import com.esun.social_media.model.User;
import com.esun.social_media.repository.UserRepository;

import com.esun.social_media.util.JwtUtil; 

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 注入 JwtUtil
    @Autowired
    private JwtUtil jwtUtil;

    // @Transactional 確保發生錯誤時資料庫會 Rollback
    @Transactional
    public void register(RegisterRequest request) {
        // 1. 取得使用者輸入的明碼並加密 (Hash + Salt)
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // 2. 呼叫資料層的 Stored Procedure 寫入資料庫
        userRepository.registerUserUsingSP(
            request.getPhoneNumber(),
            request.getUserName(),
            request.getEmail(),
            hashedPassword
        );
    }

    // 登入邏輯
    public String login(LoginRequest request) {
        // 1. 透過手機號碼去資料庫找人
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber());
        
        // 2. 如果找不到這個人，代表帳號錯誤
        if (user == null) {
            throw new RuntimeException("找不到該使用者！");
        }
        
        // 3. 比對密碼 (將前端傳來的明碼 與 資料庫的亂碼 進行比對)
        // 注意：千萬不能自己寫明碼轉亂碼去比對，必須用 passwordEncoder.matches()
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密碼錯誤！");
        }
        
        // 4. 如果都正確，回傳成功訊息 (下一步我們會在這裡產生 JWT Token)
        //return "登入成功！歡迎回來，" + user.getUserName();

        // 登入成功後，呼叫 JwtUtil 產生專屬 Token 並回傳
        return jwtUtil.generateToken(user.getUserId(), user.getPhoneNumber());
    }
}
