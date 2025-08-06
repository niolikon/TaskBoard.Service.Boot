package com.niolikon.taskboard.service.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niolikon.taskboard.service.config.SecurityConfig;
import com.niolikon.taskboard.service.todo.dto.TodoPatch;
import com.niolikon.taskboard.service.todo.dto.TodoRequest;
import com.niolikon.taskboard.service.todo.service.ITodoService;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.Date;
import java.util.stream.Stream;

import static com.niolikon.taskboard.service.todo.controller.TodoApiPaths.*;
import static com.niolikon.taskboard.service.todo.controller.testdata.TodoControllerTestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@Import({SecurityConfig.class, TodoControllerValidationSliceTest.TestSecurityBeans.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoControllerValidationSliceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ITodoService todoService;

    @TestConfiguration
    static class TestSecurityBeans {
        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
            return new JwtAuthenticationConverter();
        }
    }

    Stream<Arguments> provideInvalidTodoPatch() {
        TodoPatch patchWithPastDueDate = new TodoPatch(VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, false, Date.from(INSTANT_IN_THE_PAST));
        return Stream.of(
                Arguments.of(patchWithPastDueDate)
        );
    }

    Stream<Arguments> provideInvalidTodoRequests() {
        TodoRequest requestWithNullTitle = new TodoRequest(null, VALID_TODO_DESCRIPTION, false, Date.from(INSTANT_IN_THE_FUTURE));
        TodoRequest requestWithEmptyTitle = new TodoRequest("", VALID_TODO_DESCRIPTION, false, Date.from(INSTANT_IN_THE_FUTURE));
        TodoRequest requestWithNullDescription = new TodoRequest(VALID_TODO_TITLE, null, false, Date.from(INSTANT_IN_THE_FUTURE));
        TodoRequest requestWithEmptyDescription = new TodoRequest(VALID_TODO_TITLE, "", false, Date.from(INSTANT_IN_THE_FUTURE));
        TodoRequest requestWithNullIsCompleted = new TodoRequest(VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, null, Date.from(INSTANT_IN_THE_FUTURE));
        TodoRequest requestWithNullDueDate = new TodoRequest(VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, false, null);
        TodoRequest requestWithPastDueDate = new TodoRequest(VALID_TODO_TITLE, VALID_TODO_DESCRIPTION, false, Date.from(INSTANT_IN_THE_PAST));
        return Stream.of(
                Arguments.of(requestWithNullTitle),
                Arguments.of(requestWithEmptyTitle),
                Arguments.of(requestWithNullDescription),
                Arguments.of(requestWithEmptyDescription),
                Arguments.of(requestWithNullIsCompleted),
                Arguments.of(requestWithNullDueDate),
                Arguments.of(requestWithPastDueDate)
        );
    }

    static Stream<Arguments> provideTodoPatchBodiedEndpoint() {
        MockHttpServletRequestBuilder patchRequest = patch(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID))
                .with(jwtRequest_withValidRole);

        return Stream.of(
                Arguments.of(patchRequest)
        );
    }

    static Stream<Arguments> provideTodoRequestBodiedEndpoint() {
        MockHttpServletRequestBuilder createRequest = post(API_PATH_TODO_BASE)
                .with(jwtRequest_withValidRole);
        MockHttpServletRequestBuilder updateRequest = put(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID))
                .with(jwtRequest_withValidRole);

        return Stream.of(
                Arguments.of(createRequest),
                Arguments.of(updateRequest)
        );
    }

    Stream<Arguments> provideTodoPatchBodiedEndpointWithInvalidTodoPatchBody(){
        return provideTodoPatchBodiedEndpoint().flatMap(a ->
                provideInvalidTodoPatch().map(b ->
                        Arguments.of(a.get()[0], b.get()[0])
                )
        );
    }

    Stream<Arguments> provideTodoRequestBodiedEndpointWithInvalidTodoRequestBody(){
        return provideTodoRequestBodiedEndpoint().flatMap(a ->
                provideInvalidTodoRequests().map(b ->
                        Arguments.of(a.get()[0], b.get()[0])
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTodoPatchBodiedEndpointWithInvalidTodoPatchBody")
    void givenInvalidTodoPatch_whenAccessingBodiedEndpoint_thenReturnsBadRequest(MockHttpServletRequestBuilder endpointRequest, TodoPatch invalidTodoPatch) throws Exception {
        mockMvc.perform(endpointRequest
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTodoPatch)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("provideTodoRequestBodiedEndpointWithInvalidTodoRequestBody")
    void givenInvalidTodoRequest_whenAccessingBodiedEndpoint_thenReturnsBadRequest(MockHttpServletRequestBuilder endpointRequest, TodoRequest invalidTodoRequest) throws Exception {
        mockMvc.perform(endpointRequest
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTodoRequest)))
                .andExpect(status().isBadRequest());
    }
}
