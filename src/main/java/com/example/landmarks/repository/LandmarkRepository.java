package com.example.landmarks.repository;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {

    Page<Landmark> findByUserUsernameOrderByCreatedAtDesc(String username, Pageable pageable);

    @Query("""
        SELECT l FROM Landmark l
        WHERE l.user.id IN :userIds
        ORDER BY l.createdAt DESC
    """)
    Page<Landmark> findByUserIdInOrderByCreatedAtDesc(@Param("userIds") List<Long> userIds, Pageable pageable);

    Page<Landmark> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Landmark> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
