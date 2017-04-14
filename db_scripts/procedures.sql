DROP PROCEDURE IF EXISTS view_ratings_of_sellers_product;
DROP PROCEDURE IF EXISTS view_departments;
DROP PROCEDURE IF EXISTS filter_products_by_department;
DROP PROCEDURE IF EXISTS view_own_ratings;
DROP PROCEDURE IF EXISTS edit_profile_information;
DROP PROCEDURE IF EXISTS create_vendor;
DROP PROCEDURE IF EXISTS add_product;
DROP PROCEDURE IF EXISTS delete_product;
DROP PROCEDURE IF EXISTS filter_reviews;
DROP PROCEDURE IF EXISTS list_by_price_asc;
DROP PROCEDURE IF EXISTS list_by_price_asc;
DROP PROCEDURE IF EXISTS list_by_price_desc;
DROP PROCEDURE IF EXISTS view_orders;
DROP PROCEDURE IF EXISTS view_delivered_orders;
DROP PROCEDURE IF EXISTS view_order;
DROP PROCEDURE IF EXISTS rate_product;
DROP PROCEDURE IF EXISTS getAvgRating;
DROP PROCEDURE IF EXISTS getSeller;
DROP PROCEDURE IF EXISTS getItemInfo;
DROP PROCEDURE IF EXISTS getItemImages;
DROP PROCEDURE IF EXISTS filterItemsByFeature;
DROP PROCEDURE IF EXISTS sort_products;

DROP PROCEDURE IF EXISTS create_user;
DROP PROCEDURE IF EXISTS user_login;
DROP PROCEDURE IF EXISTS user_logout;
DROP PROCEDURE IF EXISTS view_product_rating;
DROP PROCEDURE IF EXISTS view_user;
DROP PROCEDURE IF EXISTS update_product;

DROP PROCEDURE IF EXISTS search_vendor;
DROP PROCEDURE IF EXISTS search_product;
DROP PROCEDURE IF EXISTS update_product;


DELIMITER //
CREATE PROCEDURE `view_ratings_of_sellers_product` (IN product_id INT, IN seller_id INT)
BEGIN
  SELECT rating.value, user.name, rating.create_time
    FROM rating
    INNER JOIN product
    ON rating.product_id = product.id
    INNER JOIN user
    ON user.id = rating.user_id
    WHERE
    product.id = product_id
    AND product.vendor_id1 = seller_id;
END //

DELIMITER //
CREATE PROCEDURE `view_departments` ()
BEGIN
  SELECT *
    FROM department;

END //

DELIMITER //
CREATE PROCEDURE `filter_products_by_department` (IN department_id INT)
BEGIN
  SELECT *
    FROM product
    INNER JOIN product_has_department
    ON product_has_department.product_id = product.id
    INNER JOIN department
    ON department.id = product_has_department.department_id
    WHERE department_id =  department.id;
END //

DELIMITER //
CREATE PROCEDURE `view_own_ratings` (IN user_id INT)
BEGIN
  SELECT *
    FROM rating
    INNER JOIN user
    ON rating.user_id = user.id
    WHERE user_id =  user.id;
END //

DELIMITER //
CREATE PROCEDURE `edit_profile_information` (IN user_id INT, IN new_name varchar(31), IN new_email varchar(225), IN new_address varchar(225), IN new_date_of_birth date)
BEGIN
  UPDATE user
  SET name = new_name, email = new_email, address = new_address, date_of_birth = new_date_of_birth
  WHERE user.id = user_id;
END //


DELIMITER  //
CREATE PROCEDURE create_vendor
     (
        IN  name     VARCHAR(31),
        IN  email    VARCHAR(255),
        IN password  VARCHAR(31) ,
        IN address   VARCHAR(255),
        IN date_of_birth DATE,
        IN token     VARCHAR(255),
        IN gender TINYINT
     )
