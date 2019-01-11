# product-service

This repository is the home of a domain service used to manage product data. This service will provide a central mechanism by which various product data feeds can be ingested and stored. It will also provide a mechanism for broadcasting product data changes through well-defined events. The resulting events will be fed to a messaging broker such that all downstream systems can subscribe and act upon those events.

## This is a work in progress and will be under constant change.

## Links

The following are useful links to help clarify domain terminology and design decisions make within the context of this service.

- [The Product Service PRD](https://docs.google.com/document/d/1ro6jKQwZFOGIfYMqxy9_bSAVALk10kNN5OaLdARDidA/edit?usp=sharing)
- [Product Options and Product Variations](https://www.skuvault.com/blog/difference-between-product-options-and-product-variations)
- [Product Kitting](https://www.skuvault.com/blog/product-kitting-alternates-for-ecommerce)
- [GTIN](https://blog.datafeedwatch.com/how-to-find-the-gtins)
## Assertions

- This service must provide the ability to support a multi-tenant view of the data. This means that product data can be ingested and mutated based on a given business unit or store front.
- The storage mechanism for product data will not be visible to any systems external to the product service. This requirements means that the service must provide an API for getting data in/out of the underlying storage mechanism.
- The domain terminology defined by this service will be based off of industry standards.

## Core Product Data

The core product data is used to represent products that will be sold within an online store front.

- Products
- Product Variants
- Product Identifiers
- Product Options
- Product Meta-data
- Images
- Digital Assets

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
- Business Units (A business unit is an organization entity within Ferguson which might have different operational and financial rules. A business unit can have one or more store fronts associated with it)
- Store Fronts (A store front is represented as a unique web domain on which products are sold, a store front belongs to a busienss unit)
- Marketplaces (A marketplace is third-party channel in which online goods are sold, think : Amazon, Google's marketplace, etc.)
