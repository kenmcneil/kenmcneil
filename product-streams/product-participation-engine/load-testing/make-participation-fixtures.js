// make-participation-fixtures.js
// usage: mongo mongodb-dev-1.build.internal:27017/core make-participation-fixtures.js

var numberOfFixturesToCreate = 100;
var logFixtures = false;

var minParticipationId = 50000;

var duration = 10; // minutes
var startDate = new Date(new Date().getTime() + 0 * 60000);
var endDate = new Date(startDate.getTime() + duration * 60000);

var defaultUserId = 2524; // jason conkey

var seed = 1;
function random() {
	var x = Math.sin(seed++) * 10000;
	return x - Math.floor(x);
}

// This list was obtained with:
//    SELECT TOP (20) saleId FROM mmc.dbo.product_Sale ORDER BY saleId DESC
var validSaleIds = [
	8044, 8042, 8041, 8040, 8039, 8038, 8036, 8035, 8031, 8030,
	8029, 8028, 8026, 8025, 8022, 8020, 8019, 8018, 8016, 8015
];
function getRandomSaleId() {
	return validSaleIds[Math.floor(random() * validSaleIds.length)];
}

function shuffle(array) {
	var currentIndex = array.length;
	var temporaryValue, randomIndex;

	// While there remain elements to shuffle...
	while (0 !== currentIndex) {
		// Pick a remaining element...
		randomIndex = Math.floor(random() * currentIndex);
		currentIndex -= 1;

		// And swap it with the current element.
		temporaryValue = array[currentIndex];
		array[currentIndex] = array[randomIndex];
		array[randomIndex] = temporaryValue;
	}
}

function makeUniqueIdsArray(minUniqueId, maxUniqueId) {
	const ids = [];
	for (var i = minUniqueId; i <= maxUniqueId; i++) {
		ids.push(i);
	}
	return ids;
}

function makeRandomUniqueIds(count) {
	shuffle(allUniqueIds);
	return allUniqueIds.slice(0, count);
}

function makeFixture(id, startDate, endDate, saleId, uniqueIds) {
	const fixture = {
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
		"updateStatus" : "NEEDS_UPDATE",
		"content" : {
			"_type" : "participation@1.1.0",
			"productSale" : {
				"saleId" : NumberInt(saleId),
				"_type" : "atom-product-sale@1.0.0"
			},
			"calculatedDiscounts" : {
				"_type" : "atom-section@1.0.0",
				"uniqueIds" : {
					"list" : uniqueIds.map(id => NumberInt(id)),
					"_type" : "atom-id-list@1.0.0"
				}
			},
			"priceDiscounts" : {
				"_type" : "atom-section@1.0.0"
			}
		}
	};

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

var allUniqueIds = makeUniqueIdsArray(1, 1000000);


print(`Removing all ParticipationItem records with id >= ${minParticipationId}.`);
db.participationItem.remove({ _id: { $gte: minParticipationId } });

print(`Inserting ${numberOfFixturesToCreate} ParticipationItem fixtures...`);
for (let newId = minParticipationId; newId < minParticipationId + numberOfFixturesToCreate; newId++) {
	const saleId = getRandomSaleId();

	const rnd = random();
	let uniqueIdCount;
	switch (true) {
		// case rnd < 0.05: {
		// 	uniqueIdCount = 100000;
		// 	break;
		// }
		case rnd < 0.1: {
			uniqueIdCount = 50000;
			break;
		}
		case rnd < 0.15: {
			uniqueIdCount = 20000;
			break;
		}
		case rnd < 0.45: {
			uniqueIdCount = 10000;
			break;
		}
		default: {
			uniqueIdCount = Math.floor(random() * 1000);
		}
	}

	const uniqueIds = makeRandomUniqueIds(uniqueIdCount);

	const fixture = makeFixture(newId, startDate, endDate, saleId, uniqueIds);

	db.participationItem.insert(fixture);

	if (logFixtures) {
		printjson(fixture);
	} else {
		print("Inserted fixture " + newId + " with " + uniqueIds.length + " products.");
	}
}
