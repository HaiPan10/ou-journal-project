package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.ReviewArticle;
import java.util.Optional;
import com.ou.journal.pojo.User;
import com.ou.journal.pojo.Manuscript;

import java.util.List;

public interface ReviewArticleRepositoryJPA extends JpaRepository<ReviewArticle, Long> {
    Optional<ReviewArticle> findByUserAndManuscript(User user, Manuscript manuscript);

    // List<ReviewArticle> findByArticle(Article article);
    List<ReviewArticle> findByManuscript(Manuscript manuscript);

    Optional<ReviewArticle> findById(Long id);

    @Query("SELECT COUNT(*) FROM ReviewArticle r WHERE r.manuscript.id = ?1 AND r.status = ?2")
    Integer countReviewArticleByStatus(Long manuscriptId, String status);

    @Query("SELECT COUNT(*) FROM ReviewArticle r WHERE r.manuscript.id = ?1 AND r.status IN ('ACCEPT_PUBLISH', 'REJECT_PUBLISH')")
    Integer countReviewedArticle(Long manuscriptId);

    @Query("SELECT COUNT(*) FROM ReviewArticle r WHERE r.manuscript.id = ?1")
    Integer countReviewArticle(Long manuscriptId);

    // @Query("SELECT COUNT(*) FROM ReviewArticle r WHERE r.article.id = ?1 AND
    // r.status = 'ACCEPTED'")
    // Integer countAcceptedReview(Long articleId);
    @Query("FROM ReviewArticle r WHERE r.user.id = ?1 AND r.status = ?2 AND " +
            "(r.manuscript.article.status = 'INVITING_REVIEWER' OR r.manuscript.article.status = 'IN_REVIEW')")
    List<ReviewArticle> getReviewArticles(Long userId, String reviewArticleStatus);

    // @Query("SELECT r.user FROM ReviewArticle r WHERE r.article.id = ?1 AND
    // r.status = 'ACCEPTED'")
    // List<User> getReviewer(Long articleId);
    @Query("SELECT COUNT(*) FROM ReviewArticle r WHERE r.manuscript.article.id = ?1 AND r.user.id = ?2")
    Integer countReviewedTime(Long articleId, Long reviewerId);

    @Query("SELECT COUNT(*) FROM ReviewArticle r WHERE r.user.id = ?1 AND r.status = ?2 AND " +
            "(r.manuscript.article.status = 'INVITING_REVIEWER' OR r.manuscript.article.status = 'IN_REVIEW')")
    Integer countReviewArticles(Long userId, String reviewArticleStatus);

    @Query("SELECT ra FROM ReviewArticle ra JOIN Manuscript m ON ra.manuscript.id = m.id " +
        "WHERE m.article.id = ?1 AND ra.manuscript.id != ?2 AND ra.status IN ('ACCEPT_PUBLISH', 'REJECT_PUBLISH')"
    )
    List<ReviewArticle> findByOlderManuscript(Long articleId, Long manuscriptId);
}
