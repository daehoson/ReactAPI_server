package org.daeho.apiserver.repository;

import org.daeho.apiserver.domain.Todo;
import org.daeho.apiserver.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {
}
