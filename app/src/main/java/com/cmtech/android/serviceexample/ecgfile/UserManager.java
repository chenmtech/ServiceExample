package com.cmtech.android.serviceexample.ecgfile;


import com.vise.log.ViseLog;

import org.litepal.LitePal;

import java.util.List;


/**
  *
  * ClassName:      UserManager
  * Description:    用户管理器
  * Author:         chenm
  * CreateDate:     2018/10/27 上午4:01
  * UpdateUser:     chenm
  * UpdateDate:     2019/4/20 上午4:01
  * UpdateRemark:   更新说明
  * Version:        1.0
 */

public class UserManager {
    private static UserManager instance; //用户管理器
    private User user; // 用户

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    // 是否已经登录
    public boolean isSignIn() {
        return user != null;
    }
    // 退出账号
    public void signOut() {
        user = null;
    }

    // 注册新账户
    public boolean signUp(String phone) {
        List<User> find = LitePal.where("phone = ?", phone).find(User.class);

        if(find != null && find.size() > 0) {
            ViseLog.e("The user account exists.");
            return false;
        } else {
            user = new User();
            user.setPhone(phone);
            user.save();
            return true;
        }
    }

    // 登录
    public boolean signIn(String phone) {
        List<User> find = LitePal.where("phone = ?", phone).find(User.class);
        if(find != null && find.size() == 1) {
            user = find.get(0);
            if(user.getName() == null) {
                user.setName("");
            }
            if(user.getPortraitPath() == null) {
                user.setPortraitPath("");
            }
            if(user.getPersonalInfo() == null) {
                user.setPersonalInfo("");
            }
            user.save();
            return true;
        } else {
            return false;
        }
    }

}
