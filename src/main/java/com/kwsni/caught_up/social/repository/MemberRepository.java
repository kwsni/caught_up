package com.kwsni.caught_up.social.repository;

import java.util.List;

import org.springframework.data.domain.Limit;
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
    @Query(nativeQuery = true, value = """
        SELECT m.avatar, m.bio, m.first_name, m.last_name, m.location, m.pronoun, m.website, m.id, m.is_generated, m.password, m.role, m.email, m.username
        FROM member m LEFT JOIN review r ON m.id = r.member_id
        WHERE m.is_generated
        GROUP BY m.id
        HAVING COUNT(*) < 5 ORDER BY random()""")
    List<Member> sampleByGenerated(Limit rowLimit, int reviewLimit);
}
