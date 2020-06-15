// make-participation-mixed-fixtures.js
// usage: mongo mongodb-dev-1.build.internal:27017/core make-participation-mixed-fixtures.js

load('./lib/common.js');
load('./lib/participation-1.js');
load('./lib/participation-itemized-1.js');

// ==================================== START TEST CONFIGURATION ====================================

const logFixtures = false;
const minParticipationId = 50000;

const startDelay = 2; // minutes
const testDuration = 10; // minutes
const minParticipationDurationMs = 1.5 * 60000; // e.g. 5 * 60000 = 5 min
const maxParticipationDurationMs = 5 * 60000; // e.g. 5 * 60000 = 5 min

let testConfig = [
	{
		count: 33,
		maxCount: 20000,
		type: 'saleId'
	},
	{
		count: 33,
		maxCount: 20000,
		type: 'calcDisc'
	},
	{
		count: 34,
		maxCount: 20000,
		type: 'itemDisc'
	},
];

// ===================================== END TEST CONFIGURATION =====================================

const minStartMs = new Date().getTime() + startDelay * 60000;
const maxEndMs = minStartMs + testDuration * 60000;
// print('\nStart and end load test range:');
// print(new Date(minStartMs).toISOString());
// print(new Date(maxEndMs).toISOString());

// Using test configuration values, generate a random start and end date within the load test window.
// Returns array of [startDateMs, endDateMs].
function getRandomFixtureDateRange() {
	const durationMs = minParticipationDurationMs + Math.floor(random() * (maxParticipationDurationMs - minParticipationDurationMs));
	const startDateMs = minStartMs + Math.floor(random() * (maxEndMs - durationMs - minStartMs));
	const endDateMs = startDateMs + durationMs;
	return [new Date(startDateMs), new Date(endDateMs)];
}
// print('\nRandom start and end ranges:');
// for (let z = 0; z < 80; z++) {
// 	getRandomFixtureDateRange().map(d => print(d.toISOString()));
//  print();
// }

function buildFixtureOfType(fixtureType, newId, startDate, endDate, saleId, productCount) {
	switch (fixtureType) {
		case 'saleId':
			return makeSaleIdOnlyFixture(newId, startDate, endDate, saleId, productCount);
		case 'calcDisc':
			return makeCalculatedDiscountFixture(newId, startDate, endDate, saleId, productCount);
		case 'itemDisc':
			return makeItemizedDiscountFixture(newId, startDate, endDate, saleId, productCount);
		default:
			return undefined;
	}
}

const numberOfFixturesToCreate = testConfig.reduce((sum, config) => sum + config.count, 0);

clearLoadTestingData();
print(`\nInserting ${numberOfFixturesToCreate} ParticipationItem fixtures...\n`);

let newId = minParticipationId;
while (testConfig.length > 0) {
	const config = testConfig[randomIntInRange(0, testConfig.length)];
	if (config.count > 0) {
		config.count--;

		const [startDate, endDate] = getRandomFixtureDateRange();
		const productCount = getRandomProductCount(config.maxCount);
		const saleId = getRandomSaleId();
		const fixture = buildFixtureOfType(config.type, newId, startDate, endDate, saleId, productCount);

		db.participationItem.insert(fixture);

		if (fixture.content._type.startsWith('participation@')) {
			print('"' + fixture.description + '", size ' + fixture.content.calculatedDiscounts.uniqueIds.list.length + ':');
		} else {
			print('"' + fixture.description + '", size ' + fixture.content.itemizedDiscounts.list.length + ':');
		}
		if (logFixtures) {
			printjson(fixture);
		}
	}
	if (config.count < 1) {
		testConfig = testConfig.filter(c => c.count > 0);
	}
	newId++;
}
