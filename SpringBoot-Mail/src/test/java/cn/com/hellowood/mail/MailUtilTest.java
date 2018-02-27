//package cn.com.hellowood.mail;
//
//import cn.com.hellowood.mail.util.MailUtil;
//import cn.com.hellowood.mail.util.ServiceException;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.thymeleaf.context.Context;
//
//import java.io.File;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.util.Date;
//
//import static org.junit.Assert.assertFalse;
//
//
///**
// * The type Mail util tests.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class MailUtilTest extends SpringBootMailApplicationTests {
//
//    private final String deliver = "";
//    private final String[] receiver = {""};
//    private final String[] carbonCopy = {""};
//    private final String template = "mail/InternalServerErrorTemplate";
//
//    private final String FILE_PATH = "/Users/HelloWood/Downloads/Report.png";
//
//
//    @Autowired
//    private MailUtil mailUtil;
//
//    /**
//     * Send simple email.
//     */
//    @Test
//    public void sendSimpleEmail() {
//        String subject = "This is a simple email";
//        String content = "This is a simple content";
//        try {
//            mailUtil.sendSimpleEmail(deliver, receiver, carbonCopy, subject, content);
//        } catch (Exception e) {
//            assertFalse("Send simple email failed", true);
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send html email.
//     */
//    @Test
//    public void sendHtmlEmail() {
//        String subject = "This is a HTML content email";
//        String content = "<h1>This is HTML content email </h1>";
//        boolean isHtml = true;
//        try {
//            mailUtil.sendHtmlEmail(deliver, receiver, carbonCopy, subject, content, isHtml);
//        } catch (ServiceException e) {
//            assertFalse("Send HTML content email failed", true);
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send attachment file email.
//     */
//    @Test
//    public void sendAttachmentFileEmail() {
//        String subject = "This is an attachment file email";
//        String content = "<h2>This is an attachment file email</h2>";
//        boolean isHtml = true;
//
//        File file = new File(FILE_PATH);
//        String fileName = FILE_PATH.substring(FILE_PATH.lastIndexOf(File.separator));
//
//        try {
//            mailUtil.sendAttachmentFileEmail(deliver, receiver, carbonCopy, subject, content, isHtml, fileName, file);
//        } catch (ServiceException e) {
//            assertFalse("Send attachment file email failed", true);
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send template email.
//     */
//    @Test
//    public void sendTemplateEmail() {
//        String subject = "This is a template email";
//        Context context = new Context();
//        String errorMessage;
//
//        try {
//            throw new NullPointerException();
//        } catch (NullPointerException e) {
//            Writer writer = new StringWriter();
//            PrintWriter printWriter = new PrintWriter(writer);
//            e.printStackTrace(printWriter);
//            errorMessage = writer.toString();
//        }
//
//        context.setVariable("username", "HelloWood");
//        context.setVariable("methodName", "cn.com.hellowood.mail.MailUtilTests.sendTemplateEmail()");
//        context.setVariable("errorMessage", errorMessage);
//        context.setVariable("occurredTime", new Date());
//
//        try {
//            mailUtil.sendTemplateEmail(deliver, receiver, carbonCopy, subject, template, context);
//        } catch (ServiceException e) {
//            assertFalse("Send template email failed", true);
//            e.printStackTrace();
//        }
//    }
//}