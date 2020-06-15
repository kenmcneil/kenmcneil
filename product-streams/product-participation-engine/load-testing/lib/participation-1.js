// Helper for data specific to participation@1

// The initial range of unique ids in the product table have large contiguous regions until about 410k.
const validUniqueIdRanges = [
	[1, 198419],
	[198421, 392079],
	[399709, 410228]
];
function makeUniqueIdsArray() {
	const ids = [];
	validUniqueIdRanges.forEach(range => {
		const startId = range[0];
		const endId = range[1];
		for (let i = startId; i <= endId; i++) {
			ids.push(NumberInt(i));
		}
	});
	return ids;
}
 testUniqueIds = makeUniqueIdsArray();


function makeRandomUniqueIds(count) {
	shuffle(testUniqueIds);
	return testUniqueIds.slice(0, count);
}

function makeCalculatedDiscountFixture(id, startDate, endDate, saleId, productCount) {
	const fixture = makeSaleIdOnlyFixture(id, startDate, endDate, saleId, productCount);

	// make up discount values
	if (id % 2 === 1) {
		fixture.content.priceDiscounts = {
			_type: 'atom-section@1.0.0',
			calculatedDiscount: {
				_type: 'atom-section@1.0.0',
				discountType: {
					value: 'amountDiscount',
					_type: 'atom-toggle-radios@1.0.0'
				},
				amountDiscount: {
					_type: 'atom-section@1.0.0',
					template: {
						selected: NumberInt(3),
						_type: 'atom-select@1.0.0'
					},
					pricebookId1: {
						text: '5',
						_type: 'atom-text@1.0.0'
					},
					pricebookId22: {
						text: '7',
						_type: 'atom-text@1.0.0'
					}
				}
			}
		};
	} else {
		fixture.content.priceDiscounts = {
			_type: 'atom-section@1.0.0',
			calculatedDiscount: {
				_type: 'atom-section@1.0.0',
				discountType: {
					value: 'percentDiscount',
					_type: 'atom-toggle-radios@1.0.0'
				},
				percentDiscount: {
					_type: 'atom-section@1.0.0',
					template: {
						selected: NumberInt(1),
						_type: 'atom-select@1.0.0'
					},
					pricebookId1: {
						text: '10',
						_type: 'atom-text@1.0.0'
					},
					pricebookId22: {
						text: '15',
						_type: 'atom-text@1.0.0'
					}
				}
			}
		};
	}

	return fixture;
}

function makeSaleIdOnlyFixture(id, startDate, endDate, saleId, productCount) {
	const uniqueIds = makeRandomUniqueIds(productCount);
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
			"_type" : "participation@1.1.0",
			"productSale" : {
				"saleId" : NumberInt(saleId),
				"_type" : "atom-product-sale@1.0.0"
			},
			"calculatedDiscounts" : {
				"_type" : "atom-section@1.0.0",
				"uniqueIds" : {
					"list" : uniqueIds,
					"_type" : "atom-id-list@1.0.0"
				}
			},
			"priceDiscounts" : {
				"_type" : "atom-section@1.0.0"
			}
		}
	};
}




