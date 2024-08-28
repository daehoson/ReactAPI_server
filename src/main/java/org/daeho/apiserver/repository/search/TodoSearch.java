package org.daeho.apiserver.repository.search;

import org.daeho.apiserver.domain.Todo;
import org.daeho.apiserver.dto.PageRequestDTO;
import org.springframework.data.domain.Page;

public interface TodoSearch {
    Page<Todo> search1(PageRequestDTO pageRequestDTO);
}