BEGIN
    INSERT INTO user
         (
           name,
           email,
           password,
           address,
           date_of_birth,
           token,
           gender
         )
    VALUES
         (name, email, password, address, date_of_birth, token,gender);

    SELECT @var1 := user.id FROM user WHERE user.email = email;

    INSERT INTO  vendor
      (
        user_id
      )
     VALUES
      (
        @var1
      );
    Select * from user;
END //

DELIMITER //
-- DROP procedure if exists add_product ;
CREATE PROCEDURE add_product
  (
    IN name varchar(63),
    IN vendor_id1 INT,
    IN description varchar(63),
    IN department_id int,
    IN size int,
    IN stock int,
    IN colour varchar(63),
    IN price double,
    IN image_path varchar(255)
  )
  BEGIN

    INSERT INTO product
      (
        name,
        description,
        create_time,
        vendor_id1
      )
        VALUES
        (
          name,
          description,
          now(),
          vendor_id1
        );

    SELECT @prod_id:= product.id FROM product
    WHERE product.name= name AND product.description = description ;

    INSERT INTO item
        ( size,
          stock,
          colour,
          price,
          create_time,
          product_id
        )
    VALUES
        (
          size,
          stock,
          colour,
          price,
          now(),
          @prod_id
        );
    INSERT INTO product_has_department
    VALUES
        (
          @prod_id,
          department_id
        );

    SELECT @it_id:= item.id   FROM item
    WHERE item.product_id = @prod_id
    AND item.price = price
    AND item.size = size
    AND item.stock = stock
    AND item.colour = colour ;

   INSERT INTO image
      (
        file_path,
        create_time,
        item_id,
        item_product_id,
        item_product_vendor_id1,
        user_id
      )
    VALUES
      (
        image_path,
        now(),
        @it_id,
        @prod_id,
        vendor_id1,
        vendor_id1
      );
END
//




-- drop procedure delete_product ;
DELIMITER //

CREATE PROCEDURE delete_product
  (
    IN product_id int
  )

  BEGIN

    DELETE FROM image
    WHERE image.item_product_id = product_id ;

    DELETE FROM rating
    WHERE rating.product_id = product_id ;

    DELETE FROM order_has_item
    WHERE order_has_item.item_product_id = product_id ;

    DELETE FROM user_has_item
    WHERE user_has_item.item_product_id = product_id;

    DELETE FROM product_has_department
    WHERE product_has_department.product_id = product_id ;

    DELETE FROM item
    WHERE item.product_id = product_id ;

    DELETE FROM product
    WHERE product.id = product_id ;

END //


-- -- call delete_product(5) ;

DELIMITER //

CREATE PROCEDURE filter_reviews
  (
    IN  rating_value INT,
    IN  product_id INT
  )

  BEGIN

    SELECT * FROM product
      INNER JOIN item
        ON product.id = item.product_id AND product.id = product_id
      INNER JOIN rating
        ON rating.value = rating_value AND product_id = rating.product_id;
  END //


-- -- call filter_reviews (5,2);

-- DROP PROCEDURE list_by_price_asc ;

DELIMITER //

CREATE PROCEDURE list_by_price_asc
  ( )

   BEGIN

      SELECT product.* , item.price FROM product
        INNER JOIN item
          ON item.product_id = product.id
          ORDER BY item.price ASC ;
   END

//

-- -- call list_by_price_asc();
-- DROP PROCEDURE list_by_price_desc ;

DELIMITER //

CREATE PROCEDURE list_by_price_desc
  ( )

  BEGIN

    SELECT product.* , item.price FROM product
      INNER JOIN item
        ON item.product_id = product.id
        ORDER BY item.price DESC ;
END
//
-- -- call list_by_price_desc();



DELIMITER //

  CREATE PROCEDURE view_orders
    (
      IN user_id int
    )

    BEGIN

    SELECT `order`.id   FROM `order`
          WHERE `order`.user_id = user_id
            ORDER BY `order`.delivery_status  ;
    END

//

-- -- call view_orders (1);

DELIMITER //

CREATE PROCEDURE view_delivered_orders
  (
    IN user_id int
  )
    BEGIN

      SELECT * FROM `order`WHERE delivery_status = 'DONE' AND  `order`.user_id = user_id;

    END

