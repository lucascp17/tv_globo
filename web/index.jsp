<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style/bootstrap.min.css"/>
	<meta charset="UTF-8"/>
	<title>
		Projeto TV Globo
	</title>
</head>
<body>
	<div class="container">
		<%@include file="header.jsp"%>
		<div id="loading_section" class="py-5 text-center">
			<p class="lead">
				Aguarde...
			</p>
		</div>
		<div id="no_campaigns_section" class="py-5 text-center" style="display: none;">
			<p class="lead">
				Você não possui campanhas. Clique no botão abaixo para criar uma.
			</p>
			<p class="lead">
				<a class="btn btn-primary btn-lg" href="new_campaign.jsp">
					Criar campanha
				</a>
			</p>
		</div>
		<div id="campaigns_section" class="py-5 text-center" style="display: none;">
			<div class="row">
				<table class="table table-hover">
					<thead>
						<tr>
							<th>
								Nome
							</th>
							<th>
								Palavra-chave
							</th>
							<th>
								Início
							</th>
							<th>
								Fim
							</th>
						</tr>
					</thead>
					<tbody id="campaign_table">
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/engine/core.js"></script>
	<script type="text/javascript">
		function init() {
			getAllCampaigns().then(list => {
				document.getElementById('loading_section').style.display = 'none';
				if (list.length > 0) {
					let table = document.getElementById('campaign_table');
					for (let i = 0; i < list.length; ++i) {
						let row = document.createElement('tr');
						let cell = document.createElement('td');
						cell.innerHTML = list[i].name;
						row.appendChild(cell);
						cell = document.createElement('td');
						cell.innerHTML = list[i].keyword;
						row.appendChild(cell);
						cell = document.createElement('td');
						cell.innerHTML = toPtBr(list[i].startDate) + " " + list[i].startTime;
						row.appendChild(cell);
						cell = document.createElement('td');
						if (list[i].endDate)
							cell.innerHTML = toPtBr(list[i].endDate) + " " + list[i].endTime;
						row.appendChild(cell);
						row.style.cursor = 'pointer';
						row.onclick = () => { window.document.location = '${pageContext.request.contextPath}/view_campaign.jsp?campaign=' + list[i].id };
						table.appendChild(row);
					}
					document.getElementById('campaigns_section').style.display = 'block';
				} else {
					document.getElementById('no_campaigns_section').style.display = 'block';
				}
			});
		}
		//
		init();
	</script>
</body>
</html>