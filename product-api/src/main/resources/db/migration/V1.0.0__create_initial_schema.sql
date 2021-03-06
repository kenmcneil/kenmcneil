-- This schema is for provisioning the product database on MS SQL Server

-- The create database statement must be ran as a stand alone command, then the rest of the script can be run.
CREATE DATABASE product;
use product;

-- Unit of measure table is used to standardize the "measurement units" used by a given attribute definition.
CREATE TABLE UnitOfMeasure (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	code VARCHAR(10) UNIQUE,
	name VARCHAR(60),
	description VARCHAR(200),
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,
	version SMALLINT
);

-- Enumeration for for attribute datatype.
CREATE TABLE AttributeDatatype (
	id SMALLINT PRIMARY KEY,
	name VARCHAR(60) UNIQUE
);
INSERT INTO AttributeDatatype (id, name) values (1, 'BOOLEAN');
INSERT INTO AttributeDatatype (id, name) values (2, 'NUMERIC');
INSERT INTO AttributeDatatype (id, name) values (3, 'STRING');


-- An attribute definition is a re-usable data structure for describing an attribute. These definitions can
-- then been applied to taxonomy category attributes, product attributes, and product options.
-- The definitions define the datatype, any validation rules, and enumerated values that can be assigned to an
-- those three structures. The definitions provide a data dictionary that can be reused across the application
-- and can enforce rules like "this product can only be linked to this category if it has all of the attribtues
-- required by the category". This normalizes the attribute definitions and provides consistency: An attribute
-- definition of "Center to Center in Inches" in a "Handle Type" category has the same meaning as "Center to
-- Center in Inches" when defined on a handle type product.
CREATE TABLE AttributeDefinition (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	code VARCHAR(10) UNIQUE,
	attributeDatatypeId SMALLINT,  -- datatype is an enumeration: "BOOLEAN", "NUMERIC", "STRING"
	description VARCHAR(200),
	unitOfMeasureId INTEGER,
	minimumValue VARCHAR(60),
	maximumValue VARCHAR(60),
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,
	version SMALLINT,
	CONSTRAINT fk_AttributeDefinition_unitOfMeasureId FOREIGN KEY (unitOfMeasureId) REFERENCES UnitOfMeasure(id),
	CONSTRAINT fk_AttributeDefinition_attributeDatatypeId FOREIGN KEY (attributeDatatypeId) REFERENCES AttributeDatatype(id)
);

-- In most of the cases for attribute definitions, there will be a finite set of values that can be defined
-- for that attribute. This table is used to store the "allowed" values for a given attribute definition.
CREATE TABLE AttributeDefinitionValue (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	attributeDefinitionId INTEGER,
	value VARCHAR(60), -- The actual value
	displayValue VARCHAR(60), -- A display value is useful when the actual value is decimal: .500 inches is displayed as "1/2 Inch"
	CONSTRAINT fk_AttributeDefinitionValue_attributeDefinitionId FOREIGN KEY (attributeDefinitionId) REFERENCES AttributeDefinition(id)
);

-- A taxonomy is used to manage a hierarchial product classification system. It has a reference to a root category
-- that is the top-level parent for the entire category tree. A taxonomy and a product catalog are closely related,
-- however, there are differences between the two. A product can only be placed once within a taxonomy's category
-- tree whereas a product can be placed in multiple categories within a product catalog. A taxonomy can be used to
-- standardized product ingestion, enforcing which set of attributes are required for product classification, and
-- drives reporting. A product catalog is often derived from the taxonomy structure and is typically used for site
-- navigation and driven by merchandising-based analytics. An important point to make: Taxonomies require a product
-- only be placed once within the classification system whereas a product catalog will likely place the same product
-- into multiple categories to promote the sale and visibility of the product.
CREATE TABLE Taxonomy (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	code VARCHAR(10) UNIQUE,
	description VARCHAR(200),
	categoryId INTEGER,
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,
	version SMALLINT
);

