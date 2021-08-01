package com.hyeon.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyeon.security.model.User;

// CRUD 함수를 JPARepository가 들고 있음.
// @Repository라는 어노테이션이 없어도 IoC 된다.
// 이유는 JpaRepository를 상속했기 때문에.
public interface UserRepository extends JpaRepository<User, Integer>{

	// findBy규칙 -> Username 문법
	// Select * from user where username = 1?;
	// JPA NamedQuery 를 참고한다.
	public User findByUsername(String username);

}