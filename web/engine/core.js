function toPtBr(dateStr) {
	let tokens = dateStr.split("-");
	return tokens[2] + "/" + tokens[1] + "/" + tokens[0];
}

function getCampaigns() {
	return new Promise((resolve, reject) => {
		const ajax = new XMLHttpRequest();
		ajax.onreadystatechange = () => {
			if (ajax.readyState !== 4)
				return;
			if (ajax.status === 200)
				resolve(JSON.parse(ajax.responseText));
			else
				reject(ajax.status);
		};
		ajax.open('get', 'campaigns', true);
		ajax.setRequestHeader('accepts', 'application/json');
		ajax.send();
	});
}

function createCampaign(name, keyword, startDate, startTime, endDate, endTime) {
	return new Promise((resolve, reject) => {
		const data = { name, keyword, startDate, startTime, endDate, endTime }; 
		const ajax = new XMLHttpRequest();
		ajax.onreadystatechange = () => {
			if (ajax.readyState !== 4)
				return;
			if (ajax.status === 200)
				resolve(JSON.parse(ajax.responseText));
			else
				reject(ajax.status);
		};
		ajax.open('post', 'campaigns', true);
		ajax.setRequestHeader('content-type', 'application/json');
		ajax.setRequestHeader('accepts', 'application/json');
		ajax.send(JSON.stringify(data));
	});
}

function updateCampaign(id, name, startDate, startTime, endDate, endTime) {
	return new Promise((resolve, reject) => {
		const data = { name, startDate, startTime, endDate, endTime }; 
		const ajax = new XMLHttpRequest();
		ajax.onreadystatechange = () => {
			if (ajax.readyState !== 4)
				return;
			if (ajax.status === 200)
				resolve(JSON.parse(ajax.responseText));
			else
				reject(ajax.status);
		};
		ajax.open('post', 'campaigns', true);
		ajax.setRequestHeader('content-type', 'application/json');
		ajax.setRequestHeader('accepts', 'application/json');
		ajax.send(JSON.stringify(data));
	});
}

function getVoteItems(campaign) {
	return new Promise((resolve, reject) => {
		const ajax = new XMLHttpRequest();
		ajax.onreadystatechange = () => {
			if (ajax.readyState !== 4)
				return;
			if (ajax.status === 200)
				resolve(JSON.parse(ajax.responseText));
			else
				reject(ajax.status);
		};
		ajax.open('get', 'voteitems?campaign=' + campaign, true);
		ajax.setRequestHeader('accepts', 'application/json');
		ajax.send();
	});
}

function createVoteItems(campaign, keywords) {
	return new Promise((resolve, reject) => {
		const data = {campaign, keywords};
		const ajax = new XMLHttpRequest();
		ajax.onreadystatechange = () => {
			if (ajax.readyState !== 4)
				return;
			if (ajax.status === 200)
				resolve(JSON.parse(ajax.responseText));
			else
				reject(ajax.status);
		};
		ajax.open('post', 'voteitems', true);
		ajax.setRequestHeader('content-type', 'application/json');
		ajax.setRequestHeader('accepts', 'application/json');
		ajax.send(JSON.stringify(data));
	});
}