-- The taxonomy categories are arranged into a hierarchy of parent/child relationships. The only category that
-- does not have a parent is the "root" category that is linked to the taxonomy record.
CREATE TABLE TaxonomyCategory (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	taxonomyId INTEGER,
	code VARCHAR(10) UNIQUE,
	name VARCHAR(60),
	path VARCHAR(600),
	description VARCHAR(200),
	parentCategoryId INTEGER,
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,	
	version SMALLINT,
	CONSTRAINT fk_TaxonomyCategory_taxonomyId FOREIGN KEY (taxonomyId) REFERENCES Taxonomy(id),
	CONSTRAINT fk_TaxonomyCategory_parentCategoryId FOREIGN KEY (parentCategoryId) REFERENCES TaxonomyCategory(id)
);

-- Each taxonmoy category can be associated with zero or more attributes. The "rules" for the attribute are
-- driven by the linked attribute definition and a category attribute can be marked as "required". Any
-- product that is ultimately linked to the category must have all of the required attributes defined by the
-- category and any of its parent categories. The attribute definition is the commonality between the category
-- and product attributes. The actual logic for enforcing the required attributes is done in the Java API.
CREATE TABLE TaxonomyCategoryAttribute(
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	taxonomyCategoryId INTEGER,
	attributeDefinitionId INTEGER,
	isRequired BIT,
	CONSTRAINT fk_TaxonomyCategoryAttribute_taxonomyCategoryId FOREIGN KEY (taxonomyCategoryId) REFERENCES TaxonomyCategory(id),
	CONSTRAINT fk_TaxonomyCategoryAttribute_attributeDefinitionId FOREIGN KEY (attributeDefinitionId) REFERENCES AttributeDefinition(id)
);

-- A link table between a taxomony category and a product. This link table is managed by a product classification
-- service and is an "eventually consistent" structure and does NOT have FK constraints.
CREATE TABLE TaxonomyCategoryProduct(
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	taxonomyCategoryId INTEGER,
	productId INTEGER,
	CONSTRAINT nux_TaxonomyCategoryProduct_taxonomyCategoryId_productId UNIQUE (taxonomyCategoryId, productId)
);

-- Enumeration for distribution/sales channel types.
CREATE TABLE ChannelType (
	id SMALLINT PRIMARY KEY,
	name VARCHAR(60) UNIQUE
);
INSERT INTO ChannelType (id, name) values (1, 'WEB_STORE');
INSERT INTO ChannelType (id, name) values (2, 'MARKETPLACE');

-- Enumeration of business units within Ferguson
CREATE TABLE BusinessUnit (
	id SMALLINT PRIMARY KEY,
	name VARCHAR(60) UNIQUE
);
INSERT INTO BusinessUnit (id, name) values (1, 'BUILD');
INSERT INTO BusinessUnit (id, name) values (2, 'SUPPLY');

-- A "channel" represents a sales/distribution channel within an omni-channel organization. Each channel is
-- assigned a taxonomy which then drives which products are distributed/available.
CREATE TABLE Channel (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	code VARCHAR(10) UNIQUE,
	description VARCHAR(200),
	channelTypeId SMALLINT, -- Channel type is an enumeration : "WEB_STORE", "MARKETPLACE"
	businessUnitId SMALLINT, -- Business unit is an enumeration: "BUILD", "SUPPLY"
	isActive BIT,
	taxonomyId INTEGER,
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,
	version SMALLINT,
	CONSTRAINT fk_Channel_channelTypeId FOREIGN KEY (channelTypeId) REFERENCES ChannelType(id),
	CONSTRAINT fk_Channel_businessUnitId FOREIGN KEY (businessUnitId) REFERENCES BusinessUnit(id),
	CONSTRAINT fk_Channel_taxonomyId FOREIGN KEY (taxonomyId) REFERENCES Taxonomy(id)
);

