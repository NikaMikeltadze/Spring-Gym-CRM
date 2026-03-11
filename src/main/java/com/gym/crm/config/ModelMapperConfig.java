package com.gym.crm.config;

import com.gym.crm.dto.request.training.AddTrainingRequest;
import com.gym.crm.dto.response.training.TrainingTypeInfo;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<TrainingType, TrainingTypeInfo>() {
            @Override
            protected void configure() {
                map().setTrainingTypeId(source.getId());
                map().setTrainingTypeName(source.getName());
            }
        });

        return modelMapper;
    }

}
