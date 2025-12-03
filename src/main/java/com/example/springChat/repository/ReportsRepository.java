package com.example.springChat.repository;

import com.example.springChat.entity.Reports;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportsRepository extends JpaRepository<Reports, Long> {
}
