package com.niolikon.taskboard.service.todo.service;

import com.niolikon.taskboard.framework.data.dto.PageResponse;
import com.niolikon.taskboard.service.todo.TodoMapper;
import com.niolikon.taskboard.service.todo.TodoRepository;
import com.niolikon.taskboard.service.todo.dto.TodoView;
import com.niolikon.taskboard.service.todo.model.Todo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static com.niolikon.taskboard.service.todo.service.testdata.TodoServiceTestData.*;

@ExtendWith(MockitoExtension.class)
class TodoServicePaginationUnitTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private TodoMapper todoMapper;
    @InjectMocks
    private TodoService todoService;

    @Test
    @Tag("Story=TBS8")
    @Tag("Scenario=1")
    void givenQueryProducesMultiplePages_whenClientRequestsFirstPage_thenReturnsFirstPageWithMetadata() {
        // Arrange
        Pageable firstPage = pageable_firstPageSize2_fromClient;
        List<Todo> todos = List.of(todo_instance1_fromRepository, todo_instance2_fromRepository);
        Page<Todo> todosPaged = new PageImpl<>(todos, firstPage, 5);
        when(todoRepository.findByOwnerUid(VALID_OWNER_UID, firstPage)).thenReturn(todosPaged);
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(
                todoView_mapped1_fromTodoInstance,
                todoView_mapped2_fromTodoInstance
        );

        // Act
        PageResponse<TodoView> result = todoService.readAll(VALID_OWNER_UID, firstPage);

        // Assert
        Assertions.assertThat(result.getContent()).hasSize(2);
        assertThat(result)
                .extracting(
                        PageResponse::getElementsSize,
                        PageResponse::getElementsTotal,
                        PageResponse::getPageNumber,
                        PageResponse::getPageSize,
                        PageResponse::getPageTotal,
                        PageResponse::isFirst,
                        PageResponse::isLast,
                        PageResponse::isEmpty
                )
                .containsExactly(2, 5L, 0, 2, 3, true, false, false);

        verify(todoRepository).findByOwnerUid(VALID_OWNER_UID, firstPage);
    }

    @Test
    @Tag("Story=TBS8")
    @Tag("Scenario=2")
    void givenQueryProducesMultiplePages_whenClientRequestsSpecificPage_thenCorrectPageReturnedWithMetadata() {
        // Arrange
        Pageable secondPage = pageable_secondPageSize2_fromClient;
        List<Todo> todos = List.of(todo_instance3_fromRepository, todo_instance4_fromRepository);
        Page<Todo> todosPaged = new PageImpl<>(todos, secondPage, 5);

        when(todoRepository.findByOwnerUid(VALID_OWNER_UID, secondPage)).thenReturn(todosPaged);
        when(todoMapper.toTodoView(any(Todo.class))).thenReturn(
                todoView_mapped3_fromTodoInstance,
                todoView_mapped4_fromTodoInstance
        );

        // Act
        PageResponse<TodoView> result = todoService.readAll(VALID_OWNER_UID, secondPage);

        // Assert
        Assertions.assertThat(result.getContent()).hasSize(2);
        assertThat(result)
                .extracting(
                        PageResponse::getElementsSize,
                        PageResponse::getElementsTotal,
                        PageResponse::getPageNumber,
                        PageResponse::getPageSize,
                        PageResponse::getPageTotal,
                        PageResponse::isFirst,
                        PageResponse::isLast,
                        PageResponse::isEmpty
                )
                .containsExactly(2, 5L, 1, 2, 3, false, false, false);

        verify(todoRepository).findByOwnerUid(VALID_OWNER_UID, secondPage);
    }
}
