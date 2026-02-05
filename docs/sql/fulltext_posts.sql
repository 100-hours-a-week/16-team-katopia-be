-- Fulltext index for posts.content (MySQL 8.0/8.4, InnoDB, ngram parser)
-- If the index already exists, drop it first.
ALTER TABLE posts
  DROP INDEX ft_posts_content;

ALTER TABLE posts
  ADD FULLTEXT INDEX ft_posts_content (content) WITH PARSER ngram;
