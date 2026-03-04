package com.mobilelearning.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mobilelearning.common.AuthContext;
import com.mobilelearning.common.Result;
import com.mobilelearning.common.exception.BusinessException;
import com.mobilelearning.dto.*;
import com.mobilelearning.entity.*;
import com.mobilelearning.mapper.*;
import com.mobilelearning.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SysUserMapper sysUserMapper;
    private final EduTeacherMapper eduTeacherMapper;
    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @GetMapping("/info")
    public Result<LoginResponse> getAdminInfo() {
        LoginResponse response = new LoginResponse();
        response.setUserId(AuthContext.getUserId());
        response.setUsername(AuthContext.getUser().getUsername());
        response.setNickname(AuthContext.getUser().getNickname());
        response.setRealName(AuthContext.getUser().getRealName());
        response.setRole(AuthContext.getUser().getRole());
        return Result.success(response);
    }

    @GetMapping("/teacher/current")
    public Result<EduTeacher> getCurrentTeacher() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        
        LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacher::getUserId, userId);
        wrapper.eq(EduTeacher::getIsDeleted, 0);
        EduTeacher teacher = eduTeacherMapper.selectOne(wrapper);
        
        if (teacher != null) {
            return Result.success(teacher);
        } else {
            return Result.error("未找到关联的教师信息");
        }
    }

    @GetMapping("/user/list")
    public Result<Page<SysUser>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String role) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.isNotBlank(phone)) {
            wrapper.like(SysUser::getPhone, phone);
        }
        if (StringUtils.isNotBlank(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.eq(SysUser::getRole, "user");
        wrapper.orderByDesc(SysUser::getCreateTime);
        return Result.success(sysUserMapper.selectPage(page, wrapper));
    }

    @PutMapping("/user/status")
    public Result<Void> updateUserStatus(@RequestParam Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/user/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        sysUserMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/teacher/list")
    public Result<Page<EduTeacher>> getTeacherList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String teacherNo,
            @RequestParam(required = false) String realName) {
        Page<EduTeacher> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacher::getIsDeleted, 0);
        if (StringUtils.isNotBlank(teacherNo)) {
            wrapper.like(EduTeacher::getTeacherNo, teacherNo);
        }
        wrapper.orderByDesc(EduTeacher::getCreateTime);
        return Result.success(eduTeacherMapper.selectPage(page, wrapper));
    }

    @PostMapping("/teacher")
    public Result<Void> addTeacher(@RequestBody TeacherRequest request) {
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getPhone, request.getPhone());
        SysUser existUser = sysUserMapper.selectOne(userWrapper);

        Long userId;
        if (existUser != null) {
            LambdaQueryWrapper<EduTeacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(EduTeacher::getUserId, existUser.getId());
            teacherWrapper.eq(EduTeacher::getIsDeleted, 1);
            EduTeacher deletedTeacher = eduTeacherMapper.selectOne(teacherWrapper);
            
            if (deletedTeacher != null && request.getRealName().equals(existUser.getRealName())) {
                deletedTeacher.setIsDeleted(0);
                deletedTeacher.setTeacherNo(request.getTeacherNo());
                deletedTeacher.setAvatar(request.getAvatar());
                deletedTeacher.setIntro(request.getIntro());
                eduTeacherMapper.updateById(deletedTeacher);
                return Result.success();
            }
            throw new BusinessException("手机号已被其他用户使用");
        }

        SysUser newUser = new SysUser();
        newUser.setUsername(request.getPhone());
        newUser.setPhone(request.getPhone());
        newUser.setRealName(request.getRealName());
        newUser.setNickname(request.getRealName());
        newUser.setPassword(DigestUtils.md5DigestAsHex(("mobile-learning-123456").getBytes()));
        newUser.setRole("teacher");
        newUser.setStatus(1);
        sysUserMapper.insert(newUser);
        userId = newUser.getId();

        EduTeacher teacher = new EduTeacher();
        teacher.setUserId(userId);
        teacher.setTeacherNo(request.getTeacherNo());
        teacher.setAvatar(request.getAvatar());
        teacher.setIntro(request.getIntro());
        eduTeacherMapper.insert(teacher);

        return Result.success();
    }

    @PutMapping("/teacher")
    public Result<Void> updateTeacher(@RequestBody TeacherRequest request) {
        EduTeacher teacher = eduTeacherMapper.selectById(request.getId());
        if (teacher != null) {
            teacher.setTeacherNo(request.getTeacherNo());
            teacher.setAvatar(request.getAvatar());
            teacher.setIntro(request.getIntro());
            eduTeacherMapper.updateById(teacher);
        }
        return Result.success();
    }

    @DeleteMapping("/teacher/{id}")
    public Result<Void> deleteTeacher(@PathVariable Long id) {
        EduTeacher teacher = eduTeacherMapper.selectById(id);
        if (teacher != null) {
            teacher.setIsDeleted(1);
            eduTeacherMapper.updateById(teacher);
        }
        return Result.success();
    }
}