//

-- -- call view_delivered_orders (1);

DELIMITER //
CREATE PROCEDURE view_order
  (
    IN order_id int
  )
    BEGIN

      SELECT * FROM `order` WHERE `order`.id = order_id ;

    END

//

-- -- -- call view_order(4);

DELIMITER //
CREATE PROCEDURE `mydb`.`rate_product` (IN userId INT, IN productId INT, IN ratingValue INT)

BEGIN
 INSERT INTO rating
         (
           value                  ,
           user_id               ,
           product_id          ,
           create_time
         )
    VALUES
         (ratingValue, userId, productId, now());

   END //


-- -- call rate_product(1, 1, 4);

DELIMITER //
CREATE PROCEDURE `mydb`.`getAvgRating` (IN productId INT)
Begin
SELECT
     p.name
  , ( SELECT AVG(r.value)
      FROM rating r
      WHERE r.product_id = p.id
    )
    AS avg_rating
FROM
  product p
WHERE p.id = productId;
END //

-- call mydb.getAvgRating(2);


DELIMITER //
 CREATE PROCEDURE `mydb`.`getSeller` (IN itemID INT)
 Begin
 SELECT
      uhi.user_id, user.name
 FROM
   user_has_item uhi, user

WHERE uhi.user_id  IN
(
  SELECT v.user_id
  FROM vendor v
) and uhi.item_id = itemId and uhi.user_id = user.id;
 END //
DELIMITER ;



DELIMITER //
 CREATE PROCEDURE `mydb`.`getItemInfo` (IN itemID INT)
 Begin
 SELECT
      p.name, p.description, i.size, i.colour, i.price,   ( Select AVG(r.value)
		FROM rating r
		WHERE r.product_id = p.id) as rating
 FROM
   item i, product p
where i.product_id = itemID and p.id = itemID;

 END //
 DELIMITER ;
-- call mydb.getItemInfo(1);


DELIMITER //
 CREATE PROCEDURE `mydb`.`getItemImages` (IN itemID INT)
 Begin
 SELECT
      i.file_path
 FROM
   image i
where i.item_product_id = itemID;

 END //
 DELIMITER ;
-- call mydb.getItemImages(1);




delimiter //



CREATE PROCEDURE create_user
     (
        IN name  VARCHAR(31) ,
        IN email VARCHAR(255),
        IN password VARCHAR(31) ,
        IN address  VARCHAR(255),
        IN date_of_birth DATE,
        IN token VARCHAR(255),
        IN gender TINYINT
     )
BEGIN

    INSERT INTO user
         (
           name ,
           email,
           password,
           address,
           date_of_birth,
           token,
           gender
         )
    VALUES
         (name, email, password, address, date_of_birth, token,gender);
         Select * from user;
END //


CREATE PROCEDURE user_login(IN mail VARCHAR(255), IN pass VARCHAR(31), OUT login_successful int(11))
    BEGIN
    DECLARE myvar int;
    if EXISTS(SELECT id FROM user WHERE email = mail and password = pass)
        then
        SELECT id FROM user WHERE email = mail and password = pass INTO myvar;
        else
        set myvar = -1;

        end if;

        if myvar = -1
        then
        set login_successful = -1;
        else
        set login_successful = myvar;
        end if;
        SELECT myvar;
    END //

create procedure user_logout(IN t varchar(255), IN i int(11))
  begin
    declare logout bool;
    UPDATE user
    SET token=''
    WHERE token=t and id = i;
        set logout = true;
        select logout;
  end //


-- drop procedure filterItemsByFeature;
DELIMITER //
 CREATE PROCEDURE `mydb`.`filterItemsByFeature` (IN itemID INT, IN item_color VARCHAR(63), IN item_size INT(11))
 Begin
 if(item_color is NULL and item_size is NULL)
 then
	 SELECT
		  p.name, p.description, i.size, i.colour, i.price
	 FROM
	   item i, product p
	where i.product_id = itemID and p.id = itemID;
