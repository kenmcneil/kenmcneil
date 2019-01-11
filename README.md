# product-service

This repository is the home of a domain service used to manage product data. This service will provide a central mechanism by which various product data feeds can be ingested and stored. It will also provide a mechanism for broadcasting product data changes through well-defined events. The resulting events will be fed to a messaging broker such that all downstream systems can subscribe and act upon those events.

## This is a work in progress and will be under constant change.

## Links

The following are useful links to help clarify domain terminology and design decisions made within the context of this service.

- [The Product Service PRD](https://docs.google.com/document/d/1ro6jKQwZFOGIfYMqxy9_bSAVALk10kNN5OaLdARDidA/edit?usp=sharing)
- [Product Options and Product Variations](https://www.skuvault.com/blog/difference-between-product-options-and-product-variations)
- [Product Kitting](https://www.skuvault.com/blog/product-kitting-alternates-for-ecommerce)
- [GTIN](https://blog.datafeedwatch.com/how-to-find-the-gtins)
## Assertions

- This service must provide the ability to support a multi-tenant view of the data. This means that product data can be ingested and mutated based on a given business unit or channel.
- The storage mechanism for product data will not be visible to any systems external to the product service. This requirements means that the service must provide an API for getting data in/out of the underlying storage mechanism.
- The domain terminology defined by this service will be based off of industry standards.

## Core Product Data

The core product data is used to represent products that will be sold within an online store front. A "product" representations a family of closely related items called "variants" and defines all of the characteristics/attributes that are pertinent  to the product and its variants. Information that is common across the variants is represented at the product level while each unique variant will have a slightly different configuration of the product's characteristics. Variants represent the unique, sellable items and a user selects variants when adding things to their shopping cart. A product's manufacturer information is also represented at the product level.

- Products
- Product Variants
- Product Identifiers
- Product Attributes (Product characteristics)
- Product Options (Product customizations)
- Digital Assets (Images, pdfs, AR models, etc.)

### Identifyng a Product and a Product Variant
A manufacturer and the manufacturer's assigned product ID are enough to identify a product family but a more specific identifier must be used to identify a product variant. The global identifier that is used by ferguson to reference a product variant is called an MPN (Master Product Number) ID. Additionally, there are alternate identifiers that can be assigned to a product variant and these identifiers are modeled as an identifier type ((GTIN, SKU, UPC, etc) and the actual identifier value.

### Product Attributes
A product attribute is a characteristic of the product and/or variants of that product. The attribute's validation rules are dictated through the attribute definition. An attribute that does not change across variants will have its value defined at the product level, while an attribute that does change between variants will have its value defined within each variant.
 
> **IMPORTANT:** The collection of product attributes can be validated with a taxonomy's category traits to insure a product has the traits required to be added to that category. The commonality between attributes and traits is they must both be linked to the same attribute definition.**

### Product Options
A product option is represented almost identically to a product attribute but represents a "customization" that can be made to a product variant that does NOT change the variant's SKU. The best way to distinguish between an attribute and an option is to think of a simple use case:

A Shirt has a size and a color and also allows a customer to select a name that will be printed on the back of the shirt. The "size" and "color" are product attributes and each combination of those two will result in a different product variant. The "name" is a customization that will be applied to item variant (by the vendor/manufacturer).
 
## Product Taxonomy

A product taxonomy is a hierarchical classification system where products are grouped into categories/sub-categories. A product category is a grouping of products and can, optionally, have a set of sub-categories that can be used to drill-down into more-specific groupings. A category also has a set of "traits" that define what types of products can be added to that category. The traits can also be used to derive search facets for that category.

> **IMPORTANT:** A taxonomy can be marked as "strict" which means that a product may only be assigned ONCE within the classification hierarchy. A "non-strict" taxonomy allows a product to be placed within mulitple categories within the classification hierarchy. A "strict" taxonomy is more appropriate for defining SEO strategies while a "non-strict" taxonomy can be used for site navigation.  It is important to understand that more than one taxonomy can be defined for a given sales channel where one taxonomy may reflect an SEO classification and another may reflect the a site's navigation system. 

- Taxonomies
- Categories
- Category Traits
 

## Product Pricing

A pricing "profile" represents the retail pricing for products and allows for different pricing across stores and/or for professionals or end consumers.

- Pricing Profiles/Pricebooks

## Product Inventory

The inventory feed may/may not belong in the product service, as an argument can be made that it might be better to locate this feed within the order management system. For now, we will assume the inventory will be hosted in this service but will be modularized (and encapsulated) to make it easy to move.

- Vendor Inventory 

## Related Data

Related data consists of domain concepts that are not directly managed by the product service but required to support the business use cases. These types of domains are imported "copies" of data that may be managed by a separate system.

- Manufacturers (Each product will be associated with its manufacturer.)
- Vendors (Vendors are third-parties that are selling the products, used only in the inventory feeds.)
- Business Units (A business unit is an organization entity within Ferguson which might have different operational and financial rules. A business unit can have one or more channels associated with it.)
- Channels (A channel represents a distribution channel through which products are sold. A channel can represent a product inventory sold through a web store front or through a third-party marketplace) 
