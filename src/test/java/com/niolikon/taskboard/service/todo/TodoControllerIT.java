package com.niolikon.taskboard.service.todo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.framework.test.annotations.WithIsolatedDataJpaTestScenario;
import com.niolikon.taskboard.framework.test.containers.PostgreSQLTestContainersConfig;
import com.niolikon.taskboard.framework.test.extensions.IsolatedDataJpaTestScenarioExtension;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.scenarios.MultipleTodosWithPaginationTestScenario;
import com.niolikon.taskboard.service.todo.scenarios.SingleTodoTestScenario;
import com.niolikon.taskboard.service.todo.scenarios.SingleTodoWithFixedDueDateTestScenario;
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

        PageResponse<TodoView> results = objectMapper.readValue(
                jsonResponse,
                new TypeReference<>() {}
        );

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getElementsTotal()).isGreaterThan(0);
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

        PageResponse<TodoView> results = objectMapper.readValue(
                jsonResponse,
                new TypeReference<>() {}
        );

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getElementsTotal()).isGreaterThan(0);
    }

    @Test
    @WithIsolatedDataJpaTestScenario(dataClass = SingleTodoWithFixedDueDateTestScenario.class)
    @Tag("Bugfix=TBS6")
    void givenSingleTodo_whenReadTodo_thenDueDateIsFormattedCorrectly() throws Exception {
        mockMvc.perform(
                        get("/api/Todos/pending")
                                .with(jwt()
                                        .jwt(jwt -> jwt.subject(SingleTodoTestScenario.USER_UUID))
                                        .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].DueDate").value(SingleTodoWithFixedDueDateTestScenario.FIXED_DUE_DATE_FORMATTED));
    }

    @Test
    @WithIsolatedDataJpaTestScenario(dataClass = MultipleTodosWithPaginationTestScenario.class)
    @Tag("Story=TBS8")
    @Tag("Scenario=1")
    void givenMoreTodosThenPageSize_whenFetchingTodosWithPagination_thenSystemReturnsTheFirstSubsetWithPaginationMetadata() throws Exception {
        mockMvc.perform(
                        get("/api/Todos/pending")
                                .param("page", String.valueOf(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1.getPageNumber()))
                                .param("size", String.valueOf(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1.getPageSize()))
                                .with(jwt()
                                        .jwt(jwt -> jwt.subject(SingleTodoTestScenario.USER_UUID))
                                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1_ELEMENTS_SIZE))
                .andExpect(jsonPath("$.elementsSize").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1_ELEMENTS_SIZE))
                .andExpect(jsonPath("$.elementsTotal").value(MultipleTodosWithPaginationTestScenario.DATASET_ELEMENTS_TOTAL))
                .andExpect(jsonPath("$.pageNumber").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1.getPageNumber()))
                .andExpect(jsonPath("$.pageSize").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1.getPageSize()))
                .andExpect(jsonPath("$.pageTotal").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1_PAGE_TOTAL))
                .andExpect(jsonPath("$.first").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1_FIRST))
                .andExpect(jsonPath("$.last").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1_LAST))
                .andExpect(jsonPath("$.empty").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY1_EMPTY));
    }


    @Test
    @WithIsolatedDataJpaTestScenario(dataClass = MultipleTodosWithPaginationTestScenario.class)
    @Tag("Story=TBS8")
    @Tag("Scenario=2")
    void givenMultiplePagesOfTodosExist_whenTheClientRequestsSpecificPageNumber_thenSystemReturnsTheCorrectPageWithPaginationMetadata() throws Exception {
        mockMvc.perform(
                        get("/api/Todos/pending")
                                .param("page", String.valueOf(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2.getPageNumber()))
                                .param("size", String.valueOf(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2.getPageSize()))
                                .with(jwt()
                                        .jwt(jwt -> jwt.subject(SingleTodoTestScenario.USER_UUID))
                                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2_ELEMENTS_SIZE))
                .andExpect(jsonPath("$.elementsSize").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2_ELEMENTS_SIZE))
                .andExpect(jsonPath("$.elementsTotal").value(MultipleTodosWithPaginationTestScenario.DATASET_ELEMENTS_TOTAL))
                .andExpect(jsonPath("$.pageNumber").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2.getPageNumber()))
                .andExpect(jsonPath("$.pageSize").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2.getPageSize()))
                .andExpect(jsonPath("$.pageTotal").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2_PAGE_TOTAL))
                .andExpect(jsonPath("$.first").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2_FIRST))
                .andExpect(jsonPath("$.last").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2_LAST))
                .andExpect(jsonPath("$.empty").value(MultipleTodosWithPaginationTestScenario.SAMPLE_QUERY2_EMPTY));
    }
}