-- A manufacturer represents an entity that creates/builds products that are sold through the various sales channels.
-- It is important to distinguish the difference between a manufacturer and a vendor. A manufacturer is the "maker"
-- of the product whereas the vendor is the distributor/reseller of those products
CREATE TABLE Manufacturer (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	name VARCHAR(60) UNIQUE,
	description VARCHAR(200),
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,
	version SMALLINT
);


-- The types of digital resources that can be associated with product data. Currently, just images and documents.
CREATE TABLE DigitalResourceType(
	id SMALLINT PRIMARY KEY,
	name VARCHAR(60) UNIQUE
);
INSERT INTO DigitalResourceType (id, name) values (1, 'IMAGE');
INSERT INTO DigitalResourceType (id, name) values (2, 'DOCUMENT');
INSERT INTO DigitalResourceType (id, name) values (3, 'AR_MODEL');

-- A digital resource is a reference to digital resource abstract "path". The loading/linking to the actual resource
-- can be changed without impacting all of the entities that can be associated with a digital resource. The digital
-- resource table is treated as a child of the linking entity and will be managed by the parent. This means that we
-- might have multiple records in this table that have the same resource path. 
CREATE TABLE DigitalResource(
	id BIGINT PRIMARY KEY IDENTITY(1,1),
	digitalResourceTypeId SMALLINT, -- enumeration "IMAGE", "DOCUMENT", "AR_MODEL"
	resourcePath VARCHAR(400),
	CONSTRAINT fk_DigitalResource_digitalResourceTypeId FOREIGN KEY (digitalResourceTypeId) REFERENCES digitalResourceType(id)
);

-- Often a manufacturer will have a family of products and associate them with a product series. Example: Kohler may
-- have several products that belong to the "Iron Tones" series that includes a common theme for towel racks,
-- faucets, and bathtub hardware. TBD, should a manufacturer be added to this table?
CREATE TABLE ProductSeries (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	name VARCHAR(60) UNIQUE,
	description VARCHAR(200),
	tagline VARCHAR(200),
	splashDigitalResourceId BIGINT,
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,
	version SMALLINT,
	CONSTRAINT fk_ProductSeries_splashDigitalResourceId FOREIGN KEY (splashDigitalResourceId) REFERENCES DigitalResource(id)
);

-- The collection type indicates if a product is a single item, a bundle, or a package.
CREATE TABLE ProductCollectionType(
	id SMALLINT PRIMARY KEY,
	name VARCHAR(60) UNIQUE
);
INSERT INTO ProductCollectionType (id, name) values (1, 'PRODUCT');
INSERT INTO ProductCollectionType (id, name) values (2, 'BUNDLE');
INSERT INTO ProductCollectionType (id, name) values (3, 'PACKAGE');

create table Product (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	productIdentifier VARCHAR(80), -- This is product ID assigned by the manufacturer
	title VARCHAR(120), --This is unnaturally high due to legacy data (I found one with 360 characters!)
	description VARCHAR(8000), --Today, this allows html and can be quite large in our legacy data.
	manufacturerId INTEGER,
	productSeriesId INTEGER,
	productCollectionTypeId SMALLINT,
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,
	version SMALLINT,
	CONSTRAINT fk_Product_manufacturerId FOREIGN KEY (manufacturerId) REFERENCES Manufacturer(id),
	CONSTRAINT fk_Product_productSeriesId FOREIGN KEY (productSeriesId) REFERENCES ProductSeries(id),
	CONSTRAINT fk_Product_productCollectionTypeId FOREIGN KEY (productCollectionTypeId) REFERENCES ProductCollectionType(id)
);

