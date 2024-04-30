CREATE DATABASE shopapp;
USE shopapp;

--KHÁCH HÀNG KHI MUỐN MUA HÀNG => PHẢI ĐĂNG KÝ TÀI KHOẢN => BẢNG USERS

CREATE TABLE users(
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) NOT NULL DEFAULT '', --MÃ HÓA PASSWORD
    create_at DATETIME,
    update_at DATETIME,
    is_active TINYINT(1) DEFAULT 1 ,--Xóa mềm (1: còn hoạt động, 0: đã khóa)\
    data_of_birth DATE,
    facebook_account_id INT DEFAULT 0,
    google_account_id INT DEFAULT 0
);
ALTER TABLE users ADD COLUMN role_id int;

CREATE TABLE roles(
    id INT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles(id)
CREATE TABLE tokens(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expiration_date DATETIME,
    revoked TINYINT(1) NOT NULL,
    expired TINYINT(1) NOT NULL,
    user_id INT
    FOREIGN KEY (user_id) REFERENCES users(id)
)
--hỗ trợ đăng nhập từ FACEBOOK, GOOGLE
CREATE TABLE social_accounts(
    id INT PRIMARY KEY AUTO_INCREMENT,
    provider VARCHAR(20) NOT NULL COMMENT 'Tên nhà social , network',
    provider_id VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL COMMENT 'Email của tài khoản',
    name VARCHAR(100) NOT NULL COMMENT 'Tên người dùng',
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id)
)

--DANH SÁCH SẢN PHẨM(Categories)
CREATE TABLE categories(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Tên danh mục ,vd:đồ điện tử '
);
--DANH SÁCH SẢN PHẨM(Products)
CREATE TABLE products(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(350) COMMENT 'Tên sản phẩm',
    price FLOAT NOT NULL CHECK(price >= 0) COMMENT 'Giá sản phẩm',
    thumbnail VARCHAR(300) DEFAULT '',
    description LONGTEXT DEFAULT '',
    create_at DATETIME,
    update_at DATETIME,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE TABLE product_images(
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id)
    CONSTRAINT fk_product_images_products FOREIGN KEY (product_id) REFERENCES products(id)
     ON DELETE CASCADE,
    image_url VARCHAR(300),
);
--Đặt hàng -Order
CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    fullname VARCHAR(100) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    note VARCHAR(100) DEFAULT '',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    total_money FLOAT CHECK (total_money >= 0),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
ALTER  TABLE orders ADD COLUMN 'shipping_method' VARCHAR(100);
ALTER  TABLE orders ADD COLUMN 'shipping_address' VARCHAR(200);
ALTER  TABLE orders ADD COLUMN 'shipping_date' DATE;
ALTER  TABLE orders ADD COLUMN 'tracking_number' VARCHAR(100);
ALTER  TABLE orders ADD COLUMN 'payment_method' VARCHAR(100);

--Xóa 1 đơn hàng - DELETE

ALTER TABLE orders ADD COLUMN active TINYINT(1);

--Trạng thái đơn hàng chỉ được phép nhận 1 số giá trị cụ thể
ALTER TABLE orders ADD COLUMN status ENUM('pending', 'processing', 'shipped', 'delivered', 'canceled') COMMENT 'Trạng thái đơn hàng';
--Chi tiết đơn hàng - OrderDetail
CREATE TABLE order_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    number_of_products INT CHECK(number_of_products > 0),
    price FLOAT CHECK(price >= 0),
    total_money FLOAT CHECK(total_money >= 0),
    color VARCHAR(20) DEFAULT '',
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
);
s