package com.cpy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cpy.reggie.common.R;
import com.cpy.reggie.entity.User;
import com.cpy.reggie.service.UserService;
import com.cpy.reggie.utils.SMSUtils;
import com.cpy.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 未使用的短信验证码接口，还未完全实现
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        log.info(user.toString());
//        获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            //        生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //        调用阿里云提供的Api  // 还没选择模板
//           方法参数第一个是签名，第二个是模板的code 然后是接收的手机号还有 发送的信息
            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);

//        将生成的验证码保存起来 session
            session.setAttribute("code",code);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody User user, HttpSession session){
        log.info(user.getPhone());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(user.getPhone()),User::getPhone,user.getPhone());
        User user1 = userService.getOne(queryWrapper);
        if (user1 == null) {
            user.setStatus(1); //设置了默认值
            user.setSex("男");
            user.setName("user");
            userService.save(user);
            session.setAttribute("user",user.getId());
            return R.success(user);
        }else {
            session.setAttribute("user",user1.getId());
            return R.success(user1);
        }
    }

    /**
     *  Request URL: http://localhost:8080/user/loginout
     *     Request Method: POST
     *     Status Code: 404
     *     Remote Address: [::1]:8080
     *     Referrer Policy: strict-origin-when-cross-origin
     * @return
     */
   @PostMapping("/logout")
    public R<String> logOut(HttpServletRequest request){
       HttpSession session = request.getSession();
//        删除session
       session.removeAttribute("user");
       return R.success("logout");
   }
}
