package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public User findByEmail(String email);

	boolean existsByEmail(String email);

}
