# Spring Boot 发送邮件
> 在 Spring Boot 中使用 Spring Mail 发送邮件

----------------

## 添加 Spring Mail 依赖

- build.gradle 

```groovy
   compile('org.springframework.boot:spring-boot-starter-mail')
```

## 添加配置文件 

- application.properties

```properties
    spring.mail.host=smtpdm.aliyun.com
    spring.mail.port=465
    spring.mail.username="你的邮箱"
    spring.mail.password="你的密码"
    spring.mail.properties.smtp.auth=true
    spring.mail.properties.smtp.starttls.enable=true
    spring.mail.properties.smtp.starttls.required=true
    spring.mail.properties.mail.smtp.ssl.enable=true
```

---------------

## 发送简单邮件 

- MailUtil.java

```java
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.mail.MailException;
   import org.springframework.mail.SimpleMailMessage;
   import org.springframework.mail.javamail.JavaMailSender;
   import org.springframework.stereotype.Component;
   import org.thymeleaf.TemplateEngine;
   
   @Component
   public class MailUtil {
   
       private final Logger logger = LoggerFactory.getLogger(getClass());
   
       @Autowired
       JavaMailSender mailSender;
   
       @Autowired
       TemplateEngine templateEngine;
   
       public void sendSimpleEmail(String deliver, String[] receiver, String[] carbonCopy, String subject, String content) throws ServiceException {
   
           long startTimestamp = System.currentTimeMillis();
           logger.info("Start send mail ... ");
   
           try {
               SimpleMailMessage message = new SimpleMailMessage();
               message.setFrom(deliver);
               message.setTo(receiver);
               message.setCc(carbonCopy);
               message.setSubject(subject);
               message.setText(content);
               mailSender.send(message);
               logger.info("Send mail success, cost {} million seconds", System.currentTimeMillis() - startTimestamp);
           } catch (MailException e) {
               logger.error("Send mail failed, error message is : {} \n", e.getMessage());
               e.printStackTrace();
               throw new ServiceException(e.getMessage());
           }
       }
   
   }
```

- 发送邮件

```java
    @Test
    public void sendSimpleEmail() {
        String deliver = "你的邮件地址";
        String[] receiver = {"接收者邮件地址"};
        String[] carbonCopy = {"抄送者邮件地址"};
        String subject = "This is a simple email";
        String content = "This is a simple content";
        
        try {
            mailUtil.sendSimpleEmail(deliver, receiver, carbonCopy, subject, content);
        } catch (Exception e) {
            assertFalse("Send simple email failed", true);
            e.printStackTrace();
        }
    }

```

## 发送 HTML 内容的邮件

- MailUtil.java

```java
    import cn.com.hellowood.mail.util.ServiceException;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.stereotype.Component;
    
    import javax.mail.MessagingException;
    import javax.mail.internet.MimeMessage;
    
    @Component
    public class MailUtil {
    
        private final Logger logger = LoggerFactory.getLogger(getClass());
    
        @Autowired
        JavaMailSender mailSender;
    
        public void sendHtmlEmail(String deliver, String[] receiver, String[] carbonCopy, String subject, String content, boolean isHtml) throws ServiceException {
            long startTimestamp = System.currentTimeMillis();
            logger.info("Start send email ...");
    
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message);
    
                messageHelper.setFrom(deliver);
                messageHelper.setTo(receiver);
                messageHelper.setCc(carbonCopy);
                messageHelper.setSubject(subject);
                messageHelper.setText(content, isHtml);
    
                mailSender.send(message);
                logger.info("Send email success, cost {} million seconds", System.currentTimeMillis() - startTimestamp);
            } catch (MessagingException e) {
                logger.error("Send email failed, error message is {} \n", e.getMessage());
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
    
    }

```

- 发送 HTML 邮件

```java
    @Test
    public void sendHtmlEmail() {
        String deliver = "你的邮件地址";
        String[] receiver = {"接收者邮件地址"};
        String[] carbonCopy = {"抄送者邮件地址"};
        String subject = "This is a HTML content email";
        String content = "<h1>This is HTML content email </h1>";
        
        boolean isHtml = true;
        try {
            mailUtil.sendHtmlEmail(deliver, receiver, carbonCopy, subject, content, isHtml);
        } catch (ServiceException e) {
            assertFalse("Send HTML content email failed", true);
            e.printStackTrace();
        }
    }

```

## 发送带附件的邮件

- MailUtil.java

```java
    
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.stereotype.Component;
    
    import javax.mail.MessagingException;
    import javax.mail.internet.MimeMessage;
    import java.io.File;
    
    @Component
    public class MailUtil {
    
        private final Logger logger = LoggerFactory.getLogger(getClass());
    
        @Autowired
        JavaMailSender mailSender;
    
        public void sendAttachmentFileEmail(String deliver, String[] receiver, String[] carbonCopy, String subject, String content, boolean isHtml, String fileName, File file) throws ServiceException {
            long startTimestamp = System.currentTimeMillis();
            logger.info("Start send mail ...");
    
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
    
                messageHelper.setFrom(deliver);
                messageHelper.setTo(receiver);
                messageHelper.setCc(carbonCopy);
                messageHelper.setSubject(subject);
                messageHelper.setText(content, isHtml);
                messageHelper.addAttachment(fileName, file);
    
                mailSender.send(message);
                logger.info("Send mail success, cost {} million seconds", System.currentTimeMillis() - startTimestamp);
            } catch (MessagingException e) {
                logger.error("Send mail failed, error message is {}\n", e.getMessage());
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
        
    }

```

