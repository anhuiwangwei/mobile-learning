package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mobilelearning.entity.SysUser;
import com.mobilelearning.mapper.SysUserMapper;
import com.mobilelearning.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SysUserServiceTest {

    @Autowired
    private SysUserService sysUserService;

    @Test
    public void testGetUserList() {
        Page<SysUser> page = sysUserService.getUserList(1, 10, null, null, "user");
        assertNotNull(page);
        assertTrue(page.getRecords().size() >= 0);
    }

    @Test
    public void testGetByPhone() {
        SysUser user = sysUserService.getByPhone("13800138000");
        assertNotNull(user);
        assertEquals("13800138000", user.getPhone());
    }

    @Test
    public void testUpdateUserStatus() {
        Long userId = 3L;
        sysUserService.updateUserStatus(userId, 0);
        SysUser user = sysUserService.getById(userId);
        assertEquals(0, user.getStatus());
        
        sysUserService.updateUserStatus(userId, 1);
        user = sysUserService.getById(userId);
        assertEquals(1, user.getStatus());
    }
}