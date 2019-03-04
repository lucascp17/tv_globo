<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style/bootstrap.min.css"/>
	<meta charset="UTF-8"/>
	<title>
		Projeto TV Globo
	</title>
	<style type="text/css">
		#dashboard_canvas {
		}
		.xfly {
		}
	</style>
</head>
<body>
	<nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0">
		<img src="asset/logo_globo.png" width="36" height="36" style="margin: 10px; margin-right: 20px;"/>
		<a id="campaign_title" class="navbar-brand col-sm-3 col-md-2 mr-0" href="javascript:void(0)">
			Carregando...
		</a>
		<ul class="navbar-nav px-3">
			<li class="nav-item text-nowrap">
				<a class="nav-link" href="javascript:goBack()">
					Voltar
				</a>
			</li>
		</ul>
	</nav>
	<div class="container">
		<div class="xfly d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom" style="margin-top: 20px;">
			<h1 class="h2">
				Distribuição dos votos
			</h1>
			<a class="btn btn-danger btn-sm" href="javascript:cancelPageCampaign()">
				Interromper campanha
			</a>
		</div>
		<div id="vote_items_container" class="container">
		</div>
		<div class="xfly d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom" style="margin-top: 20px;">
			<h1 class="h2">
				Audiência: Total de votos
			</h1>
		</div>
		<canvas id="dashboard_canvas" class="xfly" style="display: none;">
		</canvas>
		<p id="no_stats" class="lead text-center" style="display: none;">
			Sem votos ainda
		</p>
	</div>
	<script type="text/javascript" src="engine/charts.js"></script>
	<script type="text/javascript" src="engine/core.js"></script>
	<script type="text/javascript">
		let campaignId = ${param.campaign};
		let votesChart = null;
		function goBack() {
			window.history.back();
		}
		function switchBackground(index) {
			let modd = index % 8;
			switch (modd) {
			case 0:
				return '#CD6155';
			case 1:
				return '#AF7AC5';
			case 2:
				return '#48C9B0';
			case 3:
				return '#5D6D7E';
			case 4:
				return '#F4D03F';
			case 5:
				return '#99A3A4';
			case 6:
				return '#DC7633';
			case 7:
				return '#5DADE2';
			}
		}
		function watchVotes(itemId) {
			currentCount(itemId).then(newCount => {
				let compId = 'highlighted_' + itemId;
				let comp = document.getElementById(compId);
				comp.innerHTML = newCount;
			});
			setTimeout(() => watchVotes(itemId), 3000);
		}
		function watchStats() {
			getVoteStats(campaignId).then(stats => {
				if (stats.length > 0) {
					drawDashboard(stats);
				} else {
					document.getElementById('no_stats').style.display = 'block';
				}
				setTimeout(() => watchStats(), 3000);
			});
		}
		function drawDashboard(stats) {
			if (votesChart === null) {
				drawDashboardFirstTime(stats);
				return;
			}
			for (let i = 0; i < stats.length; ++i) {
				let tokens = stats[i].time.split("-");
				let year = parseInt(tokens[0]);
				let month = parseInt(tokens[1]) + 1;
				let day = parseInt(tokens[2]);
				let hour = parseInt(tokens[3]);
				let label = "" + day + "/" + month + "/" + year + " " + hour + ":00";
				let index = -1;
				for (let j = 0; j < votesChart.data.labels.length; ++j) {
					if (label === votesChart.data.labels[j]) {
						index = j;
						break;
					}
				}
				if (index < 0) {
					votesChart.data.labels.push(label);
					votesChart.data.datasets.forEach((dataset) => dataset.data.push(stats[i].count));
				} else {
					votesChart.data.datasets.forEach((dataset) => dataset.data[index] = stats[i].count);
				}
				votesChart.update();
			}
		}
		function drawDashboardFirstTime(stats) {
			let statsLabels = [];
			let statsData = [];
			for (let i = 0; i < stats.length; ++i) {
				let tokens = stats[i].time.split("-");
				let year = parseInt(tokens[0]);
				let month = parseInt(tokens[1]) + 1;
				let day = parseInt(tokens[2]);
				let hour = parseInt(tokens[3]);
				let label = "" + day + "/" + month + "/" + year + " " + hour + ":00";
				statsLabels.push(label);
				statsData.push(stats[i].count);
			}
			document.getElementById('no_stats').style.display = 'none';
		    var ctx = document.getElementById("dashboard_canvas");
		    ctx.style.display = 'block';
		    votesChart = new Chart(ctx, {
				type: 'line',
				data: {
					labels: statsLabels,
					datasets: [{
						data: statsData,
						lineTension: 0,
						backgroundColor: 'transparent',
						borderColor: '#007bff',
						borderWidth: 4,
						pointBackgroundColor: '#007bff'
					}]
				},
				options: {
					scales: {
						yAxes: [{
							ticks: {
								beginAtZero: false
							}
						}]
					},
					legend: {
						display: false
					}
				}
			});
		}
		function init() {
			getCampaign(campaignId).then(campaign => {
				document.getElementById('campaign_title').innerHTML = "Campanha " + campaign.name;
			});
			getVoteItems(campaignId).then(list => {
				let root = document.getElementById('vote_items_container');
				let row = null;
				for (let i = 0; i < list.length; ++i) {
					if (i % 4 === 0) {
						row = document.createElement('div');
						row.className = 'card-deck mb-3 text-center';
						root.appendChild(row);
					}
					let card = document.createElement('div');
					card.className = 'card mb-3 box-shadow';
					let header = document.createElement('div');
					header.className = 'card-header';
					let title = document.createElement('h4');
					title.className = 'my-0 font-weight-normal';
					title.innerHTML = list[i].keyword;
					header.appendChild(title);
					card.appendChild(header);
					let content = document.createElement('div');
					content.className = 'card-body';
					content.style.background = switchBackground(i);
					content.style.color = 'white';
					let highlighted = document.createElement('h1');
					highlighted.id = 'highlighted_' + list[i].id;
					highlighted.innerHTML = list[i].votes;
					content.appendChild(highlighted);
					card.appendChild(content);
					row.appendChild(card);
					watchVotes(list[i].id);
				}
			});
			watchStats();
		}
		function cancelPageCampaign() {
			let elems = document.getElementsByClassName('btn');
			for (let i = 0; i < elems.length; ++i)
				elems.disabled = true; 
			cancelCampaign(campaignId).then(() => { window.document.location = '${pageContext.request.contextPath}' });
		}
		init();
	</script>
</body>
</html>