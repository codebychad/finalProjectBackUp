package com.project.schoolmanagment.repository.user;

import com.project.schoolmanagment.entity.concretes.user.Dean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeanRepository extends JpaRepository<Dean,Long> {

	boolean existsByUsername(String username);

	boolean existsBySsn(String ssn);

	boolean existsByPhoneNumber(String phoneNumber);

	Dean findByUsernameEquals(String username);

	List<Dean> findAllByNameOrUsername(String name, String username);
}
