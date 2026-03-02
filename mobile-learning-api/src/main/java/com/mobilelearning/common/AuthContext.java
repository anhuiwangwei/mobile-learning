package com.mobilelearning.common;

import com.mobilelearning.entity.SysUser;

public class AuthContext {

    private static final ThreadLocal<SysUser> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(SysUser user) {
        USER_HOLDER.set(user);
    }

    public static SysUser getUser() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        SysUser user = getUser();
        return user != null ? user.getId() : null;
    }

    public static String getRole() {
        SysUser user = getUser();
        return user != null ? user.getRole() : null;
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
