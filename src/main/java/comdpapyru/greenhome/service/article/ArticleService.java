package comdpapyru.greenhome.service.article;

import comdpapyru.greenhome.pojo.article.Article;
import comdpapyru.greenhome.pojo.user.User;
import comdpapyru.greenhome.repository.article.ArticleRepository;
import comdpapyru.greenhome.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 保存文章
     * @param article 文章对象
     * @param userId 用户ID
     * @return 保存后的文章
     */
    public Article saveArticle(Article article, Integer userId) {
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 设置文章的作者
        article.setUser(user);
        
        // 保存文章
        return articleRepository.save(article);
    }
    
    /**
     * 根据用户ID获取文章列表
     * @param userId 用户ID
     * @return 文章列表
     */
    public List<Article> getArticlesByUserId(Integer userId) {
        return articleRepository.findByUserId(userId);
    }
    
    /**
     * 根据文章ID获取文章
     * @param articleId 文章ID
     * @return 文章对象
     */
    public Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
    }
    
    /**
     * 删除文章
     * @param articleId 文章ID
     */
    public void deleteArticle(Long articleId) {
        articleRepository.deleteById(articleId);
    }
}
