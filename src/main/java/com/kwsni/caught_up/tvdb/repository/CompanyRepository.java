package com.kwsni.caught_up.tvdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.tvdb.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    
}
