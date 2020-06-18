package bg.bulsi.egov.eauth.tfa.email.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    String build(MailContent.TemplateModel templateModel) {

        Context context = new Context();
        context.setVariables(templateModel.model());

        return templateEngine.process(templateModel.templateName(), context);
    }

}