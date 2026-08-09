package com.esun.social_media.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.esun.social_media.util.JwtUtil;

import java.io.IOException;
import java.util.ArrayList;

// @Component 讓 Spring 管理這個警衛
// 繼承 OncePerRequestFilter 確保每個 API 請求只會被攔截檢查一次
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. 從請求的 Header 中尋找名叫 "Authorization" 的欄位
        String authHeader = request.getHeader("Authorization");
        
        // 2. 檢查是否有帶 Token，而且標準的 JWT 格式通常會以 "Bearer " 開頭
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 把 "Bearer " 這 7 個字切掉，只留下真正的 Token 字串
            String token = authHeader.substring(7);
            
            // 3. 呼叫我們剛剛寫的 JwtUtil 來驗證 Token 並取得 userId
            Integer userId = jwtUtil.validateTokenAndGetUserId(token);
            
            // 4. 如果 userId 不是 null，代表手環是真的！
            if (userId != null) {
                // 發給他一張系統內部的通行證 (Authentication)
                // 我們把 userId 放在 Principal 的位置，這樣後續的 Controller 就能知道現在是誰在操作
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                
                // 把通行證交給 Spring Security 的安全中心 (SecurityContext) 保存
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // 5. 警衛檢查完畢，放行請求讓它繼續往下走 (可能走向註冊、登入，或是被 Security 擋下)
        filterChain.doFilter(request, response);
    }
}
