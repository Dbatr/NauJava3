package ru.denis.NauJava3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
