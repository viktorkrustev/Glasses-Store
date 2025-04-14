-- Категории
INSERT INTO category (id, name) VALUES
(1, 'Casual'),
(2, 'Formal'),
(3, 'Sunglasses'),
(4, 'Sport');

-- Производители
INSERT INTO manufacturer (id, name) VALUES
(1, 'RayBrand'),
(2, 'Luxottica'),
(3, 'Visionary Co.');

-- Продукти (очила)
INSERT INTO glasses (name, price, categoryId, manufacturerId, imageUrl) VALUES
('Classic Aviator', 129.99, 1, 1, 'photo0.jpg'),
('Urban Vision', 89.50, 2, 1, 'urban1.jpg'),
('SunBlast Max', 149.00, 3, 2, 'sunblast1.jpg'),
('Blue Light Defender', 99.99, 1, 3, 'blue_light1.jpg'),
('Sleek Steel', 119.90, 2, 1, 'sleek1.jpg'),
('Golden Horizon', 139.99, 3, 2, 'golden1.jpg'),
('Sport Shield', 159.00, 4, 3, 'sport1.jpg'),
('Vintage Round', 109.49, 1, 2, 'vintage1.jpg'),
('Neon Edge', 129.99, 2, 1, 'neon1.jpg'),
('Crystal Clear', 79.00, 1, 1, 'clear1.jpg'),
('Shadow Vision', 139.00, 3, 3, 'shadow1.jpg'),
('Titanium Breeze', 189.90, 2, 2, 'titanium1.jpg'),
('Modern Reader', 89.99, 1, 3, 'reader1.jpg'),
('Retro Bold', 109.99, 2, 2, 'retro1.jpg'),
('Daylight Pro', 129.00, 3, 1, 'daylight1.jpg'),
('Carbon Flex', 149.00, 4, 3, 'carbon1.jpg'),
('Transparent Edge', 99.50, 1, 2, 'transparent1.jpg'),
('Mirror Glare', 119.99, 3, 1, 'mirror1.jpg'),
('Studio Minimal', 89.00, 2, 2, 'minimal1.jpg'),
('Zoom Xtreme', 159.99, 4, 3, 'zoom1.jpg');
