package bg.bulsi.egov.eauth.services.profile;

import bg.bulsi.egov.eauth.model.ekkate.Ekatte;
import bg.bulsi.egov.eauth.model.repository.EkatteRepository;
import bg.bulsi.egov.eauth.profile.rest.api.EkatteApiDelegate;
import bg.bulsi.egov.eauth.profile.rest.api.NotFoundException;
import bg.bulsi.egov.eauth.profile.rest.api.dto.EkatteReq;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class EkatteApiService implements EkatteApiDelegate {

    private final EkatteRepository ekatteRepository;
    private final ModelMapper mapper;

    @Autowired
    public EkatteApiService(ModelMapper mapper, EkatteRepository ekatteRepository) {
        this.mapper = mapper;
        this.ekatteRepository = ekatteRepository;
    }

    @PostConstruct
    void postConstruct() {

        mapper.createTypeMap(Ekatte.class,  bg.bulsi.egov.eauth.profile.rest.api.dto.Ekatte.class)
                .addMapping( Ekatte::getEkatteId, bg.bulsi.egov.eauth.profile.rest.api.dto.Ekatte::setId)
                .addMapping( Ekatte::getArea,  bg.bulsi.egov.eauth.profile.rest.api.dto.Ekatte::setDistrict)
                .setPostConverter(context -> {
                    bg.bulsi.egov.eauth.profile.rest.api.dto.Ekatte ekatte = context.getDestination();
                    Ekatte source = context.getSource();

                    ekatte.setFullName(
                            source == null ? "" : source.getPlace() + "(" +
                                    (source.getPlaceType() == null ? "" : source.getPlaceType().getPlaceName()) + "), общ." +
                                    source.getMunicipality() + ", обл." + source.getArea()
                    );
                    return context.getDestination();
                });

    }

    @Override
    public ResponseEntity<List<bg.bulsi.egov.eauth.profile.rest.api.dto.Ekatte>> getByPlace(EkatteReq placeReq)  {

        if (StringUtils.isEmpty(placeReq.getPlace())) {
           // throw new NotFoundException(1000, "Place is empty");
        }

        List<Ekatte> list = ekatteRepository.findAllByPlaceContainingIgnoreCaseOrderByPlace(placeReq.getPlace());

        List<bg.bulsi.egov.eauth.profile.rest.api.dto.Ekatte> collect = list.stream().map(ekatteEntity -> mapper.map(ekatteEntity, bg.bulsi.egov.eauth.profile.rest.api.dto.Ekatte.class))
                .collect(Collectors.toList()).stream().peek(ekatte -> {
                    ekatte.setDistrict(null);
                    ekatte.setMunicipality(null);
                    ekatte.setPlace(null);
                    ekatte.setType(null);
                }).collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(collect);
    }




}
