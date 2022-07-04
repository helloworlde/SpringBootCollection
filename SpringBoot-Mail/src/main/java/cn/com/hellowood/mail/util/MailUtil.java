package cn.com.hellowood.mail.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * The type Mail util.
 *
 * @author HelloWood
 */
@Component
public class MailUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The Mail sender.
     */
    @Autowired
    JavaMailSender mailSender;

    /**
     * The Template engine.
     */
    @Autowired
    TemplateEngine templateEngine;

    /**
     * Send simple email.
     *
     * @param deliver  the deliver
     * @param receiver the receiver
     * @param subject  the subject
     * @param content  the content
     * @throws ServiceException the service exception
     */
    public void sendSimpleEmail(String deliver, String receiver, String subject, String content) throws ServiceException {
        String[] receivers = {receiver};
        sendSimpleEmail(deliver, receivers, null, subject, content);
    }

    /**
     * Send simple email.
     *
     * @param deliver    the deliver
     * @param receiver   the receiver
     * @param carbonCopy the carbon copy
     * @param subject    the subject
     * @param content    the content
     * @throws ServiceException the service exception
     */
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

    /**
     * Send html email.
     *
     * @param deliver  the deliver
     * @param receiver the receiver
     * @param subject  the subject
     * @param content  the content
     * @throws ServiceException the service exception
     */
    public void sendHtmlEmail(String deliver, String receiver, String subject, String content) throws ServiceException {
        String[] receivers = {receiver};
        sendHtmlEmail(deliver, receivers, null, subject, content, true);
    }

    /**
     * Send html email.
     *
     * @param deliver    the deliver
     * @param receiver   the receiver
     * @param carbonCopy the carbon copy
     * @param subject    the subject
     * @param content    the content
     * @param isHtml     the is html
     * @throws ServiceException the service exception
     */
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

    /**
     * Send attachment file email.
     *
     * @param deliver  the deliver
     * @param receiver the receiver
     * @param subject  the subject
     * @param content  the content
     * @param fileName the file name
     * @param file     the file
     * @throws ServiceException the service exception
     */
    public void sendAttachmentFileEmail(String deliver, String receiver, String subject, String content, String fileName, File file) throws ServiceException {
        String[] receivers = {receiver};
        sendAttachmentFileEmail(deliver, receivers, null, subject, content, true, fileName, file);
    }

    /**
     * Send attachment file mail.
     *
     * @param deliver    the deliver
     * @param receiver   the receiver
     * @param carbonCopy the carbon copy
     * @param subject    the subject
     * @param content    the content
     * @param isHtml     the is html
     * @param fileName   the file name
     * @param file       the file
     * @throws ServiceException the service exception
     */
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

    /**
     * Send template email.
     *
     * @param deliver  the deliver
     * @param receiver the receiver
     * @param subject  the subject
     * @param template the template
     * @param context  the context
     * @throws ServiceException the service exception
     */
    public void sendTemplateEmail(String deliver, String receiver, String subject, String template, Context context) throws ServiceException {
        String[] receivers = {receiver};
        sendTemplateEmail(deliver, receivers, null, subject, template, context);
    }

    /**
     * Send template mail.
     *
     * @param deliver    the deliver
     * @param receiver   the receiver
     * @param carbonCopy the carbon copy
     * @param subject    the subject
     * @param template   the template
     * @param context    the context
     * @throws ServiceException the service exception
     */
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
