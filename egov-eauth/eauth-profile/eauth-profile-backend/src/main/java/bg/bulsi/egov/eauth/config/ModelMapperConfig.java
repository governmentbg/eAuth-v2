package bg.bulsi.egov.eauth.config;


import bg.bulsi.egov.eauth.mapping.MappingDefinition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan("bg.bulsi.egov.eauth.mapping")
public class ModelMapperConfig {

    @Bean
    public ModelMapper mapper(List<MappingDefinition> definitions) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFullTypeMatchingRequired(true);


        definitions.forEach(mappingDefinition -> mappingDefinition.mapping(modelMapper));

        return modelMapper;
    }
}