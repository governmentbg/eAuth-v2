package bg.bulsi.egov.eauth.tfa.email.services.email;


import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import bg.bulsi.egov.hazelcast.service.HazelcastService;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class AsyncEmailServiceImpl implements AsyncEmailService {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;
    private final HazelcastService hazelcastService;
    private final MailContentBuilder mailContentBuilder;

    @Autowired
    public AsyncEmailServiceImpl(JavaMailSender mailSender,
                                 ResourceLoader resourceLoader,
                                 MailContentBuilder mailContentBuilder,
                                 HazelcastService hazelcastService) {
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
        this.hazelcastService = hazelcastService;
        this.mailContentBuilder = mailContentBuilder;
    }

    @Async
    @Override
    public Future<String> sendMailWithImages(MailContent mail) throws Exception {

        log.info("Start execution of async. Sending email with file attachment");

        try {
            MimeMessage message = mailSender.createMimeMessage();

            final MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(mail.to());
            helper.setSubject(mail.subject());
            helper.setFrom(Objects.requireNonNull(hazelcastService.get("egov.eauth.sys.tfa.email.sender.from")));

            helper.setText(mailContentBuilder.build(mail.template()), true);

            for (Map.Entry<String, String> attachment : mail.attachments().entrySet()) {
                helper.addInline(attachment.getKey(), resourceLoader.getResource(attachment.getValue()));
            }

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Exception occurs in sending email!", e);
            throw new Exception("Exception occurs in sending email! --> MessagingException", e);

        } catch (MailSendException e) {
            log.error("Exception occurs in sending email!", e);
            throw new Exception("Exception occurs in sending email! --> MailSendException", e);
        }

        return new AsyncResult<>("Email send successfully");
    }

    @Async
    @Override
    public Future<String> sendMail(MailContent mail) throws Exception {

        log.info("Start execution of async. Sending email");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            helper.setFrom(Objects.requireNonNull((hazelcastService.get("egov.eauth.sys.tfa.email.sender.from"))));
            helper.setTo(mail.to());
            helper.setSubject(mail.subject());
            helper.setText(mailContentBuilder.build(mail.template()), true);

            /*
             * FileSystemResource file = new FileSystemResource(attachFile);
             * helper.addAttachment(file.getFilename(), file);
             */

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Exception occurs in sending email! --> {}", e.getMessage());
            throw new Exception("Exception occurs in sending email! --> MessagingException", e);
        } catch (MailSendException e) {
            log.error("Exception occurs in sending email! --> {}", e.getMessage());
            throw new Exception("Exception occurs in sending email! --> MailSendException", e);
        }

        return new AsyncResult<>("Email send successfully");
    }
}