package guru.springframework.sfgpetclinic.controllers;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
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
    Model model;

    @Mock
    OwnerSDJpaService service;

    @InjectMocks
    OwnerController ownerController;

    @Mock
    BindingResult result;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;


    //gaat invocation halen uit test methode bij :ownerController.processFindForm(owner, result, null);
    //de owner == 0 -> lastname
    //de ownerservice.findAllByLastNameLike() voegt zelf de %% toe -> check op %*%
    @BeforeEach
    void setUp() {
        given(service.findAllByLastNameLike(stringArgumentCaptor.capture()))
                .willAnswer(invocation -> {
                    List<Owner> owners = new ArrayList<>();

              // param is getting passed in by test
                    String name = invocation.getArgument(0);

                    if(name.equals("%Buck%")){
                        owners.add(new Owner(1l, "Joe", "Buck"));
                        return owners;
                    } else if (name.equals("%DontFindMe%")) {
                        return owners;
                    } else if(name.equals("%multipleHits%")) {
                        owners.add(new Owner(1l, "Joe", "Buck"));
                        owners.add(new Owner(2l, "Joe2", "Buck2"));
                        return owners;
                    }

                    throw new RuntimeException("Invalid Argument");
                });
    }

/*    @Test
    void processFindFormWildcardString() {
        //given
        Owner owner = new Owner(1l, "Joe", "Buck");
        List<Owner> ownerList = new ArrayList<>();
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        // when invoked, capture argument passed into
        given(service.findAllByLastNameLike(captor.capture())).willReturn(ownerList);

        //when
        String viewName = ownerController.processFindForm(owner, result, null);

        //then
        // only checks the captured arguments!
        assertThat("%Buck%").isEqualToIgnoringCase(captor.getValue());
    }*/

    @Test
    void processFindFormWildcardStringAnnotation() {
        //given
        Owner owner = new Owner(1l, "Joe", "Buck");

// REPLACED WITH BEFORE EACH
       // List<Owner> ownerList = new ArrayList<>();
        // when invoked, capture argument passed into
     //   given(service.findAllByLastNameLike(stringArgumentCaptor.capture())).willReturn(ownerList);

        //when
        String viewName = ownerController.processFindForm(owner, result, null);

        //then
        // only checks the captured arguments!
        assertThat("%Buck%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("redirect:/owners/1").isEqualToIgnoringCase(viewName);
    }

    @Test
    void processFindFormWildcardNotFound() {
        //given
        Owner owner = new Owner(1l, "Joe", "DontFindMe");

        //when
        String viewName = ownerController.processFindForm(owner, result, null);

        //then
        // only checks the captured arguments!
        assertThat("%DontFindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
    }

    @Test
    void processFindFormWildcardMultipleFound() {
        //given
        Owner owner = new Owner(1l, "Joe", "multipleHits");

        //when
        String viewName = ownerController.processFindForm(owner, result, model);

        //then
        // only checks the captured arguments!
        assertThat("%multipleHits%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);
    }

    @Test
    void processCreationFormValid() {

        //given
        Owner owner = new Owner(5l,"first","last");
        given(result.hasErrors()).willReturn(false);
        when(service.save(any(Owner.class))).thenReturn(owner);

        //when
        String redirect = ownerController.processCreationForm(owner,result);

        //then
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