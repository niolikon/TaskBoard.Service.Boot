package com.niolikon.taskboard.service.todo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niolikon.taskboard.service.config.SecurityConfig;
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

import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.niolikon.taskboard.service.todo.controller.TodoApiPaths.*;
import static com.niolikon.taskboard.service.todo.controller.testdata.TodoControllerTestData.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TodoController.class)
@Import({SecurityConfig.class, TodoControllerRelaySliceTest.TestSecurityBeans.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoControllerRelaySliceTest {
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

    Stream<Arguments> provideEndpointRequestServiceMockAndRelayVerify() throws JsonProcessingException {
        MockHttpServletRequestBuilder createRequest = post(API_PATH_TODO_BASE)
                .with(jwtRequest_withValidRole)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoRequest_valid_fromClient));
        Consumer<ITodoService> createTodoServiceMockSetup = service -> when(service.create(eq(VALID_USER_ID), any()))
                .thenReturn(todoView_expected_fromTodoRequest);
        Consumer<ITodoService> createTodoServiceMockVerify = service -> verify(service).create(eq(VALID_USER_ID), any());

        MockHttpServletRequestBuilder readAllRequest = get(API_PATH_TODO_BASE)
                .with(jwtRequest_withValidRole);
        Consumer<ITodoService> readAllServiceMockSetup = service -> when(service.readAll(eq(VALID_USER_ID), any()))
                .thenReturn(pageResponseTodoView_empty_fromRepository);
        Consumer<ITodoService> readAllServiceMockVerify = service -> verify(service).readAll(eq(VALID_USER_ID), any());

        MockHttpServletRequestBuilder readRequest = get(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID))
                .with(jwtRequest_withValidRole);
        Consumer<ITodoService> readServiceMockSetup = service -> when(service.read(VALID_USER_ID, VALID_TODO_ASSIGNED_ID))
                .thenReturn(todoView_expected_fromTodoRequest);
        Consumer<ITodoService> readServiceMockVerify = service -> verify(service).read(VALID_USER_ID, VALID_TODO_ASSIGNED_ID);

        MockHttpServletRequestBuilder updateRequest = put(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID))
                .with(jwtRequest_withValidRole)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoRequest_valid_fromClient));
        Consumer<ITodoService> updateServiceMockSetup = service -> when(service.update(eq(VALID_USER_ID), eq(VALID_TODO_ASSIGNED_ID), any()))
                .thenReturn(todoView_expected_fromTodoRequest);
        Consumer<ITodoService> updateServiceMockVerify = service -> verify(service).update(eq(VALID_USER_ID), eq(VALID_TODO_ASSIGNED_ID), any());

        MockHttpServletRequestBuilder patchRequest = patch(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID))
                .with(jwtRequest_withValidRole)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoPatch_valid_fromClient));
        Consumer<ITodoService> patchServiceMockSetup = service -> when(service.patch(eq(VALID_USER_ID), eq(VALID_TODO_ASSIGNED_ID), any()))
                .thenReturn(todoView_expected_fromTodoPatch);
        Consumer<ITodoService> patchServiceMockVerify = service -> verify(service).patch(eq(VALID_USER_ID), eq(VALID_TODO_ASSIGNED_ID), any());

        MockHttpServletRequestBuilder readAllPendingRequest = get(API_PATH_TODO_PENDING)
                .with(jwtRequest_withValidRole);
        Consumer<ITodoService> readAllPendingServiceMockSetup = service -> when(service.readAllPending(eq(VALID_USER_ID), any()))
                .thenReturn(pageResponseTodoView_empty_fromRepository);
        Consumer<ITodoService> readAllPendingServiceMockVerify = service -> verify(service).readAllPending(eq(VALID_USER_ID), any());

        MockHttpServletRequestBuilder readAllCompletedRequest = get(API_PATH_TODO_COMPLETED)
                .with(jwtRequest_withValidRole);
        Consumer<ITodoService> readAllCompletedServiceMockSetup = service -> when(service.readAllCompleted(eq(VALID_USER_ID), any()))
                .thenReturn(pageResponseTodoView_empty_fromRepository);
        Consumer<ITodoService> readAllCompletedServiceMockVerify = service -> verify(service).readAllCompleted(eq(VALID_USER_ID), any());

        MockHttpServletRequestBuilder deleteRequest = delete(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID))
                .with(jwtRequest_withValidRole);
        Consumer<ITodoService> deleteServiceMockVerify = service -> verify(service).delete(eq(VALID_USER_ID), any());

        return Stream.of(
                Arguments.of(createRequest, createTodoServiceMockSetup, createTodoServiceMockVerify),
                Arguments.of(readAllRequest, readAllServiceMockSetup, readAllServiceMockVerify),
                Arguments.of(readRequest, readServiceMockSetup, readServiceMockVerify),
                Arguments.of(updateRequest, updateServiceMockSetup, updateServiceMockVerify),
                Arguments.of(patchRequest, patchServiceMockSetup, patchServiceMockVerify),
                Arguments.of(readAllPendingRequest, readAllPendingServiceMockSetup, readAllPendingServiceMockVerify),
                Arguments.of(readAllCompletedRequest, readAllCompletedServiceMockSetup, readAllCompletedServiceMockVerify),
                Arguments.of(deleteRequest, null, deleteServiceMockVerify)
        );
    }

    @ParameterizedTest
    @MethodSource("provideEndpointRequestServiceMockAndRelayVerify")
    void givenValidRequest_whenExecutingEndpoint_thenRequestIsRelayedToService(MockHttpServletRequestBuilder endpointRequest,
                                                                               Consumer<ITodoService> serviceMockSetup,
                                                                               Consumer<ITodoService> serviceMockVerify) throws Exception {
        if (serviceMockSetup != null) serviceMockSetup.accept(todoService);
        mockMvc.perform(endpointRequest);
        assertDoesNotThrow(() -> serviceMockVerify.accept(todoService));
    }
}
