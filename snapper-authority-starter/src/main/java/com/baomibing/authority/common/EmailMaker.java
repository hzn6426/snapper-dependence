/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.common;

import com.baomibing.authority.bo.EmailBO;
import com.baomibing.authority.dto.UserDto;
import com.google.common.collect.Lists;


/**
 * 邮件构建器，用于构建与供应商交互的邮件
 *
 * @author zening
 * @version 1.0.0
 * @date 2018年9月6日 上午9:22:38
 */
public class EmailMaker {

    /**
     * 构建审核通过邮件，主题和内容都固定
     *
     * @return
     */
    public static EmailBO makeUserActiveEmail(UserDto user) {
        String title = user.getTitilePrefix() + "服务平台 - 激活成功！";
        String messageBuilder = "<html><META http-equiv=Content-Type content=\"text/html; " +
                "charset=utf-8\"><body>" +
                "<table width='900px' style='margin: 0 auto'><tbody>" +
                "<tr><td style='height: 10px;width:100%;padding:0;margin: 0 auto;background: #383D41;'></td></tr>" +
                "<tr style=\"min-height: 240px;width: 100%;margin-bottom: 20px;\">" +
                "<td style=\"padding: 26px 46px;border: 1px #ECECEC solid;position: relative;\"> <span style=\"width: 8px;height: 35px;display: table-cell;background: #00C6FF;position: absolute;left: 30px;\"></span>" +
                "<h3 style=\"display: table-cell;margin: 0;padding: 0;font-size: 20px;line-height: 30px;\"> 恭喜您，成为" + user.getTitilePrefix() + "平台用户！ </h3>" +
                "<p style=\"margin: 10px 30px;padding: 0;font-size: 20px;\"> 账 号：" + user.getUserName() + " </p>" +
                "<p style=\"margin: 10px 30px;padding: 0;font-size: 20px;\"> 密 码：" + user.getUnencryptPassword() + " </p>" +
                "<p style=\"margin: 40px 30px;padding: 0;font-size: 14px;\"> " + user.getTitilePrefix() + "平台网址：<a href=\"" + user.getWebsiteUrl() + "\" target=\"_blank\" rel=\"noopener\">" + user.getWebsiteUrl() + "</a> </p>" +
                "<a style=\"cursor: pointer;outline: none;text-decoration: none;width: 134px;height: 40px;margin-top: 36px;line-height: 40px;text-align:center;display: inline-block;background: #00C6FF;"
                + "color: #fff;font-weight: 450;\" href=\"" + user.getWebsiteUrl() + "\"  target=\"_blank\" rel=\"noopener\"> 马上登录 </a>" +
                "</td></tr></tbody></table></body></html>";
        EmailBO email = new EmailBO();
        email.setSubject(title).setFrom(null).setTo(user.getUserEmail()).setAttchments(Lists.newArrayList()).setMessage(messageBuilder);
//		if (Checker.BeNotEmpty(cc)) {
//			email.setCc(cc);
//		}
        return email;
    }

    public static EmailBO makeUserResetPasswdEmail(UserDto user) {
        String title = user.getTitilePrefix() + "服务平台 - 密码重置成功！";
        String messageBuilder = "<html><META http-equiv=Content-Type content=\"text/html; " +
                "charset=utf-8\"><body>" +
                "<table width='900px' style='margin: 0 auto'><tbody>" +
                "<tr><td style='height: 10px;width:100%;padding:0;margin: 0 auto;background: #383D41;'></td></tr>" +
                "<tr style=\"min-height: 240px;width: 100%;margin-bottom: 20px;\">" +
                "<td style=\"padding: 26px 46px;border: 1px #ECECEC solid;position: relative;\"> <span style=\"width: 8px;height: 35px;display: table-cell;background: #00C6FF;position: absolute;left: 30px;\"></span>" +
                "<h3 style=\"display: table-cell;margin: 0;padding: 0;font-size: 20px;line-height: 30px;\"> 您的密码已被重置，具体如下： </h3>" +
                "<p style=\"margin: 10px 30px;padding: 0;font-size: 20px;\"> 账 号：" + user.getUserName() + " </p>" +
                "<p style=\"margin: 10px 30px;padding: 0;font-size: 20px;\"> 密 码：" + user.getUnencryptPassword() + " </p>" +
                "<p style=\"margin: 40px 30px;padding: 0;font-size: 14px;\"> " + user.getTitilePrefix() + "平台网址：<a href=\"" + user.getWebsiteUrl() + "\" target=\"_blank\" rel=\"noopener\">" + user.getWebsiteUrl() + "</a> </p>" +
                "<a style=\"cursor: pointer;outline: none;text-decoration: none;width: 134px;height: 40px;margin-top: 36px;line-height: 40px;text-align:center;display: inline-block;background: #00C6FF;"
                + "color: #fff;font-weight: 450;\" href=\"" + user.getWebsiteUrl() + "\"  target=\"_blank\" rel=\"noopener\"> 马上登录 </a>" +
                "</td></tr></tbody></table></body></html>";
        EmailBO email = new EmailBO();
        email.setSubject(title).setFrom(null).setTo(user.getUserEmail()).setAttchments(Lists.newArrayList()).setMessage(messageBuilder);
//		if (Checker.BeNotEmpty(cc)) {
//			email.setCc(cc);
//		}
        return email;
    }

    public static EmailBO makeUserValidateEmail(String emailAddress, String titilePrefix, String websiteUrl) {
        String title = titilePrefix + "服务平台 - 测试邮件！";
        String messageBuilder = "<html><META http-equiv=Content-Type content=\"text/html; " +
                "charset=utf-8\"><body>" +
                "<table width='900px' style='margin: 0 auto'><tbody>" +
                "<tr><td style='height: 10px;width:100%;padding:0;margin: 0 auto;background: #383D41;'></td></tr>" +
                "<tr style=\"min-height: 240px;width: 100%;margin-bottom: 20px;\">" +
                "<td style=\"padding: 26px 46px;border: 1px #ECECEC solid;position: relative;\"> <span style=\"width: 8px;height: 35px;display: table-cell;background: #00C6FF;position: absolute;left: 30px;\"></span>" +
                "<h3 style=\"display: table-cell;margin: 0;padding: 0;font-size: 20px;line-height: 30px;\"> 这是一封测试邮件,收到邮件时证明您在" + titilePrefix + "服务平台的邮箱已配置成功! 请勿回复!： </h3>" +
                "<p style=\"margin: 40px 30px;padding: 0;font-size: 14px;\"> " + titilePrefix + "平台网址：<a href=\"" + websiteUrl + "\" target=\"_blank\" rel=\"noopener\">" + websiteUrl + "</a> </p>" +
                "<a style=\"cursor: pointer;outline: none;text-decoration: none;width: 134px;height: 40px;margin-top: 36px;line-height: 40px;text-align:center;display: inline-block;background: #00C6FF;"
                + "color: #fff;font-weight: 450;\" href=\"" + websiteUrl + "\" target=\"_blank\" rel=\"noopener\"> 马上登录 </a>" +
                "</td></tr></tbody></table></body></html>";
        EmailBO email = new EmailBO();
        email.setSubject(title).setFrom(null).setTo(emailAddress).setAttchments(Lists.newArrayList()).setMessage(messageBuilder);
//		if (Checker.BeNotEmpty(cc)) {
//			email.setCc(cc);
//		}
        return email;
    }

}
