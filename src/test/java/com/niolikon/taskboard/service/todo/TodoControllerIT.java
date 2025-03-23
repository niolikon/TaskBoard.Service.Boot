package com.niolikon.taskboard.service.todo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niolikon.taskboard.framework.test.annotations.WithIsolatedDataJpaTestScenario;
import com.niolikon.taskboard.framework.test.containers.PostgreSQLTestContainersConfig;
import com.niolikon.taskboard.framework.test.extensions.IsolatedDataJpaTestScenarioExtension;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.scenarios.SingleTodoTestScenario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgreSQLTestContainersConfig.class)
@ExtendWith(IsolatedDataJpaTestScenarioExtension.class)
class TodoControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithIsolatedDataJpaTestScenario(dataClass = SingleTodoTestScenario.class)
    @Tag("Story=TBS3")
    @Tag("Scenario=1")
    void givenSingleTodo_whenReadAll_thenListWithSingleTodoIsReturned() throws Exception {
        String jsonResponse = mockMvc.perform(
                get("/api/Todos")
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(SingleTodoTestScenario.USER_UUID))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        List<TodoView> results = Arrays.asList(objectMapper.readValue(jsonResponse, TodoView[].class));

        assertThat(results).hasSize(1);
    }

    @Test
    @WithIsolatedDataJpaTestScenario(dataClass = SingleTodoTestScenario.class)
    @Tag("Story=TBS3")
    @Tag("Scenario=1")
    void givenSingleTodo_whenReadAllPending_thenSingleTodoIsReturned() throws Exception {
        String jsonResponse = mockMvc.perform(
                        get("/api/Todos/pending")
                                .with(jwt()
                                        .jwt(jwt -> jwt.subject(SingleTodoTestScenario.USER_UUID))
                                        .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        List<TodoView> results = Arrays.asList(objectMapper.readValue(jsonResponse, TodoView[].class));

        assertThat(results).hasSize(1);
    }
}
