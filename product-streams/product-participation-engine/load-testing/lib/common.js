// Helper for common code not specific to a participation type.

var defaultUserId = 2524; // jason conkey


var seed = 1;

function random() {
	let x = Math.sin(seed++) * 10000;
	return x - Math.floor(x);
}

// returns an int value from start to (endExclusive - 1)
function randomIntInRange(start, endExclusive) {
	return Math.floor(random() * (endExclusive - start) + start);
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

// This list was obtained with:
//    SELECT TOP (80) saleId FROM mmc.dbo.product_Sale ORDER BY saleId DESC
// This should be updated every load test, since sale ids change frequently.
var validSaleIds = [
	8153, 8152, 8151, 8150, 8149, 8148, 8147, 8146, 8145, 8144,
	8143, 8142, 8141, 8140, 8139, 8138, 8137, 8136, 8135, 8134,
	8133, 8132, 8131, 8130, 8129, 8128, 8127, 8126, 8125, 8124,
	8123, 8122, 8120, 8119, 8118, 8117, 8116, 8115, 8112, 8111,
	8110, 8109, 8108, 8101, 8100, 8099, 8098, 8097, 8096, 8095,
	8094, 8093, 8092, 8091, 8090, 8089, 8088, 8087, 8086, 8085,
	8084, 8083, 8082, 8081, 8080, 8079, 8078, 8077, 8076, 8075,
	8074, 8073, 8072, 8071, 8070, 8069, 8068, 8066, 8065, 8064
];

function getRandomSaleId() {
	return validSaleIds[Math.floor(random() * validSaleIds.length)];
}

function clearLoadTestingData() {
	print(`\nRemoving all Participation records with id >= ${minParticipationId}.`);
	db.contentEvent.remove({
		participationItem: { $exists: true },
		'participationItem._id': { $gte: minParticipationId }
	});
	db.participationItem.remove({ _id: { $gte: minParticipationId } });
}

function getRandomProductCount(maxCount) {
	const rnd = random();
	switch (true) {
		case rnd < 0.05 && maxCount >= 100000:
			return 100000;
		case rnd < 0.1 && maxCount >= 50000:
			return 50000;
		case rnd < 0.15 && maxCount >= 20000:
			return 20000;
		case rnd < 0.40 && maxCount >= 10000:
			return 10000;
		case rnd < 0.75 && maxCount >= 5000:
			return 5000;
		default:
			return Math.floor(random() * 1000);
	}
}
