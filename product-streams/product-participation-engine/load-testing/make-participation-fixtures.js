/**
 * make-participation-fixtures.js
 *
 * This script should be run in the mongo shell:
 * 		mongo mongodb-dev-1.build.internal:27017/core make-participation-fixtures.js
 */

var newId = 5000;

db.participationItem.remove({ _id: { $gte: 5000 } });

// This relies on the records in the loadTestUnalteredData collection in the dev database.
// TODO Create new Participation records here instead of copying from this collection...
db.loadTestUnalteredData
	.find()
	.limit(3)
	.forEach(function(p) {
		const content = p.content;

		const duration = 2; // minutes
		const startDate = new Date(new Date().getTime() + 0 * 60000);
		const endDate = new Date(startDate.getTime() + duration * 60000);

		p._id = newId;
		p.content._type = 'participation@1.1.0';
		p.status = 'DRAFT';
		p.updateStatus = null;
		p.schedule = {
			from: startDate,
			to: endDate
		};

		// make up discount values for p here
		// ... replace hardcoded values here with the made-up discounts
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
