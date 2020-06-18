su - postgres

createdb eauth -U bulsiadmin -E UTF-8 -h localhost

CREATE SCHEMA sms;

-- update sequence:
--SELECT MAX(id) + 1 FROM sms.sms;
--ALTER SEQUENCE sms.sms_id_seq RESTART WITH 15;

CREATE TABLE public.db_property_source
(
    property_key character varying(128) COLLATE pg_catalog."default" NOT NULL,
    property_value character varying(128) COLLATE pg_catalog."default",
    CONSTRAINT pk_key PRIMARY KEY (property_key)
)
WITH (
    OIDS = FALSE
)

ALTER TABLE public.db_property_source OWNER to bulsiadmin;

-- restart Sms sequence
ALTER SEQUENCE sms.sms_id_seq RESTART WITH 160;

INSERT INTO db_property_source (property_key, property_value) VALUES ('sms.provider.api.key', 'c52a2e0dc36334c2c6844634deabe078');

INSERT INTO db_property_source (property_key, property_value) VALUES ('eauth.tfa.email.validate.url', 'http://localhost:8110/otp/validate');

INSERT INTO db_property_source (property_key, property_value) VALUES ('eauth.tfa.email.auth.url', 'http://localhost:8110/otp/auth');

INSERT INTO db_property_source (property_key, property_value) VALUES ('eauth.tfa.sms.validate.url', 'http://localhost:8120/otp/validate');

INSERT INTO db_property_source (property_key, property_value) VALUES ('eauth.tfa.sms.auth.url', 'http://localhost:8120/otp/auth');

INSERT INTO db_property_source (property_key, property_value) VALUES ('sms.provider.text.template', 'Vashiyat kod za dostap e: ');

INSERT INTO db_property_source (property_key, property_value) VALUES ('sms.provider.api.url', 'http://api.smspro.bg/bsms/send');

INSERT INTO db_property_source (property_key, property_value) VALUES ('sms.cron.enabled', 'true');

INSERT INTO db_property_source (property_key, property_value) VALUES ('sms.cron.expression', '0 0 0 1 * ?');

INSERT INTO db_property_source (property_key, property_value) VALUES ('sms.delete.period.days', '30');

INSERT INTO db_property_source (property_key, property_value) VALUES ('sms.code.expiration', '300');

INSERT INTO db_property_source (property_key, property_value) VALUES ('eauth.tfa.totp.generate.url', 'http://localhost:8130/totp/1234567890/generate-secret');

INSERT INTO db_property_source (property_key, property_value) VALUES ('eauth.tfa.totp.validate.url', 'http://localhost:8130/totp/{applicationID}/validate-totp');

INSERT INTO db_property_source (property_key, property_value) VALUES ('eauth.tfa.totp.msg', 'Въведете получения TOTP код');

INSERT INTO db_property_source (property_key, property_value) VALUES ('eauth.tfa.send.msg', 'Кодът за двуфакторна автентикация е изпратен на {0}. Валиден е {1} мин.');

INSERT INTO db_property_source (property_key, property_value) VALUES ('total.authentication.time.minutes', '30');

-- New

INSERT INTO db_property_source (property_key, property_value) VALUES ('egov.eauth.sys.mgs.auth.timeout', 'Изтекло време за автентикация');

INSERT INTO db_property_source (property_key, property_value) VALUES ('egov.eauth.sys.mgs.auth.cancel', 'Отказ от автентикация');

INSERT INTO db_property_source (property_key, property_value) VALUES ('egov.eauth.sys.admin.reload.map.url', 'http://localhost:8080/api/idp/dbreload');


CREATE SCHEMA identity_provider;

-- Updates of db properties:

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.authentication.maxtime.minutes' WHERE property_key = 'total.authentication.time.minutes';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.email.enabled' WHERE property_key = 'otp.email.enabled';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.email.sender.subject' WHERE property_key = 'eauth.tfa.email.sender.subject';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.email.otp.expiration' WHERE property_key = 'eauth.tfa.email.token.validation-interval-in-seconds';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.send.msg' WHERE property_key = 'eauth.tfa.send.msg';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.sms.otp.expiration' WHERE property_key = 'sms.code.expiration';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.sms.enabled' WHERE property_key = 'otp.sms.enabled';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.sms.provider.text.template' WHERE property_key = 'sms.provider.text.template';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.totp.enabled' WHERE property_key = 'otp.totp.enabled';

UPDATE public.db_property_source SET property_key = 'egov.eauth.dyn.tfa.totp.msg' WHERE property_key = 'eauth.tfa.totp.msg';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.totp.window.size' WHERE property_key = 'totp.window.size';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.eid.client.auth.url' WHERE property_key = 'eauth.eid.auth.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.eid.client.inq.url' WHERE property_key = 'eauth.eid.inq.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.int.soap.regres.service.name' WHERE property_key = 'soap.service.name';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.int.soap.regres.service.url' WHERE property_key = 'soap.service.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.email.auth.url' WHERE property_key = 'eauth.tfa.email.auth.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.email.sender.from' WHERE property_key = 'eauth.tfa.email.sender.from';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.email.otp.code_length' WHERE property_key = 'eauth.tfa.email.token.verification-code-length';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.email.validate_url' WHERE property_key = 'eauth.tfa.email.validate.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.sms.auth_url' WHERE property_key = 'eauth.tfa.sms.auth.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.sms.cron.enabled' WHERE property_key = 'sms.cron.enabled';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.sms.cron.expression' WHERE property_key = 'sms.cron.expression';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.sms.otp.delete.period.days' WHERE property_key = 'sms.delete.period.days';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.sms.provider.api.key' WHERE property_key = 'sms.provider.api.key';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.sms.provider.api.url' WHERE property_key = 'sms.provider.api.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.sms.validate.url' WHERE property_key = 'eauth.tfa.sms.validate.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.totp.generate.url' WHERE property_key = 'eauth.tfa.totp.generate.url';

UPDATE public.db_property_source SET property_key = 'egov.eauth.sys.tfa.totp.validate.url' WHERE property_key = 'eauth.tfa.totp.validate.url';

