package comdpapyru.greenhome.repository.article;

import comdpapyru.greenhome.pojo.article.Article;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {
    @Query("SELECT a FROM Article a WHERE a.user.userID = :userId")
    List<Article> findByUserId(@Param("userId") Integer userId);
}
