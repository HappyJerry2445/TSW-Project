CREATE DATABASE CardHaven;
USE CardHaven;
CREATE TABLE "User"
(
    UserID       INT PRIMARY KEY AUTO_INCREMENT,
    FirstName    VARCHAR(255)        NOT NULL,
    LastName     VARCHAR(255)        NOT NULL,
    Email        VARCHAR(255) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255)        NOT NULL, -- Per Argon2id
    CreatedAt    TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP,
    LastLogin    TIMESTAMP,
    Role         ENUM ('Customer', 'Admin', 'Moderator') DEFAULT 'Customer',
    INDEX idx_email (Email)
);

CREATE TABLE Address
(
    AddressID     INT PRIMARY KEY AUTO_INCREMENT,
    UserID        INT                          NOT NULL,
    StreetAddress TEXT                         NOT NULL,
    City          VARCHAR(100)                 NOT NULL,
    State         VARCHAR(100),
    PostalCode    VARCHAR(20)                  NOT NULL,
    Country       VARCHAR(100)                 NOT NULL,
    AddressType   ENUM ('Shipping', 'Billing') NOT NULL,
    IsDefault     BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (UserID) REFERENCES "User" (UserID) ON DELETE CASCADE,
    INDEX idx_user_address (UserID, AddressType)
);

CREATE TABLE Category
(
    CategoryID       INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName     VARCHAR(255)                          NOT NULL,
    ParentCategoryID INT,
    CategoryType     ENUM ('Card', 'Accessory', 'Generic') NOT NULL,
    Description      TEXT,
    FOREIGN KEY (ParentCategoryID) REFERENCES Category (CategoryID) ON DELETE SET NULL,
    UNIQUE KEY unq_category (CategoryName, CategoryType)
);

CREATE TABLE Product
(
    ProductID     INT PRIMARY KEY AUTO_INCREMENT,
    SKU           VARCHAR(50) UNIQUE                               NOT NULL,
    ProductName   VARCHAR(255)                                     NOT NULL,
    BasePrice     DECIMAL(10, 2)                                   NOT NULL CHECK (BasePrice >= 0),
    CurrentPrice  DECIMAL(10, 2)                                   NOT NULL CHECK (CurrentPrice >= 0),
    StockQuantity INT                                              NOT NULL DEFAULT 0 CHECK (StockQuantity >= 0),
    ProductType   ENUM ('TradingCard', 'Accessory', 'BoosterPack') NOT NULL,
    CreatedAt     TIMESTAMP                                                 DEFAULT CURRENT_TIMESTAMP,
    LastUpdated   TIMESTAMP                                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    IsActive      BOOLEAN                                                   DEFAULT TRUE,
    INDEX idx_product_type (ProductType),
    INDEX idx_sku (SKU)
);

CREATE TABLE TradingCard
(
    CardID        INT PRIMARY KEY,
    CardSet       VARCHAR(100)                                            NOT NULL,
    CardNumber    VARCHAR(50)                                             NOT NULL,
    Rarity        ENUM ('Common', 'Uncommon', 'Rare', 'Mythic', 'Secret') NOT NULL,
    CardCondition ENUM ('Mint', 'Near Mint', 'Lightly Played', 'Moderately Played', 'Heavily Played'),
    CardText      TEXT,
    Artist        VARCHAR(100),
    YearPublished YEAR,
    FOREIGN KEY (CardID) REFERENCES Product (ProductID) ON DELETE CASCADE,
    UNIQUE KEY unq_card_identifier (CardSet, CardNumber)
);

CREATE TABLE Accessory
(
    AccessoryID   INT PRIMARY KEY,
    AccessoryType ENUM ('Sleeves', 'Binders', 'Dice', 'Playmats', 'Boxes') NOT NULL,
    Material      VARCHAR(100),
    Color         VARCHAR(50),
    Dimensions    VARCHAR(100),
    Compatibility TEXT,
    FOREIGN KEY (AccessoryID) REFERENCES Product (ProductID) ON DELETE CASCADE
);

CREATE TABLE ProductVariant
(
    VariantID       INT PRIMARY KEY AUTO_INCREMENT,
    ProductID       INT  NOT NULL,
    VariantName     VARCHAR(255),
    Attributes      JSON NOT NULL, -- Es: {"color": "red", "size": "XL"}
    AdditionalPrice DECIMAL(10, 2) DEFAULT 0,
    Stock           INT  NOT NULL  DEFAULT 0,
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID) ON DELETE CASCADE,
    INDEX idx_product_variants (ProductID)
);

