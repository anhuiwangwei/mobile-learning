package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobilelearning.common.exception.BusinessException;
import com.mobilelearning.dto.LoginRequest;
import com.mobilelearning.dto.LoginResponse;
import com.mobilelearning.dto.RegisterRequest;
import com.mobilelearning.entity.SysUser;
import com.mobilelearning.mapper.SysUserMapper;
import com.mobilelearning.service.AuthService;
import com.mobilelearning.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
            throw new BusinessException("用户名和密码不能为空");
        }

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        String encryptPassword = encryptPassword(request.getPassword());
        if (!encryptPassword.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        response.setToken(token);

        return response;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        if (StringUtils.isBlank(request.getPhone()) || StringUtils.isBlank(request.getRealName())
                || StringUtils.isBlank(request.getPassword())) {
            throw new BusinessException("手机号、姓名、密码不能为空");
        }

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, request.getPhone());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("手机号已被注册");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getPhone());
        user.setPhone(request.getPhone());
        user.setRealName(request.getRealName());
        user.setNickname(request.getRealName());
        user.setPassword(encryptPassword(request.getPassword()));
        user.setRole("user");
        user.setStatus(1);

        sysUserMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        response.setToken(token);

        return response;
    }

    @Override
    public void logout() {
    }

    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(("mobile-learning-" + password).getBytes());
    }
}