elseif (item_color is NULL)
then
 SELECT
		  p.name, p.description, i.size, i.colour, i.price
	 FROM
	   item i, product p
	where i.product_id = itemID and p.id = itemID and i.size = item_size;
elseif(item_size is NULL)
then
SELECT
		  p.name, p.description, i.size, i.colour, i.price
	 FROM
	   item i, product p
	where i.product_id = itemID and p.id = itemID and i.colour = item_color;
end if;
 END //
 DELIMITER ;

 DELIMITER //
	CREATE PROCEDURE `mydb`.`update_product`(
	 n_id int,
	 n_name varchar(63),
	 n_desc varchar(255),
	 n_v_id int
	)
	BEGIN
		UPDATE product
		SET
	         name = n_name,
	         description = n_desc,
	         vendor_id1 = n_v_id
		WHERE
		id = n_id;
	END //
	DELIMITER ;

DELIMITER //
	CREATE PROCEDURE `mydb`.`view_user`(
	 n_id int
	)
	BEGIN
	 select * from user where id = n_id;
	END //
	DELIMITER ;

DELIMITER //
	CREATE PROCEDURE `mydb`.`view_product_rating`(
	u_id int,
    p_id int
	)
	BEGIN
		select value
		from rating inner join product on rating.product_id = product.id
		where product.vendor_id1 = u_id
	    and product.id = p_id;
	END //
 DELIMITER ;

DELIMITER //
create procedure `mydb`.`sort_products` (IN qr varchar(50))
begin
    IF qr = 'price_asc' THEN
        SELECT * FROM item ORDER BY price;
    ELSEIF qr = 'price_desc' THEN
        SELECT * FROM item ORDER BY price DESC;
    ELSEIF qr = 'rating' THEN
        SELECT *,(SELECT avg(r.value) FROM rating r WHERE r.product_id = i.id) as tmprating from product i order by tmprating DESC;
    END IF;
end //
DELIMITER ;



DELIMITER //
CREATE PROCEDURE `mydb`.`search_product` (IN qr varchar(50))
begin
    DECLARE qrlike varchar(52);
    SET qrlike =concat( '%' , qr, '%');
    SELECT * FROM product WHERE name LIKE qrlike
        UNION
    SELECT * FROM product WHERE description LIKE qrlike;

end //

DELIMITER ;



DELIMITER //
CREATE PROCEDURE `mydb`.`search_vendor` (IN qr varchar(50))
begin
    DECLARE qrlike varchar(52);
    SET qrlike =concat( '%' , qr, '%');
    SELECT * FROM vendor v, user u WHERE v.user_id = u.id AND u.name LIKE qrlike;
end //

DELIMITER ;

-- call mydb.filterItemsByFeature(1, NULL, 25);

DELIMITER //
CREATE PROCEDURE `mydb`.`get_user_reviews` (IN user_id INT)
begin
  SELECT * FROM rating r WHERE r.user_id = user_id;
end //

DELIMITER ;

drop procedure if exists mydb.get_products_reviews;

DELIMITER //
CREATE PROCEDURE `mydb`.`get_products_reviews` (IN products_id INT)
begin
  SELECT * FROM rating r WHERE r.product_id = product_id;
end //

DELIMITER ;

DROP procedure IF EXISTS mydb.create_review;

DELIMITER //
CREATE PROCEDURE `mydb`.`create_review`
(IN value INT,
IN user_id INT,
IN product_id INT,
IN review longtext)
begin
    INSERT INTO rating
        ( value,
          user_id,
          product_id,
          review,
          create_time,
          updated_at
        )
    VALUES
        (
         value,
          user_id,
          product_id,
          review,
          now(),
          now()
        );
end //

DROP procedure IF EXISTS my
db.get_total_rating

DELIMITER //
CREATE PROCEDURE `mydb`.`get_total_rating` (IN products_id INT, OUT res INT)
begin
  SELECT AVG(value)
  FROM rating r
  WHERE r.product_id = product_id
  into res;

end //

DELIMITER ;
-- call mydb.filterItemsByFeature(1, NULL, 25);
