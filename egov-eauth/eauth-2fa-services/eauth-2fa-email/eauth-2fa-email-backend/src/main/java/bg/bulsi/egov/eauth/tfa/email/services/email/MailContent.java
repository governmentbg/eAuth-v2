package bg.bulsi.egov.eauth.tfa.email.services.email;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class MailContent {

    /*
        Mail sender
     */
    @Getter
    @Setter
    private String from;

    /*
        Mail recipient
     */
    @Getter
    @Setter
    private String to;

    /*
        Mail subject
     */
    @Getter
    @Setter
    private String subject;

    @Getter
    private TemplateModel template = new TemplateModel();

    /*
        Inline Attachments - used to display images into template

        Images
        KEY:    <img src="cid:KEY />
        VALUE:  image as resource
    */
    @Getter
    private Map<String, String> attachments = new HashMap<>();

    @Accessors(fluent = true)
    public class TemplateModel {

        /*
            Name of template
            in classpath:/templates/
         */
        @Getter
        @Setter
        private String templateName;

        /*
            Template model
            Key/Value
         */
        @Getter
        private Map<String, Object> model = new HashMap<>();

        public TemplateModel property(String key, String value) {
            model.put(key, value);
            return this;
        }
    }
}
