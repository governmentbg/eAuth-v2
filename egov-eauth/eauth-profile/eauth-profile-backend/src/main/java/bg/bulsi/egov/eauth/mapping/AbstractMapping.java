package bg.bulsi.egov.eauth.mapping;

import org.modelmapper.ModelMapper;


public abstract class AbstractMapping implements MappingDefinition {

    @Override
    public final void mapping(ModelMapper modelMapper) {
        fromDtoToEntity(modelMapper);
        fromEntityToDto(modelMapper);
    }

    abstract void fromDtoToEntity(ModelMapper modelMapper);

    abstract void fromEntityToDto(ModelMapper modelMapper);

}
