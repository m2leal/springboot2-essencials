package academy.devdojo.springboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.devdojo.springboot2.domain.DevDojoUser;

public interface DevDojoUserRepository extends JpaRepository<DevDojoUser, Long> {
	
	DevDojoUser findByUsername(String username);
	
}
