package com.example.landmarks.repository;

import com.example.landmarks.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Page<User> findByUsernameContainingIgnoreCase(String query, Pageable pageable);

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.following f WHERE u = :follower AND f = :followed")
    boolean isUserFollowing(@Param("follower") User follower, @Param("followed") User followed);

    @Modifying
    @Query(value = "INSERT INTO user_following (follower_id, followed_id) VALUES (:followerId, :followedId)", nativeQuery = true)
    void insertFollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    @Modifying
    @Query(value = "DELETE FROM user_following WHERE follower_id = :followerId AND followed_id = :followedId", nativeQuery = true)
    void deleteFollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);
    @Query("""
    SELECT f FROM User u 
    JOIN u.followers f 
    WHERE u.username = :username AND LOWER(f.username) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<User> findFollowersByUsernameAndQuery(@Param("username") String username,
                                               @Param("query") String query,
                                               Pageable pageable);

    @Query("""
    SELECT f FROM User u 
    JOIN u.following f 
    WHERE u.username = :username AND LOWER(f.username) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<User> findFollowingByUsernameAndQuery(@Param("username") String username,
                                               @Param("query") String query,
                                               Pageable pageable);
    @Query("""
        SELECT f.id FROM User u 
        JOIN u.following f 
        WHERE u.username = :username
    """)
    List<Long> findFollowedUserIdsByUsername(@Param("username") String username);
}
