/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vets.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.samples.petclinic.vets.model.Specialty;


import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.ServletException;

/**
 * @author Maciej Szarlinski
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(VetResource.class)
@ActiveProfiles("test")
class VetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    VetRepository vetRepository;

    // @Test
    // void shouldGetAListOfVets() throws Exception {

    //     Vet vet = new Vet();
    //     vet.setId(1);

    //     given(vetRepository.findAll()).willReturn(asList(vet));

    //     mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
    //         .andExpect(status().isOk())
    //         .andExpect(jsonPath("$[0].id").value(1));
    // }
    @Test
    void shouldGetAListOfVets() throws Exception {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("John");
        vet.setLastName("Doe");

        Specialty specialty = new Specialty();
        //specialty.setId(1);
        specialty.setName("surgery");

        vet.addSpecialty(specialty);

        given(vetRepository.findAll()).willReturn(asList(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Doe"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("surgery"));
    }

    @Test
    void shouldReturnEmptyListWhenNoVetsFound() throws Exception {
        given(vetRepository.findAll()).willReturn(Collections.emptyList());

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }
    
    @Test
    void shouldReturnVetWithNoSpecialties() throws Exception {
        Vet vet = new Vet();
        vet.setId(2);
        vet.setFirstName("Jane");
        vet.setLastName("Smith");

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(2))
            .andExpect(jsonPath("$[0].firstName").value("Jane"))
            .andExpect(jsonPath("$[0].lastName").value("Smith"))
            .andExpect(jsonPath("$[0].specialties").isEmpty());
    }

    @Test
    void shouldReturnSpecialtiesSortedByName() throws Exception {
        Vet vet = new Vet();
        vet.setId(3);
        vet.setFirstName("Alice");
        vet.setLastName("Nguyen");

        Specialty s1 = new Specialty();
        s1.setName("Dentistry");

        Specialty s2 = new Specialty();
        s2.setName("Anesthesiology");

        vet.addSpecialty(s1);
        vet.addSpecialty(s2);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties[0].name").value("Anesthesiology"))
            .andExpect(jsonPath("$[0].specialties[1].name").value("Dentistry"));
    }

    @Test
    void shouldReturnMultipleVets() throws Exception {
        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("John");
        vet1.setLastName("Doe");

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Jane");
        vet2.setLastName("Smith");

        Specialty specialty = new Specialty();
        specialty.setName("Surgery");
        vet1.addSpecialty(specialty);

        given(vetRepository.findAll()).willReturn(asList(vet1, vet2));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void shouldReturnVetWithMultipleSpecialties() throws Exception {
        Vet vet = new Vet();
        vet.setId(4);
        vet.setFirstName("Bob");
        vet.setLastName("Johnson");

        Specialty s1 = new Specialty();
        s1.setName("Cardiology");

        Specialty s2 = new Specialty();
        s2.setName("Neurology");

        Specialty s3 = new Specialty();
        s3.setName("Oncology");

        vet.addSpecialty(s1);
        vet.addSpecialty(s2);
        vet.addSpecialty(s3);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties.length()").value(3))
            .andExpect(jsonPath("$[0].specialties[0].name").value("Cardiology"))
            .andExpect(jsonPath("$[0].specialties[1].name").value("Neurology"))
            .andExpect(jsonPath("$[0].specialties[2].name").value("Oncology"));
    }

    @Test
    void shouldReturnVetWithNullSpecialties() throws Exception {
        Vet vet = new Vet();
        vet.setId(5);
        vet.setFirstName("Charlie");
        vet.setLastName("Brown");

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(5))
            .andExpect(jsonPath("$[0].specialties").isEmpty());
    }

    @Test
    void shouldReturnVetWithEmptySpecialties() throws Exception {
        Vet vet = new Vet();
        vet.setId(6);
        vet.setFirstName("David");
        vet.setLastName("Wilson");

        // Add and then clear specialties to test empty set
        Specialty specialty = new Specialty();
        specialty.setName("Test");
        vet.addSpecialty(specialty);
        // Clear specialties by setting to null and then accessing
        vet.setSpecialties(null);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(6))
            .andExpect(jsonPath("$[0].specialties").isEmpty());
    }

    @Test
    void shouldReturnVetWithSpecialtiesContainingNullNames() throws Exception {
        Vet vet = new Vet();
        vet.setId(7);
        vet.setFirstName("Eva");
        vet.setLastName("Garcia");

        Specialty s1 = new Specialty();
        s1.setName(null);

        Specialty s2 = new Specialty();
        s2.setName("Valid Specialty");

        vet.addSpecialty(s1);
        vet.addSpecialty(s2);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties.length()").value(2));
    }

    @Test
    void shouldReturnVetWithSpecialtiesContainingEmptyNames() throws Exception {
        Vet vet = new Vet();
        vet.setId(8);
        vet.setFirstName("Frank");
        vet.setLastName("Miller");

        Specialty s1 = new Specialty();
        s1.setName("");

        Specialty s2 = new Specialty();
        s2.setName("Valid Specialty");

        vet.addSpecialty(s1);
        vet.addSpecialty(s2);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties.length()").value(2));
    }

    @Test
    void shouldReturnVetWithSpecialtiesContainingSpecialCharacters() throws Exception {
        Vet vet = new Vet();
        vet.setId(9);
        vet.setFirstName("Grace");
        vet.setLastName("O'Connor");

        Specialty s1 = new Specialty();
        s1.setName("Cardio-Thoracic Surgery");

        Specialty s2 = new Specialty();
        s2.setName("Emergency & Critical Care");

        vet.addSpecialty(s1);
        vet.addSpecialty(s2);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties.length()").value(2))
            .andExpect(jsonPath("$[0].specialties[0].name").value("Cardio-Thoracic Surgery"))
            .andExpect(jsonPath("$[0].specialties[1].name").value("Emergency & Critical Care"));
    }

    @Test
    void shouldReturnVetWithVeryLongSpecialtyName() throws Exception {
        Vet vet = new Vet();
        vet.setId(10);
        vet.setFirstName("Henry");
        vet.setLastName("Thompson");

        Specialty s1 = new Specialty();
        s1.setName("A".repeat(1000)); // Very long name

        vet.addSpecialty(s1);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties.length()").value(1));
    }

    @Test
    void shouldReturnVetWithUnicodeSpecialtyNames() throws Exception {
        Vet vet = new Vet();
        vet.setId(11);
        vet.setFirstName("Isabella");
        vet.setLastName("Rodríguez");

        Specialty s1 = new Specialty();
        s1.setName("Cardiología");

        Specialty s2 = new Specialty();
        s2.setName("Neurología");

        vet.addSpecialty(s1);
        vet.addSpecialty(s2);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties.length()").value(2))
            .andExpect(jsonPath("$[0].specialties[0].name").value("Cardiología"))
            .andExpect(jsonPath("$[0].specialties[1].name").value("Neurología"));
    }

    @Test
    void shouldReturnVetWithRepositoryException() throws Exception {
        given(vetRepository.findAll()).willThrow(new RuntimeException("Database error"));

        assertThrows(ServletException.class, () -> {
            mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void shouldReturnVetWithNullFirstName() throws Exception {
        Vet vet = new Vet();
        vet.setId(12);
        vet.setFirstName(null);
        vet.setLastName("Smith");

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(12))
            .andExpect(jsonPath("$[0].firstName").isEmpty());
    }

    @Test
    void shouldReturnVetWithNullLastName() throws Exception {
        Vet vet = new Vet();
        vet.setId(13);
        vet.setFirstName("John");
        vet.setLastName(null);

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(13))
            .andExpect(jsonPath("$[0].lastName").isEmpty());
    }

    @Test
    void shouldReturnVetWithEmptyFirstName() throws Exception {
        Vet vet = new Vet();
        vet.setId(14);
        vet.setFirstName("");
        vet.setLastName("Smith");

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(14))
            .andExpect(jsonPath("$[0].firstName").value(""));
    }

    @Test
    void shouldReturnVetWithEmptyLastName() throws Exception {
        Vet vet = new Vet();
        vet.setId(15);
        vet.setFirstName("John");
        vet.setLastName("");

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(15))
            .andExpect(jsonPath("$[0].lastName").value(""));
    }

    @Test
    void shouldReturnVetWithZeroId() throws Exception {
        Vet vet = new Vet();
        vet.setId(0);
        vet.setFirstName("Zero");
        vet.setLastName("Vet");

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(0));
    }

    @Test
    void shouldReturnVetWithNegativeId() throws Exception {
        Vet vet = new Vet();
        vet.setId(-1);
        vet.setFirstName("Negative");
        vet.setLastName("Vet");

        given(vetRepository.findAll()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(-1));
    }
}
