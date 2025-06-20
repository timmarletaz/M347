package com.m347.pollit.repositories;

import com.m347.pollit.entities.Element;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElementRepository extends JpaRepository<Element, Integer> {
}
