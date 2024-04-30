package com.project.shopapp.repository;

import com.project.shopapp.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long>{
}
