package com.esun.social_media.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

// @Component 讓 Spring 幫我們管理這個工具，可以在其他地方用 @Autowired 注入
@Component
public class JwtUtil {

    // 這是用來簽名手環的「最高機密金鑰」(實務上會放在屬性檔裡，這裡為了教學先寫在程式碼中)
    private static final String SECRET = "EsunBankSocialMediaSuperSecretKey2026";
    // Token 有效期限 (設定為 24 小時，單位為毫秒)
    private static final long EXPIRATION_TIME = 86400000;

    // 產生 JWT Token 的方法
    public String generateToken(Integer userId, String phoneNumber) {
        // 使用 HMAC256 演算法與我們的機密金鑰進行加密簽章
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        
        return JWT.create()
                // Payload (夾帶的資訊)：我們把使用者的 ID 和手機號碼放進去，方便後續辨識
                .withClaim("userId", userId)
                .withClaim("phone", phoneNumber)
                // 設定過期時間
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // 簽名並產生字串
                .sign(algorithm);
    }

    // 驗證 Token 並取得 userId
    public Integer validateTokenAndGetUserId(String token) {
        try {
            // 使用跟發放時一模一樣的演算法和金鑰
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            
            return JWT.require(algorithm)
                    .build()
                    // 驗證這個 token 是否有效（有沒有被篡改、有沒有過期）
                    .verify(token)
                    // 如果有效，就把我們當初塞進去的 userId 拿出來
                    .getClaim("userId").asInt();
        } catch (Exception e) {
            // 如果過期或被篡改，就會發生 Exception，我們回傳 null 代表驗證失敗
            return null;
        }
    }
}
