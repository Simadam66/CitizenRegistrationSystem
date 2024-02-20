package com.dsoft.CitizenRegistrationSystem.service;

import com.dsoft.CitizenRegistrationSystem.model.Citizen;
import com.dsoft.CitizenRegistrationSystem.repository.CitizenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.time.Month.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CitizenServiceTest {

    @Mock
    CitizenRepository repository;

    @InjectMocks
    CitizenService service;

    private static final String USER_1_IDENTITYCARD = "969756ME";

    private static final String FAKE_IDENTITYCARD = "999999ZZ";

    private static final Citizen CITIZEN_1 =
            Citizen.builder()
                    .id("65d3509b22eba52e770cdc67")
                    .name("Lajos")
                    .birthdate(LocalDate.of(1969, JULY, 14))
                    .identityCard("969756ME")
                    .build();

    private static final Citizen CITIZEN_2 =
            Citizen.builder()
                    .id("65d350b522eba52e770cdc68")
                    .name("Pista")
                    .birthdate(LocalDate.of(1970, AUGUST, 9))
                    .identityCard("987756LO")
                    .build();

    @Test
    void getByIdentityCardReturnsRightCitizen() {
        when(repository.findByIdentityCard(anyString())).thenReturn(Optional.of(CITIZEN_1));

        Citizen citizen = service.getByIdentityCard(USER_1_IDENTITYCARD);

        verify(repository, times(1)).findByIdentityCard(USER_1_IDENTITYCARD);
        assertEquals(CITIZEN_1, citizen);
    }

    @Test
    void getByIdentityCardThrowsNoSuchElementException() {
        when(repository.findByIdentityCard(anyString())).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> service.getByIdentityCard(FAKE_IDENTITYCARD));
        assertEquals("No citizen found with the provided identity card", ex.getMessage());
        verify(repository, times(1)).findByIdentityCard(FAKE_IDENTITYCARD);
    }
}