-- An attribute associated with the product.
create table ProductAttribute(
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	productId INTEGER,
	attributeDefinitionId INTEGER,
	isOverrideAllowed BIT, -- can this attribute value be overridden at the variant level?
	value VARCHAR(60), -- required if override is false.
	CONSTRAINT fk_ProductAttribute_productId FOREIGN KEY (productId) REFERENCES Product(id),
	CONSTRAINT fk_ProductAttribute_attributeDefinitionId FOREIGN KEY (attributeDefinitionId) REFERENCES AttributeDefinition(id)
);

-- An options/customizations that can be modified on the product. These must be collected when the product
-- is added to a cart.
create table ProductOption(
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	productId INTEGER,
	attributeDefinitionId INTEGER,
	CONSTRAINT fk_ProductOption_productId FOREIGN KEY (productId) REFERENCES Product(id),
	CONSTRAINT fk_ProductOption_attributeDefinitionId FOREIGN KEY (attributeDefinitionId) REFERENCES AttributeDefinition(id)
);

-- A list of pre-defined values that can be selected by the user when adding the product to their cart. An option value
-- may or may not impact the price of the item and price of each value should be applied to the item.
create table ProductOptionValue(
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	productOptionId INTEGER,
	value VARCHAR(60), -- The list of possible values will be restricted if the attribute definition defines enumerated values.
	price NUMERIC(13,4), -- any additional cost (or less cost) when the customer selects a given option.
	CONSTRAINT fk_ProductOptionValue_productOptionId FOREIGN KEY (productOptionId) REFERENCES ProductOption(id)
);

-- Images, documents and AR assets associated to the product.
create table ProductDigitalResource(
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	productId INTEGER,
	digitalResourceId BIGINT,
	CONSTRAINT fk_ProductDigitalResource_productId FOREIGN KEY (productId) REFERENCES Product(id),
	CONSTRAINT fk_ProductDigitalResource_digitalResourceId FOREIGN KEY (digitalResourceId) REFERENCES DigitalResource(id)
);

-- The status of a variant will impact it's availability within a distribution/sales channel.
-- Please note, stock/non-stock have been combined to "active" and the inventory feed will
-- track stock/non-stock.
create table ProductVariantStatus (
	id SMALLINT PRIMARY KEY,
	name VARCHAR(60) UNIQUE
);
INSERT INTO ProductVariantStatus (id, name) values (1, 'ACTIVE');
INSERT INTO ProductVariantStatus (id, name) values (2, 'PENDING');
INSERT INTO ProductVariantStatus (id, name) values (3, 'NOT_APPROVED');
INSERT INTO ProductVariantStatus (id, name) values (4, 'DISCONTINUED');
INSERT INTO ProductVariantStatus (id, name) values (5, 'REMOVED');

-- The product variant is a tangible unit of merchandise that has a specific name, part number, size, price,
-- and any other attribute required to make the merchandise ???sellable???
-- This table is still under development and subject to change.
create table ProductVariant (
	id INTEGER PRIMARY KEY IDENTITY(1,1),
	productId INTEGER,
	description VARCHAR(8000), --Today, this allows html and can be quite large in our legacy data.
	productVariantStatusId SMALLINT, -- Enumeration
	msrp numeric(13,4), -- manufacturer suggested retail price.
	weight numeric(12,4), -- The weight of the product.
	weightUnitOfMeasureId INTEGER, -- The unit of measure in which the weight is expressed.
	isShippable BIT,
	isShippableToForeignCountry BIT,	
	createdTimestamp DATETIME2,
	lastModifiedTimestamp DATETIME2,
	version SMALLINT,
	CONSTRAINT fk_ProductVariant_productId FOREIGN KEY (productId) REFERENCES Product(id),
	CONSTRAINT fk_ProductVariant_productVariantStatusId FOREIGN KEY (productVariantStatusId) REFERENCES ProductVariantStatus(id),
	CONSTRAINT fk_ProductVariant_weightUnitOfMeasureId FOREIGN KEY (weightUnitOfMeasureId) REFERENCES UnitOfMeasure(id)
);

