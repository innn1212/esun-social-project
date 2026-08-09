package com.esun.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.esun.social_media.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    // 註冊用的 Stored Procedure
    // @Modifying 代表這是一個會異動資料(INSERT/UPDATE/DELETE)的操作
    @Modifying
    // nativeQuery = true 代表我們直接寫原生的 MySQL 語法來呼叫 Stored Procedure
    @Query(value = "CALL SP_RegisterUser(:phone, :name, :email, :password)", nativeQuery = true)
    void registerUserUsingSP(
        @Param("phone") String phone, 
        @Param("name") String name, 
        @Param("email") String email, 
        @Param("password") String password
    );

    // 【新增這行】透過手機號碼尋找使用者
    User findByPhoneNumber(String phoneNumber);
}
