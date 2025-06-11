/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.bootstrap;


import com.baomibing.email.listener.EmailReceiverListener;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import jodd.mail.EmailFilter;
import jodd.mail.MailException;
import jodd.mail.ReceivedEmail;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;


/**
 * 收件箱对象，提供收件箱的各种操作，基于email-imap协议
 * @author zening
 * @date 2018年6月7日 下午4:50:29
 * @version 1.0.0
 */
public class EmailReceiveMonitor {
	
	private String username;
	private String password;
	private String protocol;
	private String host;
	private String port;
 
	public EmailReceiveMonitor(String protocol, String host,
                               String port, String username, String password) {
		this.username = username;
		this.password = password;
		this.protocol = protocol;
		this.host = host;
		this.port = port;
	}
	
	
	private Properties getServerProperties() {
        Properties properties = new Properties();
 
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
        properties.put("mail.imap.partialfetch", false);//imap bug，不添加这条取inputstream有时候取不到
        // SSL setting
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));
 
        return properties;
    }
    
    /**
     * 关闭监控
     */
    public void killMonitor() {
    	if (monitorThread != null) {
    		monitorThread.kill();
    	}
    }
    
    private IdleThread monitorThread = null;
    
    /**
     * 监控收件箱,收到邮件,匹配收件人为<code>from</code>,触发listener事件，并将邮件标记为已读。收件人为空或null，不进行匹配
     * @param emailFilter 过滤器
     * @param listener 监控器
     * 
     */
    public void monitorInbox(EmailFilter emailFilter, EmailReceiverListener listener) {
    	if (listener == null) return;
    	Session session = Session.getInstance(getServerProperties());
    	IMAPStore store = null;
		Folder inbox = null;
		try {
			store = (IMAPStore) session.getStore(protocol);
			store.connect(username, password);
			if (!store.hasCapability("IDLE")) {
				throw new MessagingException("IDLE not supported,Can not monitor the INBOX.");
			}

			inbox = (IMAPFolder) store.getFolder("INBOX");
			inbox.addMessageCountListener(new MessageCountAdapter() {

				@Override
				public void messagesAdded(MessageCountEvent event) {
					Message[] messages = event.getMessages();
					List<ReceivedEmail> mailList = new ArrayList<>();
					try {
						for (Message message : messages) {
							if(emailFilter != null && !message.match(emailFilter.getSearchTerm())) {
								continue;
							}
							mailList.add(new ReceivedEmail(message, false, null));
						}
						listener.receiveMessages(mailList);
					} catch (MessagingException e) {
						MailException exception = new MailException(e);
						listener.onReceiveException(exception);
					}
					
				}
			});

			monitorThread = new IdleThread(inbox);
			monitorThread.setDaemon(false);
			monitorThread.start();

			monitorThread.join();
			// idleThread.kill(); //to terminate from another thread

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			close(inbox);
			close(store);
		}
    }
 
    
    private static String wrapPattern(String text) {
    	 Pattern encodeStringPattern = Pattern.compile("=\\?(.+)\\?(B|Q)\\?(.+)\\?=" + text, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    	 return encodeStringPattern.pattern();
    }
    /**
     * Test monitor e-mail messages
     */
//    public static void main(String[] args) {
//
//    	String protocol = "imap";
//      String host = "imap.exmail.qq.com";
//      String port = "993";
//      String username = "IC-receiver@canaan-creative.com";
//      String password = "ICreceiver2018";
//      Properties properties = new Properties();
//
//      // server setting
//      properties.put(String.format("mail.%s.host", protocol), host);
//      properties.put(String.format("mail.%s.port", protocol), port);
//      properties.put("mail.imap.partialfetch", false);//imap bug，不添加这条取inputstream有时候取不到
//      // SSL setting
//      properties.setProperty(
//              String.format("mail.%s.socketFactory.class", protocol),
//              "javax.net.ssl.SSLSocketFactory");
//      properties.setProperty(
//              String.format("mail.%s.socketFactory.fallback", protocol),
//              "false");
//      properties.setProperty(
//              String.format("mail.%s.socketFactory.port", protocol),
//              String.valueOf(port));
//		Session session = Session.getInstance(properties);
//		IMAPStore store = null;
//		Folder inbox = null;
//		try {
//			store = (IMAPStore) session.getStore(protocol);
//			store.connect(username, password);
//			if (!store.hasCapability("IDLE")) {
//				throw new MessagingException("IDLE not supported,Can not monitor the INBOX.");
//			}
//
//			inbox = (IMAPFolder) store.getFolder("INBOX");
//			inbox.open(Folder.READ_WRITE);
//
//
////			String[] CHARTSET_HEADER = new String[] { "Subject", "From", "To", "Cc", "Delivered-To" };
//			FlagTerm ft =  new FlagTerm(new Flags(Flags.Flag.SEEN), false);
////			SearchTerm fromTerm = new FromStringTerm(wrapPattern("ic-receiver@canaan-creative.com"));
//			SearchTerm fromTerm = new SubjectTerm(wrapPattern("欢迎新同学加入Canaan--陈双文"));
//			SearchTerm andTerm = new AndTerm(ft, fromTerm);
//			Message[] messages = inbox.search(andTerm);
//			for (Message message : messages) {
//				log.info("主题：{}", message.getSubject());
//				log.info("发送者：{}", message.getFrom().toString());
//				log.info("时间：{}", message.getSentDate());
//				log.info("内容：{}", message.getContent());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//
//			close(inbox);
//			close(store);
//		}
////        // for POP3
////        //String protocol = "pop3";
////        //String host = "pop.gmail.com";
////        //String port = "995";
////
////        // for IMAP
//////        String protocol = "imap";
//////        String host = "imap.qq.com";
//////        String port = "993";
//////
//////
//////        int port = 993;
////
////        MailThreadPool pool = new MailThreadPool();
////
////        SmtpServer<?> smtpServer = MailServer.create().auth(userName, password)
////                .host("smtp.exmail.qq.com")
////                .buildSmtpMailServer();
////        SendMailSession session = smtpServer.createSession();
//////        ImapServer imapServer = MailServer.create().host(host).auth(userName, password).buildImapMailServer();
//////        log.info("listen the email...");
//////        ReceiveMailSession session = imapServer.createSession();
////        session.open();
////        Email email = Email.create()
////                .from("316279828@qq.com")
////                .to("1035385576@qq.com")
////                .subject("test6")
////                .textMessage("Hello!")
////                .htmlMessage(
////                    "<html><META http-equiv=Content-Type content=\"text/html; " +
////                    "charset=utf-8\"><body><h1>Hey!</h1><img src='cid:c.png'>" +
////                    "<h2>Hay!</h2></body></html>")
////                .embeddedAttachment(EmailAttachment.with()
////                    .content(new File("D:\\template_po.pdf")));
//////        session.sendMail(email);
////        pool.newMailSenderThread(smtpServer, email, new EmailSenderListener() {
//////
////			@Override
////			public void onException(Email mail, String filePath, MailException exception) {
////				exception.printStackTrace();
////
////			}
////
////			@Override
////			public void onComplete(Email mail, String filePath) {
////				log.info("email send success , attchment is {}", filePath);
////
////			}
////		});
////        session.useDefaultFolder();
////        ReceivedEmail[] mails = session.receiveEmailAndMarkSeen(new EmailFilter().flag(Flag.SEEN, false));
////        log.info("receive email, size is {}", mails.length);
////        session.useDefaultFolder();
//////        session.receiveEnvelopes()
////        log.info("mail count {}", session.getUnreadMessageCount());
////        ReceivedEmail[] mails = session.receiveEmailAndMarkSeen(new EmailFilter().flag(Flag.SEEN, false));
////        for (ReceivedEmail mail : mails) {
////			List<EmailAttachment<? extends DataSource>> attchmentList = mail.attachments();
////			for (EmailAttachment<? extends DataSource> attchment : attchmentList) {
////				log.info("mail subject {}: attchment file  is starting download...", mail.subject());
////				pool.newAttchmentDownLoadThread(mail, fileStorePath, attchment, new EmailAttchmentDownloadListener() {
////
////					@Override
////					public void process(String fileName, long current, long total) {
////						float percent = (float)(current * 100) / (float)total;
////						log.info("mail subject:{},attchment name:{}, process:{}",mail.subject(),fileName, percent);
////
////					}
////
////					@Override
////					public void onException(ReceivedEmail mail, MailException exception) {
////						log.error("mail download exception.");
////						exception.printStackTrace();
////
////					}
////
////					@Override
////					public void onComplete(ReceivedEmail mail, File file) {
////						log.info("file {} download complete!",file.getName());
////
////					}
////				});
////			}
////		}
////        session.close();
////        EmailReceiveMonitor receiver = new EmailReceiveMonitor(protocol, host, port, userName, password);
////        receiver.monitorInbox(null, new EmailReceiverListener() {
////
////			@Override
////			public void receiveMessages(List<ReceivedEmail> emails) {
////				for (ReceivedEmail mail : emails) {
////					List<EmailAttachment<? extends DataSource>> attchmentList = mail.attachments();
////					for (EmailAttachment<? extends DataSource> attchment : attchmentList) {
////						log.info("mail subject {}: attchment file  is starting download...", mail.subject());
////						pool.newAttchmentDownLoadThread(mail, fileStorePath, attchment, new EmailAttchmentDownloadListener() {
////
////							@Override
////							public void process(String fileName, long current, long total) {
////								float percent = (float)(current * 100) / (float)total;
////								log.info("mail subject:{},attchment name:{}, process:{}",mail.subject(),fileName, percent);
////
////							}
////
////							@Override
////							public void onException(MailException exception) {
////								log.error("mail download exception.");
////								exception.printStackTrace();
////
////							}
////
////							@Override
////							public void onComplete(ReceivedEmail mail, File file) {
////								log.info("file {} download complete!",file.getName());
////
////							}
////						});
////					}
////				}
////
////			}
////
////			@Override
////			public void onReceiveException(MailException exception) {
////				log.error("monitor email exception.");
////				exception.printStackTrace();
////
////			}
////		});
////        receiver.monitorInbox(null, new EmailReceiverListener() {
////
////			@Override
////			public void receiveMessages(List<MailMessage> messages) {
////				for (MailMessage message : messages) {
////					log.info("主题：{}", message.getSubject());
////					log.info("发送者：{}", message.getFrom());
////					log.info("时间：{}", message.getSendDate());
////					log.info("内容：{}", message.getContent());
////
////					//取附件
////					List<MaillAttchment> list = message.getAttchments();
////					if (list == null || list.isEmpty()) {
////						log.info("附件：无");
////						continue;
////					}
////
////					log.info("附件：");
////					for (MaillAttchment att : list) {
////						log.info("文件名：{}，大小{}",att.getFileName(),att.getFileSize());
////						try {
////							String filePath = "d:\\" + System.currentTimeMillis() + att.getFileName();
////							FileUtil.writeFile(filePath, att.getFilePart().getInputStream());
////						} catch (IOException e) {
////							e.printStackTrace();
////						} catch (MessagingException e) {
////							e.printStackTrace();
////						}
////					}
////				}
////
////			}
////
////			@Override
////			public void onReceiveException(Exception exception) {
////				// TODO Auto-generated method stub
////
////			}
////
////
////		});
//    }
    
    private  class IdleThread extends Thread {
        private final Folder folder;
        private volatile boolean running = true;

        public IdleThread(Folder folder) {
            super();
            this.folder = folder;
        }

        public synchronized void kill() {

            if (!running)
                return;
            this.running = false;
        }

        @Override
        public void run() {
            while (running) {

                try {
                    ensureOpen(folder);
                    System.out.println("enter idle");
                    ((IMAPFolder) folder).idle();
                } catch (Exception e) {
                    // something went wrong
                    // wait and try again
                    e.printStackTrace();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                }

            }
        }
    }

    public static void close(final Folder folder) {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
        } catch (final Exception e) {
            // ignore
        }

    }

    public static void close(final Store store) {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (final Exception e) {
            // ignore
        }

    }

    public  void ensureOpen(final Folder folder) throws MessagingException {

        if (folder != null) {
            Store store = folder.getStore();
            if (store != null && !store.isConnected()) {
                store.connect(username, password);
            }
        } else {
            throw new MessagingException("Unable to open a null folder");
        }

        if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            System.out.println("open folder " + folder.getFullName());
            folder.open(Folder.READ_ONLY);
            if (!folder.isOpen())
                throw new MessagingException("Unable to open folder " + folder.getFullName());
        }

    }
}