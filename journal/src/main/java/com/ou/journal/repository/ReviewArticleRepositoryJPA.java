package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.ReviewArticle;
import java.util.Optional;
import com.ou.journal.pojo.User;
import com.ou.journal.pojo.Article;
import java.util.List;


public interface ReviewArticleRepositoryJPA extends JpaRepository<ReviewArticle, Long> {
    Optional<ReviewArticle> findByUserAndArticle(User user, Article article);
    List<ReviewArticle> findByArticle(Article article);
    Optional<ReviewArticle> findById(Long id);
    @Query("SELECT COUNT(*) FROM ReviewArticle r WHERE r.article.id = ?1 AND r.status = 'ACCEPTED'")
    Integer countAcceptedReview(Long articleId);
    @Query("FROM ReviewArticle r WHERE r.user.id = ?1 AND r.status = ?2 AND r.article.status = ?3")
    List<ReviewArticle> getReviewArticles(Long userId, String reviewArticleStatus, String articleStatus);
}
