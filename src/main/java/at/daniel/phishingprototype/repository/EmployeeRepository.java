package at.daniel.phishingprototype.repository;

import at.daniel.phishingprototype.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
}