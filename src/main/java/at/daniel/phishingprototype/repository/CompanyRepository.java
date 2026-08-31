package at.daniel.phishingprototype.repository;

import at.daniel.phishingprototype.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}