CREATE TABLE ProductImage
(
    ImageID           INT PRIMARY KEY AUTO_INCREMENT,
    ProductID         INT         NOT NULL,
    ImageData         LONGBLOB    NOT NULL,
    MimeType          VARCHAR(50) NOT NULL, -- Es. 'image/jpeg', 'image/png'
    ImageName         VARCHAR(255),
    Description       VARCHAR(255),
    SortOrder         INT       DEFAULT 0,
    CreatedAt         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ThumbnailData     MEDIUMBLOB,
    ThumbnailMimeType VARCHAR(50),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID) ON DELETE CASCADE,
    INDEX idx_product_images (ProductID)
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;


CREATE TABLE ProductCategory
(
    ProductID  INT NOT NULL,
    CategoryID INT NOT NULL,
    PRIMARY KEY (ProductID, CategoryID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID) ON DELETE CASCADE,
    FOREIGN KEY (CategoryID) REFERENCES Category (CategoryID) ON DELETE CASCADE
);

CREATE TABLE OrderAddress
(
    OrderAddressID INT PRIMARY KEY AUTO_INCREMENT,
    StreetAddress  TEXT         NOT NULL,
    City           VARCHAR(100) NOT NULL,
    State          VARCHAR(100),
    PostalCode     VARCHAR(20)  NOT NULL,
    Country        VARCHAR(100) NOT NULL
);

CREATE TABLE "Order"
(
    OrderID           INT PRIMARY KEY AUTO_INCREMENT,
    UserID            INT            NOT NULL,
    OrderDate         TIMESTAMP                                                           DEFAULT CURRENT_TIMESTAMP,
    OrderStatus       ENUM ('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    ShippingAddressID INT            NOT NULL,
    BillingAddressID  INT            NOT NULL,
    TotalAmount       DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES "User" (UserID) ON DELETE RESTRICT,
    FOREIGN KEY (ShippingAddressID) REFERENCES OrderAddress (OrderAddressID),
    FOREIGN KEY (BillingAddressID) REFERENCES OrderAddress (OrderAddressID),
    INDEX idx_order_status (OrderStatus)
);

CREATE TABLE OrderItem
(
    OrderItemID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID     INT            NOT NULL,
    ProductID   INT,
    VariantID   INT,
    ProductSnapshot JSON           NOT NULL,
    Quantity    INT            NOT NULL CHECK (Quantity > 0),
    UnitPrice   DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES "Order" (OrderID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID) ON DELETE SET NULL,
    FOREIGN KEY (VariantID) REFERENCES ProductVariant (VariantID) ON DELETE SET NULL
);

CREATE TABLE AttributeDefinition
(
    AttributeID   INT PRIMARY KEY AUTO_INCREMENT,
    AttributeName VARCHAR(255) UNIQUE                          NOT NULL,
    DataType      ENUM ('String', 'Number', 'Boolean', 'Date') NOT NULL,
    ApplicableTo  ENUM ('Card', 'Accessory', 'All')            NOT NULL
);

CREATE TABLE ProductAttribute
(
    ProductID   INT  NOT NULL,
    AttributeID INT  NOT NULL,
    Value       TEXT NOT NULL,
    PRIMARY KEY (ProductID, AttributeID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID) ON DELETE CASCADE,
    FOREIGN KEY (AttributeID) REFERENCES AttributeDefinition (AttributeID) ON DELETE CASCADE
);

CREATE TABLE Review
(
    ReviewID     INT PRIMARY KEY AUTO_INCREMENT,
    ProductID    INT     NOT NULL,
    UserID       INT     NOT NULL,
    Rating       TINYINT NOT NULL CHECK (Rating BETWEEN 1 AND 5),
    Title        VARCHAR(255),
    ReviewText   TEXT,
    CreatedAt    TIMESTAMP                                DEFAULT CURRENT_TIMESTAMP,
    ReviewStatus ENUM ('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    UNIQUE (ProductID, UserID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES "User" (UserID) ON DELETE CASCADE,
    INDEX idx_reviews_product (ProductID)
);

CREATE TABLE Cart
(
    CartID      INT PRIMARY KEY AUTO_INCREMENT,
    UserID      INT NOT NULL,
    CreatedAt   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    LastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES User (UserID) ON DELETE CASCADE,
    UNIQUE (UserID),
    INDEX idx_cart_user (UserID)
);

CREATE TABLE CartItem
(
    CartItemID INT PRIMARY KEY AUTO_INCREMENT,
    CartID     INT NOT NULL,
    ProductID  INT NOT NULL,
    VartiantID INT,
    Quantity   INT NOT NULL CHECK ( Quantity > 0 ),
    AddedAT    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CartID) REFERENCES Cart (CartID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID),
    FOREIGN KEY (VartiantID) REFERENCES ProductVariant (VariantID),
    UNIQUE KEY unq_cart_product_variant (CartID, ProductID, VartiantID)
);

