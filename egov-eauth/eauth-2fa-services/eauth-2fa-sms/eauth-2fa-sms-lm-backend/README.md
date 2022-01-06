Параметри на заявката

```bash
msisdn 		Valid MSISDN 		 string 	Yes 
sc     		Shortcode 		 string 	Yes 
text   		SMS Message 		 string 	Yes 
service_id 	Link-Mobility Service ID integer 	Yes
priority	Priority of message	 string		No
```
Достъпи

```bash
ID:		394
SC:		1917
Api key:	$2y$10$mN8ywDmA2bVSUbiZ8hQHyOsj8x8DY1jm9zvobXwt/R2mWU/egu4He
Api secret:	$2y$10$Ny.07AJ5mJRxaY55BuT.KeQeGZoDaq3NJ0Pg.uSosj4zoaEGQvwAC
```
Postman

```bash
POST https://api-test.msghub.cloud/bulknew

Headers:
------------------------------
Content-Type:	application/json
x-api-key:	$2y$10$mN8ywDmA2bVSUbiZ8hQHyOsj8x8DY1jm9zvobXwt/R2mWU/egu4He
x-api-sign:	fac30ef0c8b51663324af5362cdbfbb06b4fd090edb3f8ebf958e0b9c845d24f18252252465619ac5c29f721c734234fa2b0789e9871da0696c537994c326f7c
Expect:		

Body(raw):
------------------------------
{"msisdn":"+359883367967","sc":"1917","text":"Тестово съобщение еАвт","service_id":394,"priority":"high"}
```
Примерен код за HMAC SHA512 хеширане

```bash
import static org.apache.commons.codec.digest.HmacAlgorithms.HMAC_SHA_512;
import org.apache.commons.codec.digest.HmacUtils;

public static void main(String[] args) {
	String secret = "$2y$10$Ny.07AJ5mJRxaY55BuT.KeQeGZoDaq3NJ0Pg.uSosj4zoaEGQvwAC";
	String dataAsJson = "{\"msisdn\":\"+359883367967\",\"sc\":\"1917\",\"text\":\"Тестово съобщение еАвт\",\"service_id\":394,\"priority\":\"high\"}";
	System.out.println("hmac-512 => " + new HmacUtils(HMAC_SHA_512, secret).hmacHex(dataAsJson));
}
```
TODO in DB!!!

```bash
-- update api url and key properties
UPDATE db_property_source SET property_value = 'https://api.msghub.cloud/bulknew' WHERE property_key = 'egov.eauth.sys.tfa.sms.provider.api.url';
UPDATE db_property_source SET property_value = '$2y$10$mN8ywDmA2bVSUbiZ8hQHyOsj8x8DY1jm9zvobXwt/R2mWU/egu4He' WHERE property_key = 'egov.eauth.sys.tfa.sms.provider.api.key';

-- update local api port
UPDATE db_property_source SET property_value = 'http://localhost:8140/otp/auth' WHERE property_key = 'egov.eauth.sys.tfa.sms.auth_url';
UPDATE db_property_source SET property_value = 'http://localhost:8140/otp/validate' WHERE property_key = 'egov.eauth.sys.tfa.sms.validate.url';
	
-- add api secret property
INSERT INTO db_property_source (property_key, property_value) VALUES ('egov.eauth.sys.tfa.sms.provider.api.secret', '$2y$10$Ny.07AJ5mJRxaY55BuT.KeQeGZoDaq3NJ0Pg.uSosj4zoaEGQvwAC');
```

