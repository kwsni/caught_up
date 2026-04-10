package com.kwsni.caught_up.tvdb.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;

public interface UpdateRecordRepository extends JpaRepository<UpdateRecord, Long> {
    
}
