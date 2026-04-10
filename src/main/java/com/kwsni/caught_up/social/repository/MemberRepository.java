package com.kwsni.caught_up.social.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kwsni.caught_up.social.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUsername(String username);
    Member findByEmail(String username);
    @Query("SELECT m, COUNT(r) as reviewCount FROM Member m LEFT JOIN m.reviews r GROUP BY m")
    Page<Object[]> findAllOrderByReviewCount(Pageable pageable);
    @Query("SELECT m, COUNT(f.follower) as followerCount FROM Member m LEFT JOIN m.followingMembers f GROUP BY m")
    Page<Object[]> findAllOrderByFollowerCount(Pageable pageable);
}
