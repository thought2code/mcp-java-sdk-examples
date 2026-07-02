CREATE DATABASE IF NOT EXISTS `bookstore`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `bookstore`;

CREATE TABLE IF NOT EXISTS `author` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Author primary key.',
    `name` VARCHAR(128) NOT NULL COMMENT 'Author display name.',
    `country` VARCHAR(64) DEFAULT NULL COMMENT 'Country or region associated with the author.',
    `birth_year` SMALLINT DEFAULT NULL COMMENT 'Year the author was born.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_author_name` (`name`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Authors whose books are sold in the store.';

CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Book primary key.',
    `author_id` BIGINT NOT NULL COMMENT 'Author who wrote the book.',
    `title` VARCHAR(255) NOT NULL COMMENT 'Book title.',
    `isbn` VARCHAR(20) NOT NULL COMMENT 'International Standard Book Number.',
    `category` VARCHAR(64) NOT NULL COMMENT 'Store category used for browsing and filtering.',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'Current sale price.',
    `published_year` SMALLINT DEFAULT NULL COMMENT 'Year the book was published.',
    `in_stock` INT NOT NULL DEFAULT 0 COMMENT 'Number of available copies in stock.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_book_isbn` (`isbn`),
    KEY `idx_book_author` (`author_id`),
    KEY `idx_book_category` (`category`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Books available for purchase in the online bookstore.';

CREATE TABLE IF NOT EXISTS `customer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Customer primary key.',
    `name` VARCHAR(128) NOT NULL COMMENT 'Customer full name.',
    `email` VARCHAR(255) NOT NULL COMMENT 'Customer email address.',
    `city` VARCHAR(128) DEFAULT NULL COMMENT 'Customer city used for shipping and reporting.',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the customer account was created.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_customer_email` (`email`),
    KEY `idx_customer_city` (`city`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Customers who can place orders and review books.';

CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order primary key.',
    `customer_id` BIGINT NOT NULL COMMENT 'Customer who placed the order.',
    `order_no` VARCHAR(32) NOT NULL COMMENT 'Human-readable order number.',
    `status` VARCHAR(32) NOT NULL COMMENT 'Order lifecycle status such as pending, paid, shipped, or cancelled.',
    `ordered_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the order was placed.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_orders_order_no` (`order_no`),
    KEY `idx_orders_customer` (`customer_id`),
    KEY `idx_orders_status` (`status`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Customer orders placed through the bookstore.';

CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order item primary key.',
    `order_id` BIGINT NOT NULL COMMENT 'Order that contains this line item.',
    `book_id` BIGINT NOT NULL COMMENT 'Book purchased in this line item.',
    `quantity` INT NOT NULL COMMENT 'Number of copies purchased.',
    `unit_price` DECIMAL(10,2) NOT NULL COMMENT 'Book price captured when the order was placed.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_item_order_book` (`order_id`, `book_id`),
    KEY `idx_order_item_book` (`book_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Individual books included in each customer order.';

CREATE TABLE IF NOT EXISTS `review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Review primary key.',
    `book_id` BIGINT NOT NULL COMMENT 'Book being reviewed.',
    `customer_id` BIGINT NOT NULL COMMENT 'Customer who wrote the review.',
    `rating` TINYINT NOT NULL COMMENT 'Rating from 1 to 5.',
    `comment` TEXT DEFAULT NULL COMMENT 'Optional review text.',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the review was submitted.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_review_book_customer` (`book_id`, `customer_id`),
    KEY `idx_review_customer` (`customer_id`),
    KEY `idx_review_rating` (`rating`),
    CHECK (`rating` BETWEEN 1 AND 5)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Customer reviews and ratings for books.';

INSERT IGNORE INTO `author` (`id`, `name`, `country`, `birth_year`) VALUES
    (1, 'Ursula K. Le Guin', 'United States', 1929),
    (2, 'Haruki Murakami', 'Japan', 1949),
    (3, 'Octavia E. Butler', 'United States', 1947),
    (4, 'Kazuo Ishiguro', 'United Kingdom', 1954);

INSERT IGNORE INTO `book` (
    `id`,
    `author_id`,
    `title`,
    `isbn`,
    `category`,
    `price`,
    `published_year`,
    `in_stock`
) VALUES
    (1, 1, 'A Wizard of Earthsea', '9780547773742', 'Fantasy', 12.99, 1968, 18),
    (2, 1, 'The Left Hand of Darkness', '9780441478125', 'Science Fiction', 14.99, 1969, 11),
    (3, 2, 'Kafka on the Shore', '9781400079278', 'Literary Fiction', 16.50, 2002, 9),
    (4, 3, 'Kindred', '9780807083697', 'Science Fiction', 15.95, 1979, 14),
    (5, 4, 'Never Let Me Go', '9781400078776', 'Literary Fiction', 13.95, 2005, 7);

INSERT IGNORE INTO `customer` (`id`, `name`, `email`, `city`, `created_at`) VALUES
    (1, 'Maya Chen', 'maya.chen@example.com', 'Shanghai', '2026-01-08 10:15:00'),
    (2, 'Daniel Brooks', 'daniel.brooks@example.com', 'Seattle', '2026-01-18 14:32:00'),
    (3, 'Aiko Tanaka', 'aiko.tanaka@example.com', 'Tokyo', '2026-02-03 09:05:00');

INSERT IGNORE INTO `orders` (`id`, `customer_id`, `order_no`, `status`, `ordered_at`) VALUES
    (1, 1, 'ORD-2026-0001', 'paid', '2026-02-10 11:22:00'),
    (2, 2, 'ORD-2026-0002', 'shipped', '2026-02-12 16:45:00'),
    (3, 3, 'ORD-2026-0003', 'pending', '2026-02-14 08:30:00');

INSERT IGNORE INTO `order_item` (`id`, `order_id`, `book_id`, `quantity`, `unit_price`) VALUES
    (1, 1, 1, 1, 12.99),
    (2, 1, 4, 1, 15.95),
    (3, 2, 2, 2, 14.99),
    (4, 3, 3, 1, 16.50),
    (5, 3, 5, 1, 13.95);

INSERT IGNORE INTO `review` (`id`, `book_id`, `customer_id`, `rating`, `comment`, `created_at`) VALUES
    (1, 1, 1, 5, 'A compact adventure with a rich world and clear emotional stakes.', '2026-02-15 20:10:00'),
    (2, 4, 1, 5, 'Powerful, unsettling, and easy to discuss from the schema alone.', '2026-02-16 19:35:00'),
    (3, 2, 2, 4, 'Thought-provoking and memorable.', '2026-02-18 12:25:00');
