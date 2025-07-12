CREATE DATABASE CardHaven;
USE CardHaven;

create table AttributeDefinition
(
    AttributeID   int auto_increment
        primary key,
    AttributeName varchar(255)                                 not null,
    DataType      enum ('String', 'Number', 'Boolean', 'Date') not null,
    ApplicableTo  enum ('Card', 'Accessory', 'All')            not null,
    constraint AttributeName
        unique (AttributeName)
);

create table Category
(
    CategoryID       int auto_increment
        primary key,
    CategoryName     varchar(255)                          not null,
    ParentCategoryID int                                   null,
    CategoryType     enum ('Card', 'Accessory', 'Generic') not null,
    Description      text                                  null,
    constraint unq_category
        unique (CategoryName, CategoryType),
    constraint Category_ibfk_1
        foreign key (ParentCategoryID) references Category (CategoryID)
            on delete set null
);

create index ParentCategoryID
    on Category (ParentCategoryID);

create table Image
(
    ImageId   int auto_increment
        primary key,
    MimeType  varchar(255) not null,
    ImageData longblob     not null
);

create table OrderAddress
(
    OrderAddressID int auto_increment
        primary key,
    StreetAddress  text         not null,
    City           varchar(100) not null,
    State          varchar(100) null,
    PostalCode     varchar(20)  not null,
    Country        varchar(100) not null
);

create table Product
(
    ProductID     int auto_increment
        primary key,
    SKU           varchar(50)                                      not null,
    ProductName   varchar(255)                                     not null,
    BasePrice     decimal(10, 2)                                   not null,
    CurrentPrice  decimal(10, 2)                                   not null,
    StockQuantity int        default 0                             not null,
    ProductType   enum ('TradingCard', 'Accessory', 'BoosterPack') not null,
    CreatedAt     timestamp  default CURRENT_TIMESTAMP             null,
    LastUpdated   timestamp  default CURRENT_TIMESTAMP             null on update CURRENT_TIMESTAMP,
    IsActive      tinyint(1) default 1                             null,
    constraint SKU
        unique (SKU),
    check (`BasePrice` >= 0),
    check (`CurrentPrice` >= 0),
    check (`StockQuantity` >= 0)
);

create table Accessory
(
    AccessoryID   int                                                      not null
        primary key,
    AccessoryType enum ('Sleeves', 'Binders', 'Dice', 'Playmats', 'Boxes') not null,
    Material      varchar(100)                                             null,
    Color         varchar(50)                                              null,
    Dimensions    varchar(100)                                             null,
    Compatibility text                                                     null,
    constraint Accessory_ibfk_1
        foreign key (AccessoryID) references Product (ProductID)
            on delete cascade
);

create index idx_product_type
    on Product (ProductType);

create index idx_sku
    on Product (SKU);

create table ProductAttribute
(
    ProductID   int  not null,
    AttributeID int  not null,
    Value       text not null,
    primary key (ProductID, AttributeID),
    constraint ProductAttribute_ibfk_1
        foreign key (ProductID) references Product (ProductID)
            on delete cascade,
    constraint ProductAttribute_ibfk_2
        foreign key (AttributeID) references AttributeDefinition (AttributeID)
            on delete cascade
);

create index AttributeID
    on ProductAttribute (AttributeID);

create table ProductCategory
(
    ProductID  int not null,
    CategoryID int not null,
    primary key (ProductID, CategoryID),
    constraint ProductCategory_ibfk_1
        foreign key (ProductID) references Product (ProductID)
            on delete cascade,
    constraint ProductCategory_ibfk_2
        foreign key (CategoryID) references Category (CategoryID)
            on delete cascade
);

create index CategoryID
    on ProductCategory (CategoryID);

create table ProductImage
(
    ProductImageID int auto_increment
        primary key,
    ProductID      int           not null,
    SortOrder      int default 0 null,
    ImageID        int           null,
    constraint ProductImage_pk
        unique (ProductID, ImageID),
    constraint ProductImage_Image_ImageId_fk
        foreign key (ImageID) references Image (ImageId),
    constraint ProductImage_ibfk_1
        foreign key (ProductID) references Product (ProductID)
            on delete cascade
)
    row_format = DYNAMIC;

create index idx_product_images
    on ProductImage (ProductID);

create table TradingCard
(
    CardID        int                                                                                 not null
        primary key,
    CardSet       varchar(100)                                                                        not null,
    CardNumber    varchar(50)                                                                         not null,
    Rarity        enum ('Common', 'Uncommon', 'Rare', 'Mythic', 'Secret')                             not null,
    CardCondition enum ('Mint', 'Near Mint', 'Lightly Played', 'Moderately Played', 'Heavily Played') null,
    CardText      text                                                                                null,
    Artist        varchar(100)                                                                        null,
    YearPublished year                                                                                null,
    constraint unq_card_identifier
        unique (CardSet, CardNumber),
    constraint TradingCard_ibfk_1
        foreign key (CardID) references Product (ProductID)
            on delete cascade
);

