DROP TABLE product IF EXISTS ;
CREATE TABLE product(
  id    INT AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(50)        NOT NULL,
  price DOUBLE DEFAULT '0' NOT NULL
);

INSERT INTO product (id, name, price) VALUES(1, 'product', '1');
