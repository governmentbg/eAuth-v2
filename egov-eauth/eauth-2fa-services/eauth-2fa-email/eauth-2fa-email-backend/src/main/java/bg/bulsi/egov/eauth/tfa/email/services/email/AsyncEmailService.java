package bg.bulsi.egov.eauth.tfa.email.services.email;

import java.util.concurrent.Future;

public interface AsyncEmailService {

    Future<String> sendMail(MailContent mail) throws Exception;

    Future<String> sendMailWithImages(MailContent mail) throws Exception;
}
