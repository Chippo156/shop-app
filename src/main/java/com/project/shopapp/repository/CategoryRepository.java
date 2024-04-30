package com.project.shopapp.repository;

import com.project.shopapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
public interface CategoryRepository extends JpaRepository<Category, Long>{
}
