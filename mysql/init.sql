CREATE DATABASE IF NOT EXISTS awesome_collect
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE awesome_collect;

-- ユーザー登録情報
CREATE TABLE user_info(
  id INT AUTO_INCREMENT,
  user_id VARCHAR(20) UNIQUE NOT NULL,
  user_name VARCHAR(20),
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(32) NOT NULL,
  PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ユーザー進捗状況
CREATE TABLE user_progress(
  user_id INT,
  registered_date DATE,
  total_action_days INT,
  last_action_date DATE,
  current_streak INT,
  longest_streak INT,
  streak_bonus_count INT,
  FOREIGN KEY (user_id) REFERENCES user_info(id),
  PRIMARY KEY (user_id)
);

-- ボーナスえらい！獲得状況
CREATE TABLE bonus_awesome(
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  awesome_points INT,
  reason VARCHAR(100),
  collected_date DATE,
  FOREIGN KEY (user_id) REFERENCES user_info(id),
  PRIMARY KEY (id)
);

-- タグ情報
CREATE TABLE tag(
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  name VARCHAR(30) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user_info(id),
  UNIQUE (user_id, name),
  PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- アクション管理
-- やること
CREATE TABLE daily_todo(
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  date DATE NOT NULL,
  content VARCHAR(100),
  registered_at DATETIME,
  updated_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES user_info(id),
  PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- できたこと
CREATE TABLE daily_done(
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  date DATE NOT NULL,
  content VARCHAR(100) NOT NULL,
  minutes INT NOT NULL,
  memo VARCHAR(500),
  registered_at DATETIME,
  updated_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES user_info(id),
  PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 目標
CREATE TABLE goal(
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  content VARCHAR(500) NOT NULL,
  achieved BOOLEAN,
  registered_at DATETIME,
  updated_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES user_info(id),
  PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- メモ
CREATE TABLE memo(
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  content MEDIUMTEXT NOT NULL,
  registered_at DATETIME,
  updated_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES user_info(id),
  PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

--記事ストック
CREATE TABLE article_stock(
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  title VARCHAR(100),
  url VARCHAR(2083) NOT NULL,
  memo VARCHAR(500),
  finished BOOLEAN,
  registered_at DATETIME,
  updated_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES user_info(id),
  PRIMARY KEY (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 中間テーブル
-- できたこと
CREATE TABLE done_tag(
  done_id INT,
  tag_id INT,
  FOREIGN KEY (done_id) REFERENCES daily_done(id),
  FOREIGN KEY (tag_id) REFERENCES tag(id),
  PRIMARY KEY (done_id, tag_id)
);

-- 目標
CREATE TABLE goal_tag(
  goal_id INT,
  tag_id INT,
  FOREIGN KEY (goal_id) REFERENCES goal(id),
  FOREIGN KEY (tag_id) REFERENCES tag(id),
  PRIMARY KEY (goal_id, tag_id)
);

-- メモ
CREATE TABLE memo_tag(
  memo_id INT,
  tag_id INT,
  FOREIGN KEY (memo_id) REFERENCES memo(id),
  FOREIGN KEY (tag_id) REFERENCES tag(id),
  PRIMARY KEY (memo_id, tag_id)
);

-- 記事ストック
CREATE TABLE article_tag(
  article_id INT,
  tag_id INT,
  FOREIGN KEY (article_id) REFERENCES article_stock(id),
  FOREIGN KEY (tag_id) REFERENCES tag(id),
  PRIMARY KEY (article_id, tag_id)
);