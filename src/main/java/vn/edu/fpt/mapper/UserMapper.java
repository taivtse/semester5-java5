package vn.edu.fpt.mapper;

import org.modelmapper.ModelMapper;
import vn.edu.fpt.dto.UserDto;
import vn.edu.fpt.entity.UserEntity;

public class UserMapper implements AbstractMapper<UserDto, UserEntity> {
    @Override
    public UserDto entityToDto(UserEntity entity) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(entity, UserDto.class);
    }

    @Override
    public UserEntity dtoToEntity(UserDto dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, UserEntity.class);
    }
}
