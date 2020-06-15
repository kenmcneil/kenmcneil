// Helper for data specific to participation-itemized@1

// Load in a file that has rows for the four columns of data required to make an itemized discount.
// -- uniqueId, manufacturer, pb1 target, pb22 target
// Also converts the values to be ready to insert to mongodb.
// This was created with this SQL script:
//
// 		SELECT top 200000
// 		    p.uniqueId,
// 		    m.name AS manufacturer,
// 		    p.uniqueId * 0.1 as pb1Price,
// 		    p.uniqueId * 0.01 as pb22Price
// 		FROM mmc.product.product p
// 		INNER JOIN mmc.product.family
// 		    ON family.id = p.familyId
// 		INNER JOIN mmc.dbo.manufacturer AS m
// 		    ON m.id = family.manufacturerId
// 		INNER JOIN mmc.dbo.Pricebook_Cost pbc1
// 		    ON pbc1.UniqueId = p.uniqueId
// 		    AND pbc1.PriceBookId = 1
// 		INNER JOIN mmc.dbo.Pricebook_Cost pbc22
// 		    ON pbc22.UniqueId = p.uniqueId
// 		    AND pbc22.PriceBookId = 22
// 		WHERE p.statusId = 6 and
// 		p.uniqueId > 800000
//
// Be careful to remove any column header from the top of the file, and any newlines from the end.
const itemizedDiscounts = cat('./itemized-discounts.csv')
	.split('\n')
	.map(row => row.split(',')
		.map((col, i) => {
			col = col.trim();
			if (i === 0) {
				return NumberInt(col);
			} else if (i === 2 || i === 3) {
				return Number(col);
			}
			return col.trim();
		})
	);
// printjson(itemizedDiscounts);

// Return a number of random rows from the loaded itemized discount data.
function getRandomItemizedDiscounts(count) {
	shuffle(itemizedDiscounts);
	return itemizedDiscounts.slice(0, count);
}

function makeItemizedDiscountFixture(id, startDate, endDate, saleId, productCount) {
	const discounts = getRandomItemizedDiscounts(productCount);

	return {
		"_id" : NumberInt(id),
		"saleId" : NumberInt(saleId),
		"description" : "Fixture " + id,
		"productUniqueIds" : [],
		"schedule" : {
			"from" : startDate,
			"to" : endDate
		},
		"status" : "DRAFT",
		"updateStatus": null,
		"starringUserIds" : [],
		"lastModifiedUserId" : NumberInt(defaultUserId),
		"lastModifiedDate" : new Date(),
		"deletedProductUniqueIds" : [],
		"content" : {
			"_type" : "participation-itemized@1.0.0",
			"productSale" : {
				"saleId" : NumberInt(saleId),
				"_type" : "atom-product-sale@1.0.0"
			},
			"itemizedDiscounts": {
				"_type": "atom-tuple-list@1.0.0",
				"list": discounts
			}
		}
	};
}
