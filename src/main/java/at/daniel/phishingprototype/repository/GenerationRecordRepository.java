package at.daniel.phishingprototype.repository;

import at.daniel.phishingprototype.entity.GenerationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GenerationRecordRepository
        extends JpaRepository<GenerationRecord, UUID> {

    List<GenerationRecord> findAllByOrderByCreatedAtDesc();
}