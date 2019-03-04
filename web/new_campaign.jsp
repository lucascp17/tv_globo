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
		<div class="row">
			<div class="col-md-12">
				<h4 class="mb-3">
					Dados da campanha
				</h4>
				<form id="campaign_form" name="campaign_data" action="javascript:sendCreateCampaignRequest()" method="post" class="needs-validation" novalidate>
					<div class="mb-3">
						<label for="campaign_name_input">
							Nome da campanha
						</label>
						<div class="input-group">
							<input id="campaign_name_input" name="campaign_name" type="text" class="form-control" placeholder="Nome da campanha" required autofocus/>
							<div class="invalid-feedback" style="width: 100%;">
								O nome da campanha é exigido
							</div> 
						</div>
					</div>
					<div class="mb-3">
						<label for="campaign_keyword_input">
							Keyword da campanha
						</label>
						<div class="input-group">
							<input id="campaign_keyword_input" name="campaign_keyword" type="text" class="form-control" placeholder="Keyword da campanha" required/>
							<div class="invalid-feedback" style="width: 100%;">
								A keyword da campanha é exigido
							</div> 
						</div>
					</div>
					<div class="mb-3">
						<label for="campaign_vote_items_input">
							Opções de voto (separados por ponto-e-vírgula)
						</label>
						<div class="input-group">
							<input id="campaign_vote_items_input" name="campaign_vote_items" type="text" class="form-control" placeholder="Opções de voto" required/>
							<div class="invalid-feedback" style="width: 100%;">
								As opções de voto são exigidas
							</div> 
						</div>
					</div>
					<div class="mb-3">
						<label for="campaign_date_start_input">
							Data de início
						</label>
						<div class="row">
							<div class="col-md-6">
								<div class="input-group">
									<input id="campaign_date_start_input" name="campaign_start_date" type="date" class="form-control" placeholder="Data de início" required/>
									<div class="invalid-feedback" style="width: 100%;">
										A data de início é exigida
									</div> 
								</div>
							</div>
							<div class="col-md-6">
								<div class="input-group">
									<input id="campaign_time_start_input" name="campaign_start_time" type="time" class="form-control" placeholder="Hora de início" required/>
									<div class="invalid-feedback" style="width: 100%;">
										A hora de início é exigida
									</div> 
								</div>
							</div>
						</div>
					</div>
					<div class="mb-3">
						<label for="campaign_date_end_input">
							Data de encerramento
						</label>
						<div class="row">
							<div class="col-md-6">
								<div class="input-group">
									<input id="campaign_date_end_input" name="campaign_end_date" type="date" class="form-control" placeholder="Data de início" required/>
									<div class="invalid-feedback" style="width: 100%;">
										A data de encerramento é exigida
									</div> 
								</div>
							</div>
							<div class="col-md-6">
								<div class="input-group">
									<input id="campaign_time_end_input" name="campaign_end_time" type="time" class="form-control" placeholder="Hora de início" required/>
									<div class="invalid-feedback" style="width: 100%;">
										A hora de encerramento é exigida
									</div> 
								</div>
							</div>
						</div>
					</div>
					<div class="mb-3">
						<hr class="mb-4" style="margin-top: 50px;"/>
						<div class="row">
							<div class="col-md-6">
								<button class="btn btn-primary btn-lg btn-block" type="submit">
									Criar
								</button>
							</div>
							<div class="col-md-6">
								<button class="btn btn-danger btn-lg btn-block" type="button" onclick="goBack()">
									Voltar
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
		<footer class="my-5 pt-5 text-muted text-center text-small">
		</footer>
	</div>
	<script type="text/javascript" src="engine/core.js"></script>
	<script type="text/javascript">
		function goBack() {
			window.history.back();
		}
		function blockButtons() {
			const buttons = document.getElementsByClassName('btn');
			for (let i = 0; i < buttons.length; ++i)
				buttons[i].disabled = true;
		}
		function sendCreateCampaignRequest() {
			blockButtons();
			let name = document.campaign_data.campaign_name.value;
			let keyword = document.campaign_data.campaign_keyword.value;
			let startDate = document.campaign_data.campaign_start_date.value;
			let startTime = document.campaign_data.campaign_start_time.value;
			let endDate = document.campaign_data.campaign_end_date.value;
			let endTime = document.campaign_data.campaign_end_time.value;
			let options = document.campaign_data.campaign_vote_items.value;
			createService(name, keyword, options, startDate, startTime, endDate, endTime).then(id => {
				window.document.location = '${pageContext.request.contextPath}';
			});
		}
	</script>
</body>
</html>