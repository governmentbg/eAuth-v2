package bg.bulsi.egov.eauth.mapping;

import bg.bulsi.egov.eauth.model.Preferred2FA;
import bg.bulsi.egov.eauth.model.ekkate.Ekatte;
import bg.bulsi.egov.eauth.profile.rest.api.dto.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class UserMapping extends AbstractMapping {


    @Override
    public void fromDtoToEntity(ModelMapper modelMapper) {

        // EKATTE
        modelMapper.createTypeMap(Ekatte.class, bg.bulsi.egov.eauth.model.ekkate.Ekatte.class)
                .addMappings(new PropertyMap<Ekatte, bg.bulsi.egov.eauth.model.ekkate.Ekatte>() {
                    @Override
                    protected void configure() {
                        map().setArea(source.getMunicipality());
                        map().setEkatteId(source.getId());
                        map().setPlace(source.getPlace());
                    }
                });

        // Address
        modelMapper.createTypeMap(Address.class, bg.bulsi.egov.eauth.model.Address.class)
                .addMappings(mapper -> mapper.using(context -> context.getMappingEngine().map(context.create(context.getSource(), context.getDestinationType())))
                        .map(Address::getEkatte, bg.bulsi.egov.eauth.model.Address::setEkatte));




        modelMapper.createTypeMap(User.class, bg.bulsi.egov.eauth.model.User.class)
                .addMappings(mapping -> {
                    mapping.map(User::getAddress, bg.bulsi.egov.eauth.model.User::setAddress);
                    mapping.map(User::getNames, bg.bulsi.egov.eauth.model.User::setName);
                    mapping.map(User::getPhoneNumber, bg.bulsi.egov.eauth.model.User::setPhoneNumber);
                    mapping.using((Converter<OTPMethod, Preferred2FA>) context -> {
                        OTPMethod s = context.getSource();
                        return (s == null) ? Preferred2FA.SMS : Preferred2FA.valueOf(s.name());
                    }).map(User::getPreferred2FA, bg.bulsi.egov.eauth.model.User::setPreferred);
                });




    }

    @Override
    public void fromEntityToDto(ModelMapper modelMapper) {

        modelMapper.createTypeMap(bg.bulsi.egov.eauth.model.User.class, User.class)
                .addMappings(mapping -> {
                    mapping.map(bg.bulsi.egov.eauth.model.User::getId, User::setId);
                    mapping.map(bg.bulsi.egov.eauth.model.User::getAddress, User::setAddress);

                    mapping.map(bg.bulsi.egov.eauth.model.User::getPhoneNumber, User::setPhoneNumber);
                    mapping.map(bg.bulsi.egov.eauth.model.User::getName, User::setNames);
                    mapping.map(bg.bulsi.egov.eauth.model.User::getPreferred, User::setPreferred2FA);


                });

        // Address
        modelMapper.createTypeMap( bg.bulsi.egov.eauth.model.Address.class,Address.class)
                .addMappings(mapper -> mapper.using(context -> context.getMappingEngine().map(context.create(context.getSource(), context.getDestinationType())))
                        .map( bg.bulsi.egov.eauth.model.Address::getEkatte,Address::setEkatte));
/*
       // EKATTE
        modelMapper.createTypeMap( bg.bulsi.egov.eauth.model.ekkate.Ekatte.class,Ekatte.class)
                .addMappings(new PropertyMap<bg.bulsi.egov.eauth.model.ekkate.Ekatte, Ekatte>() {
                    @Override
                    protected void configure() {
                        map().setDistrict(source.getArea());
                        map().setId(source.getEkatteId());
                        map().setPlace(source.getPlace());

                    }
                });
*/
        modelMapper.createTypeMap(bg.bulsi.egov.eauth.model.User.class, FullProfile.class)
                .setProvider((Provider.ProvisionRequest<FullProfile> request) -> {
                    FullProfile profile = new FullProfile();
                    profile.setUser(new User());

                    return profile;
                })
                .addMappings(mapping -> {
                    mapping.<String>map(bg.bulsi.egov.eauth.model.User::getName, (dest, v) -> dest.getUser().setNames(v));
                    mapping.<String>map(bg.bulsi.egov.eauth.model.User::getEmail, (dest, v) -> dest.getUser().setEmail(v));
                    mapping.<Long>map(bg.bulsi.egov.eauth.model.User::getId, (dest, v) -> dest.getUser().setId(v));
                    mapping.<String>map(bg.bulsi.egov.eauth.model.User::getPhoneNumber, (dest, v) -> dest.getUser().setPhoneNumber(v));
                    mapping.<OTPMethod>map(bg.bulsi.egov.eauth.model.User::getPreferred, (dest, v) -> dest.getUser().setPreferred2FA(v));

                    mapping.using((Converter<Map<String, String>, Userttributes>) context -> {

                                Userttributes d = new Userttributes();

                                Map<String, String> source = context.getSource();
                                if (source != null) {
                                    Set<Map.Entry<String, String>> entries = source.entrySet();
                                    for (Map.Entry<String, String> mapEntry : entries) {
                                        d.put(mapEntry.getKey(), mapEntry.getValue());
                                    }
                                }

                                return d;
                            }
                    ).map(bg.bulsi.egov.eauth.model.User::getAttributes, FullProfile::setAttributes);

                });
    }
}