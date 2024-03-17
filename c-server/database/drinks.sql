delete from user_cart;

delete from user_order;
delete from drink_ingredients;
delete from drink_tags;

delete from drinks_table;
delete from tags;
delete from ingredients;

delete from user_biometric;
delete from user_payment_method;
delete from users_table;


-- Inserting data into drinks_table
INSERT INTO drinks_table (drink_id, drink_name, drink_description, drink_icon_path, drink_price, is_smoothie)
VALUES (1, 'Mango Smoothie', 'Refreshing mango blend', 'Drinks/smoothie.jpg', 4.99, 1),
       (2, 'Strawberry Lemonade', 'Sweet and tangy', 'Drinks/smoothie.jpg', 3.49, 1),
       (3, 'Piña Colada', 'Tropical coconut flavor', 'Drinks/cocktail.jpg', 6.99, 0),
       (4, 'Mojito', 'Classic lime and mint', 'Drinks/cocktail.jpg', 5.49, 0),
       (5, 'Blueberry Blast', 'A burst of blueberry flavor', 'Drinks/smoothie.jpg', 4.99, 1),
       (6, 'Caramel Latte', 'Rich and creamy caramel-infused latte', 'Drinks/cocktail.jpg', 3.99, 0),
       (7, 'Watermelon Slush', 'Refreshing watermelon slushie', 'Drinks/smoothie.jpg', 3.49, 1),
       (8, 'Espresso Martini', 'A mix of espresso and vodka', 'Drinks/cocktail.jpg', 7.99, 0);


-- Inserting data into tags
INSERT INTO tags (tag_id, tag_name)
VALUES (1, 'Fruity'),
       (2, 'Refreshing'),
       (3, 'Tropical'),
       (4, 'Classic'),
       (5, 'Citrus'),
       (6, 'Herbal'),
       (7, 'Creamy'),
       (8, 'Sweet'),
       (9, 'Coffee');

-- Inserting data into ingredients
INSERT INTO ingredients (ingredient_id, ingredient_name)
VALUES (1, 'Mango'),
       (2, 'Strawberry'),
       (3, 'Lemon'),
       (4, 'Coconut'),
       (5, 'Lime'),
       (6, 'Mint'),
       (7, 'Coconut Milk'),
       (8, 'Blueberry'),
       (9, 'Caramel'),
       (10, 'Coffee'),
       (11, 'Whipped Cream');

-- Inserting data into drink_ingredients
INSERT INTO drink_ingredients (ingredient_id, drink_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (2, 2),
       (4, 3),
       (5, 3),
       (6, 4),
       (5, 4),
       (7, 5),
       (8, 5),
       (10, 6),
       (10, 6),
       (11, 6),
       (9, 7),
       (11, 7),
       (8, 8),
       (10, 8);
       
       

-- Inserting data into drink_tags
INSERT INTO drink_tags (tag_id, drink_id)
VALUES (1, 1),
       (2, 1),
       (1, 2),
       (2, 2),
       (3, 3),
       (4, 4),
       (6, 5),
       (7, 5),
       (8, 6),
       (5, 6),
       (6, 7),
       (8, 7),
       (7, 8),
       (8, 8),
       (9, 8);




