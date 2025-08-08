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

import java.util.stream.Stream;

import static com.niolikon.taskboard.service.todo.controller.TodoApiPaths.*;
import static com.niolikon.taskboard.service.todo.controller.testdata.TodoControllerTestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@Import({SecurityConfig.class, TodoControllerSecuritySliceTest.TestSecurityBeans.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoControllerSecuritySliceTest {
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

    Stream<Arguments> provideSecuredEndpointRequest() throws JsonProcessingException {
        MockHttpServletRequestBuilder createRequest = post(API_PATH_TODO_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoRequest_valid_fromClient));
        MockHttpServletRequestBuilder readAllRequest = get(API_PATH_TODO_BASE);
        MockHttpServletRequestBuilder readRequest = get(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID));
        MockHttpServletRequestBuilder updateRequest = put(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoRequest_valid_fromClient));
        MockHttpServletRequestBuilder patchRequest = patch(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoPatch_valid_fromClient));
        MockHttpServletRequestBuilder readAllPendingRequest = get(API_PATH_TODO_PENDING);
        MockHttpServletRequestBuilder readAllCompletedRequest = get(API_PATH_TODO_COMPLETED);
        MockHttpServletRequestBuilder deleteRequest = delete(new UriTemplate(API_PATH_TODO_BY_ID).expand(VALID_TODO_ASSIGNED_ID));

        return Stream.of(
                Arguments.of(createRequest),
                Arguments.of(readAllRequest),
                Arguments.of(readRequest),
                Arguments.of(updateRequest),
                Arguments.of(patchRequest),
                Arguments.of(readAllPendingRequest),
                Arguments.of(readAllCompletedRequest),
                Arguments.of(deleteRequest)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSecuredEndpointRequest")
    void givenNoAuthorities_whenAccessingSecuredEndpoint_thenReturnsForbidden(MockHttpServletRequestBuilder endpointRequest) throws Exception {
        mockMvc.perform(endpointRequest
                        .with(jwtRequest_withoutAuthorities))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("provideSecuredEndpointRequest")
    void givenWrongAuthorities_whenAccessingSecuredEndpoint_thenReturnsForbidden(MockHttpServletRequestBuilder endpointRequest) throws Exception {
        mockMvc.perform(endpointRequest
                        .with(jwtRequest_withInvalidRole))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("provideSecuredEndpointRequest")
    void givenNoJwt_whenAccessingSecuredEndpoint_thenReturnsUnauthorized(MockHttpServletRequestBuilder endpointRequest) throws Exception {
        mockMvc.perform(endpointRequest)
                .andExpect(status().isUnauthorized());
    }
}
