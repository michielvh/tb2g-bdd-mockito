package guru.springframework.sfgpetclinic.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.repositories.OwnerRepository;
import guru.springframework.sfgpetclinic.repositories.PetRepository;
import guru.springframework.sfgpetclinic.repositories.PetTypeRepository;
import guru.springframework.sfgpetclinic.services.springdatajpa.OwnerSDJpaService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {


    @Mock
    OwnerSDJpaService service;

    @InjectMocks
    OwnerController ownerController;

    @Mock
    BindingResult result;

    @Test
    void processCreationFormValid() {


        Owner owner = new Owner(5l,"first","last");
        given(result.hasErrors()).willReturn(false);

        when(service.save(any(Owner.class))).thenReturn(owner);

        String redirect = ownerController.processCreationForm(owner,result);

        verify(service).save(any(Owner.class));
        assertThat("redirect:/owners/5").isEqualTo(redirect);



    }

    @Test
    void processCreationFormHasErrors() {
        Owner owner = new Owner(1l,"first","last");
//setten het result om haserrors terug te gaven
     given(result.hasErrors()).willReturn(true);
        //  ==  when(result.hasErrors()).thenReturn(true);

        String viewName= ownerController.processCreationForm(owner,result);

        assertThat(viewName.equalsIgnoreCase("owners/createOrUpdateOwnerForm"));
        verify(result).hasErrors();

    }
}