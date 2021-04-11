package com.shelter.animalback.component.api.animal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shelter.animalback.repository.AnimalRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class SaveAnimalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    @SneakyThrows
    public void createAnimalSuccessful() {
        // Arrange animal data
        var animal = new CreateAnimalRequestBody();
        animal.setName("Hela");
        animal.setBreed("Mestizo");
        animal.setGender("Femenino");
        animal.setVaccinated(true);

        var createAnimalRequestBody = new ObjectMapper().writeValueAsString(animal);

        // Action request
        var response = mockMvc.perform(
                post("/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAnimalRequestBody))
                .andReturn()
                .getResponse();
        var animalResponse = new ObjectMapper().readValue(response.getContentAsString(), CreateAnimalResponse.class);

        // Asserts Http response
        assertThat(animalResponse.getName(), equalTo("Hela"));
        assertThat(animalResponse.getBreed(), equalTo("Mestizo"));
        assertThat(animalResponse.getGender(), equalTo("Femenino"));
        assertThat(animalResponse.isVaccinated(), equalTo(true));
        assertThat(animalResponse.getId(), notNullValue());

        // Database asserts
        var dbQuery = animalRepository.findById(animalResponse.getId());
        assertThat(dbQuery.isPresent(), is(true));

        var animalDB = dbQuery.get();
        assertThat(animalDB.getName(), equalTo("Hela"));
        assertThat(animalDB.getBreed(), equalTo("Mestizo"));
        assertThat(animalDB.getGender(), equalTo("Femenino"));
        assertThat(animalDB.isVaccinated(), equalTo(true));
    }

    @Setter
    @Getter
    @NoArgsConstructor
    private class CreateAnimalRequestBody {
        private String name;
        private String breed;
        private String gender;
        private boolean isVaccinated;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    private static class CreateAnimalResponse {
        private long id;
        private String name;
        private String breed;
        private String gender;
        private boolean vaccinated;
        private String[] vaccines;
    }
}
