package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    Page<SysUser> getUserList(Integer pageNum, Integer pageSize, String username, String phone, String role);
    void updateUserStatus(Long id, Integer status);
    void deleteUser(Long id);
    SysUser getByPhone(String phone);
}