/**
 * make-participation-fixtures-large.js
 *
 * This script should be run in the mongo shell:
 * 		mongo mongodb-dev-1.build.internal:27017/core make-participation-fixtures-large.js
 */

var uniqueIds = [];
for (var i = 1; i <= 100000; i++) {
	uniqueIds.push(i);
}

var seed = 1;
function random() {
	var x = Math.sin(seed++) * 10000;
	return x - Math.floor(x);
}

var newId = 50000;

db.participationItem.remove({ _id: { $gte: 50000 } });

db.loadTestUnalteredData
	.find()
	.limit(3)
	.forEach(function(p) {
		const duration = 5; // minutes
		const startDate = new Date(new Date().getTime() + 1 * 60000); // ISODate('2019-11-14T22:57:00.000Z');
		const endDate = new Date(startDate.getTime() + duration * 60000);
		const startDate2 = endDate;
		const endDate2 = new Date(startDate2.getTime() + duration * 60000);

		const rnd = random();
		let uniqueIdCount;
		switch (true) {
			case rnd < 0.05: {
				uniqueIdCount = 100000;
				break;
			}
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

		p._id = newId;
		p.content._type = 'participation@1.1.0';
		p.status = 'DRAFT';
		p.updateStatus = null;

		if (newId % 2 === 0) {
			p.schedule = {
				from: startDate,
				to: endDate
			};
		} else {
			p.schedule = {
				from: startDate2,
				to: endDate2
			};
		}

		p.content.calculatedDiscounts.uniqueIds.list = uniqueIds.slice(0, uniqueIdCount).map((id) => NumberInt(id));

		// make up discount values for p here
		if (newId % 2 === 0) {
			p.content.priceDiscounts = {
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
			p.content.priceDiscounts = {
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

		db.participationItem.insert(p);

		newId++;

		// if you want to log the change:
		printjson(p);
	});
