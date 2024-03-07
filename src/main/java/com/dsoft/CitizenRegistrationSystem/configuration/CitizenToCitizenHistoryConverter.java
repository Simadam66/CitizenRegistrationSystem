package com.dsoft.CitizenRegistrationSystem.configuration;

import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import com.dsoft.CitizenRegistrationSystem.model.CitizenHistory;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.LocalDateTime;

public class CitizenToCitizenHistoryConverter implements Converter<Citizen, CitizenHistory> {
    @Override
    public CitizenHistory convert(MappingContext<Citizen, CitizenHistory> mappingContext) {
        return CitizenHistory.builder()
                .id(null)
                .citizenId(mappingContext.getSource().getId())
                .timestamp(LocalDateTime.now())
                .name(mappingContext.getSource().getName())
                .birthdate(mappingContext.getSource().getBirthdate())
                .identityCard(mappingContext.getSource().getIdentityCard())
                .build();
    }
}
