package test.coffee.lab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import test.coffee.lab.entity.User;

public interface UserRepository extends JpaRepository<User,
Long> {
 User findByEmail(String email);
}