- 发送带附件的邮件

```java
    @Test
    public void sendAttachmentFileEmail() {
        String FILE_PATH = "文件地址";
        String deliver = "你的邮件地址";
        String[] receiver = {"接收者邮件地址"};
        String[] carbonCopy = {"抄送者邮件地址"};    
        String subject = "This is an attachment file email";
        String content = "<h2>This is an attachment file email</h2>";
        boolean isHtml = true;

        File file = new File(FILE_PATH);
        String fileName = FILE_PATH.substring(FILE_PATH.lastIndexOf(File.separator));

        try {
            mailUtil.sendAttachmentFileEmail(deliver, receiver, carbonCopy, subject, content, isHtml, fileName, file);
        } catch (ServiceException e) {
            assertFalse("Send attachment file email failed", true);
            e.printStackTrace();
        }
    }
```

## 发送模板邮件

> 使用 Thymeleaf 作为模板

- 添加依赖

```groovy
	compile('org.springframework.boot:spring-boot-starter-thymeleaf')
```

- 邮件模板(`InternalServerErrorTemplate.html`)

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <title>Hello World</title>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css"
          th:href="@{https://cdn.bootcss.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css}"/>
</head>
<style type="text/css">
    .full-screen {
        height: fit-content;
        width: fit-content;
    }

    .fixed-page {
        overflow-x: scroll;
        overflow-y: scroll;
    }

    .container {
        width: 100%;
        padding-right: 15px;
        padding-left: 15px;
        margin-right: auto;
        margin-left: auto
    }

    .jumbotron {
        padding: 2rem 1rem;
        margin-bottom: 2rem;
        background-color: #e9ecef;
        border-radius: .3rem
    }

    .jumbotron-fluid {
        padding-right: 0;
        padding-left: 0;
        border-radius: 0
    }

</style>
<body class="fixed-page">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bgcolor="#ffffff"
       style="font-family:'Microsoft YaHei';">
    <div class="jumbotron jumbotron-fluid full-screen">
        <div class="container  full-screen">
            <h3>Hi,
                <span th:text="${username}">Developer</span>
            </h3>
            <p>There is an exception occurred in method
                <code style="color: #d57e13;"><span th:text="${methodName}">methodName</span></code>,
                the error message is :
            </p>
            <div>
            <pre>
                <code style="font-family: 'Source Code Pro';">
                    <span th:text="${errorMessage}">Error Message</span>
                </code>
            </pre>
            </div>
            <span th:text="${occurredTime}">occurredTime</span>
        </div>
    </div>
</table>
</body>
</html>

```

- MailUtil.java

```java

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.stereotype.Component;
    import org.thymeleaf.TemplateEngine;
    import org.thymeleaf.context.Context;
    
    import javax.mail.MessagingException;
    import javax.mail.internet.MimeMessage;
    
    @Component
    public class MailUtil {
    
        private final Logger logger = LoggerFactory.getLogger(getClass());
    
        @Autowired
        JavaMailSender mailSender;
    
        @Autowired
        TemplateEngine templateEngine;
    
        public void sendTemplateEmail(String deliver, String[] receiver, String[] carbonCopy, String subject, String template, Context context) throws ServiceException {
            long startTimestamp = System.currentTimeMillis();
            logger.info("Start send mail ...");
    
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message);
    
                String content = templateEngine.process(template, context);
                messageHelper.setFrom(deliver);
                messageHelper.setTo(receiver);
                messageHelper.setCc(carbonCopy);
                messageHelper.setSubject(subject);
                messageHelper.setText(content, true);
    
                mailSender.send(message);
                logger.info("Send mail success, cost {} million seconds", System.currentTimeMillis() - startTimestamp);
            } catch (MessagingException e) {
                logger.error("Send mail failed, error message is {} \n", e.getMessage());
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
    }

```

- 发送模板邮件 

```java
    @Test
    public void sendTemplateEmail() {
        String deliver = "你的邮件地址";
        String[] receiver = {"接收者邮件地址"};
        String[] carbonCopy = {"抄送者邮件地址"};
        String template = "mail/InternalServerErrorTemplate";
        String subject = "This is a template email";
        Context context = new Context();
        String errorMessage;

        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            errorMessage = writer.toString();
        }

        context.setVariable("username", "HelloWood");
        context.setVariable("methodName", "cn.com.hellowood.mail.MailUtilTests.sendTemplateEmail()");
        context.setVariable("errorMessage", errorMessage);
        context.setVariable("occurredTime", new Date());

        try {
            mailUtil.sendTemplateEmail(deliver, receiver, carbonCopy, subject, template, context);
        } catch (ServiceException e) {
            assertFalse("Send template email failed", true);
            e.printStackTrace();
        }
    }

```