create table User
(
    UserID       int auto_increment
        primary key,
    FirstName    varchar(255)                                                      not null,
    LastName     varchar(255)                                                      not null,
    Email        varchar(255)                                                      not null,
    PasswordHash varchar(255)                                                      not null,
    CreatedAt    timestamp                               default CURRENT_TIMESTAMP null,
    LastLogin    timestamp                                                         null,
    Role         enum ('Customer', 'Admin', 'Moderator') default 'Customer'        null,
    constraint Email
        unique (Email)
);

create table Address
(
    AddressID     int auto_increment
        primary key,
    UserID        int                          not null,
    StreetAddress text                         not null,
    City          varchar(100)                 not null,
    State         varchar(100)                 null,
    PostalCode    varchar(20)                  not null,
    Country       varchar(100)                 not null,
    AddressType   enum ('Shipping', 'Billing') not null,
    IsDefault     tinyint(1) default 0         null,
    constraint Address_ibfk_1
        foreign key (UserID) references User (UserID)
            on delete cascade
);

create index idx_user_address
    on Address (UserID, AddressType);

create table Cart
(
    CartID      int auto_increment
        primary key,
    UserID      int                                 not null,
    CreatedAt   timestamp default CURRENT_TIMESTAMP null,
    LastUpdated timestamp default CURRENT_TIMESTAMP null,
    constraint UserID
        unique (UserID),
    constraint Cart_ibfk_1
        foreign key (UserID) references User (UserID)
            on delete cascade
);

create index idx_cart_user
    on Cart (UserID);

create table CartItem
(
    CartItemID int auto_increment
        primary key,
    CartID     int                                 not null,
    ProductID  int                                 not null,
    Quantity   int                                 not null,
    AddedAT    timestamp default CURRENT_TIMESTAMP null,
    constraint unq_cart_product_variant
        unique (CartID, ProductID),
    constraint CartItem_ibfk_1
        foreign key (CartID) references Cart (CartID),
    constraint CartItem_ibfk_2
        foreign key (ProductID) references Product (ProductID),
    check (`Quantity` > 0)
);

create index ProductID
    on CartItem (ProductID);

create table `Order`
(
    OrderID           int auto_increment
        primary key,
    UserID            int                                                                                           not null,
    OrderDate         timestamp                                                           default CURRENT_TIMESTAMP null,
    OrderStatus       enum ('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled') default 'Pending'         null,
    ShippingAddressID int                                                                                           not null,
    BillingAddressID  int                                                                                           not null,
    TotalAmount       decimal(10, 2)                                                                                not null,
    constraint Order_ibfk_1
        foreign key (UserID) references User (UserID),
    constraint Order_ibfk_2
        foreign key (ShippingAddressID) references OrderAddress (OrderAddressID),
    constraint Order_ibfk_3
        foreign key (BillingAddressID) references OrderAddress (OrderAddressID)
);

create index UserID
    on `Order` (UserID);

create index idx_order_status
    on `Order` (OrderStatus);

create table OrderItem
(
    OrderItemID     int auto_increment
        primary key,
    OrderID         int            not null,
    ProductID       int            null,
    Quantity        int            not null,
    UnitPrice       decimal(10, 2) not null,
    ProductSnapshot json           not null,
    constraint OrderItem_ibfk_1
        foreign key (OrderID) references `Order` (OrderID)
            on delete cascade,
    constraint OrderItem_ibfk_2
        foreign key (ProductID) references Product (ProductID)
            on delete set null,
    check (`Quantity` > 0)
);

create index OrderID
    on OrderItem (OrderID);

create index ProductID
    on OrderItem (ProductID);

create table Review
(
    ReviewID     int auto_increment
        primary key,
    ProductID    int                                                                not null,
    UserID       int                                                                not null,
    Rating       tinyint                                                            not null,
    Title        varchar(255)                                                       null,
    ReviewText   text                                                               null,
    CreatedAt    timestamp                                default CURRENT_TIMESTAMP null,
    ReviewStatus enum ('Pending', 'Approved', 'Rejected') default 'Pending'         null,
    constraint ProductID
        unique (ProductID, UserID),
    constraint Review_ibfk_1
        foreign key (ProductID) references Product (ProductID)
            on delete cascade,
    constraint Review_ibfk_2
        foreign key (UserID) references User (UserID)
            on delete cascade,
    check (`Rating` between 1 and 5)
);

create index UserID
    on Review (UserID);

create index idx_reviews_product
    on Review (ProductID);

create index idx_email
    on User (Email);

