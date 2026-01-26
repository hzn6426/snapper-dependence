/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomibing.authority.common;

import com.baomibing.authority.bo.EmailBO;
import com.baomibing.authority.bo.MailServerBo;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.email.listener.EmailSenderListener;
import com.baomibing.email.pool.MailThreadPool;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ConvertUtil;
import com.baomibing.tool.util.SnowflakeIdWorker;
import com.google.common.base.Splitter;
import jodd.mail.*;
import lombok.extern.slf4j.Slf4j;

import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.baomibing.tool.constant.NumberConstant.*;

@Slf4j
public class EmailSender {

    private static final String SMTP_PREFIX = "smtp.";


    public static void send2Email(List<EmailBO> emailList, MailServerBo bo) {
        if (Checker.beEmpty(emailList)) {
            throw new ServerRuntimeException(ExceptionEnum.NO_EMAIL_FOR_SEND);
        }
        if (emailList.size() > MAX_ONCE_SEND_MAIL_SIZE) {
            throw new ServerRuntimeException(ExceptionEnum.ONCE_SEND_EMAIL_NUMBER_TOO_LARGE, MAX_ONCE_SEND_MAIL_SIZE);
        }

        //init email 连接
        SmtpServer smtpServer = MailServer.create().host((Checker.beEmpty(bo.getProtocol()) ? SMTP_PREFIX : bo.getProtocol() + Strings.DOT) + bo.getHost())
                .auth(bo.getAddress(), bo.getPasswd())
                .ssl(true)
                .buildSmtpMailServer();
        //待发送邮件列表
        List<Email> sendEmailList = new ArrayList<>(emailList.size());

        for (EmailBO emailBo : emailList) {
            List<File> attchments = emailBo.getAttchments();
            if (Checker.beNotEmpty(attchments) && attchments.size() > MAX_FILE_NUMBER_IN_ONE_MAIL) {
                throw new ServerRuntimeException(ExceptionEnum.EMAIL_ATTCHMENT_NUMBER_TOO_LARGE, MAX_FILE_NUMBER_IN_ONE_MAIL);
            }

            //init email
            String clientId = SnowflakeIdWorker.getId();
            Email mail = new Email();
            try {
                mail.from(Checker.beNull(emailBo.getFrom()) ? new EmailAddress(bo.getFromTitle(), bo.getAddress()) : emailBo.getFrom()).to(emailBo.getTo())
                        .htmlMessage(emailBo.getMessage(), StandardCharsets.UTF_8.name())
                        .header("clientId", clientId).subject(MimeUtility.encodeText(emailBo.getSubject(), "UTF-8", "B"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (Checker.beNotEmpty(emailBo.getCc())) {
                String[] ccArray = ConvertUtil.toArray(Splitter.on(";").splitToList(emailBo.getCc()), String.class);
                mail.cc(ccArray);
            }


            if (Checker.beNotEmpty(attchments)) {
                for (File file : attchments) {
                    if (file.length() > MAX_MAIL_FILE_SIZE) {
                        throw new ServerRuntimeException(ExceptionEnum.EMAIL_ATTCHMENT_SIZE_TOO_LARGE, MAX_MAIL_FILE_SIZE / (1024 * 1024));
                    }
                    mail.embeddedAttachment(EmailAttachment.with().content(file));
                }
            }
            sendEmailList.add(mail);

        }
        //发送邮件
        MailThreadPool pool = new MailThreadPool();
        for (Email mail : sendEmailList) {
            pool.newMailSenderThread(smtpServer, mail, new EmailSenderListener() {
                @Override
                public void onException(Email mail, String filePath, MailException exception) {
                    log.error("email attchments download fail, subject is {}, from is {}, exception is {}", mail.subject(), mail.from(), exception.getMessage());
                }

                @Override
                public void onComplete(Email mail, String filePath) {
                    log.info("Email has sended....");
                }
            });
        }

    }

}
