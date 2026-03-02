package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.common.exception.BusinessException;
import com.mobilelearning.dto.TeacherRequest;
import com.mobilelearning.entity.EduTeacher;
import com.mobilelearning.entity.SysUser;
import com.mobilelearning.mapper.EduTeacherMapper;
import com.mobilelearning.service.EduTeacherService;
import com.mobilelearning.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
public class EduTeacherServiceImpl extends ServiceImpl<EduTeacherMapper, EduTeacher> implements EduTeacherService {

    private final SysUserService sysUserService;

    @Override
    public Page<EduTeacher> getTeacherList(Integer pageNum, Integer pageSize, String teacherNo) {
        Page<EduTeacher> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacher::getIsDeleted, 0);
        if (StringUtils.isNotBlank(teacherNo)) {
            wrapper.like(EduTeacher::getTeacherNo, teacherNo);
        }
        wrapper.orderByDesc(EduTeacher::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public void addTeacher(TeacherRequest request) {
        SysUser existUser = sysUserService.getByPhone(request.getPhone());
        
        if (existUser != null) {
            EduTeacher deletedTeacher = getByUserId(existUser.getId());
            if (deletedTeacher != null && deletedTeacher.getIsDeleted() == 1 
                    && request.getRealName().equals(existUser.getRealName())) {
                deletedTeacher.setIsDeleted(0);
                deletedTeacher.setTeacherNo(request.getTeacherNo());
                deletedTeacher.setAvatar(request.getAvatar());
                deletedTeacher.setIntro(request.getIntro());
                this.updateById(deletedTeacher);
                return;
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
        sysUserService.save(newUser);

        EduTeacher teacher = new EduTeacher();
        teacher.setUserId(newUser.getId());
        teacher.setTeacherNo(request.getTeacherNo());
        teacher.setAvatar(request.getAvatar());
        teacher.setIntro(request.getIntro());
        teacher.setIsDeleted(0);
        this.save(teacher);
    }

    @Override
    public void updateTeacher(TeacherRequest request) {
        EduTeacher teacher = this.getById(request.getId());
        if (teacher != null) {
            teacher.setTeacherNo(request.getTeacherNo());
            teacher.setAvatar(request.getAvatar());
            teacher.setIntro(request.getIntro());
            this.updateById(teacher);
        }
    }

    @Override
    public void deleteTeacher(Long id) {
        EduTeacher teacher = this.getById(id);
        if (teacher != null) {
            teacher.setIsDeleted(1);
            this.updateById(teacher);
        }
    }

    @Override
    public EduTeacher getByUserId(Long userId) {
        LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacher::getUserId, userId);
        return this.getOne(wrapper);
    }
}
