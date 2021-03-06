<%@ tag pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>

<%@ attribute name="solicitacoes" required="true"
	type="java.lang.Iterable"%>
<%@ attribute name="titulo" required="true"%>
<%@ attribute name="divId" required="true"%>
<%@ attribute name="mensagemVazia" required="true"%>
<%@ attribute name="cor" required="true"%>
<%@ attribute name="registrosPorPagina" required="false"%>
<%@ attribute name="mostraStatus" required="true"%>
<div class="panel ${cor}">
	<div class="panel-heading">
		<h4 class="panel-title">${titulo}${(solicitacoes != null && not empty solicitacoes) ? ' ('.concat(fn:length(solicitacoes)).concat(')') : ' (0)'}
			<small role="button" style="float: right;" data-toggle="collapse"
				data-target="#${divId}"
				onclick="$(this).find('.glyphicon').toggle();"> <i
				class="glyphicon glyphicon-minus"></i><i
				class="glyphicon glyphicon-plus" style="display: none;"></i>
			</small>
		</h4>
	</div>
	<br />
	<div id="${divId}" class="collapse in">
		<c:choose>
			<c:when test="${not empty solicitacoes}">
				<div
					class="table-responsive container-fluid registros-por-pagina-${registrosPorPagina}">
					<table class="table table-condensed compact responsive">
						<thead>
							<tr>
								<th>Paciente</th>
								<th>Data Nascimento</th>
								<th>Data de Atendimento</th>
								<th>Motivo</th>
								<th>Status</th>
								<th>Autor</th>
								<th>Data solicitação</th>
								<th>Telefone</th>
								<th>Celular</th>
								<th>Solicitante</th>
								<th>RG solicitante</th>
								<th></th>
							</tr>
						</thead>
							<tbody>
							<c:forEach items="${solicitacoes}" var="solicitacao">
								<tr>
									<td style="vertical-align: middle;">${solicitacao.nome}</td>
									<td style="vertical-align: middle;"><fmt:formatDate
											pattern="dd/MM/yyyy"
											value="${solicitacao.dataNascimento.time}" /></td>
									<td style="vertical-align: middle;"><fmt:formatDate
											pattern="dd/MM/yyyy"
 											value="${solicitacao.dataAtendimento.time}" /></td>
									<td style="vertical-align: middle;">${solicitacao.motivo}</td>
 									<td style="vertical-align: middle;">${solicitacao.status}</td>
									<td style="vertical-align: middle;">${solicitacao.autor.nome}</td>
									<td style="vertical-align: middle;"><fmt:formatDate
 											pattern="dd/MM/yyyy HH:mm:ss"
 											value="${solicitacao.data.time}" /></td>
									<td style="vertical-align: middle;">${solicitacao.telefone}</td>
									<td style="vertical-align: middle;">${solicitacao.celular}</td>
									<td style="vertical-align: middle;">${solicitacao.nomeSolicitante}</td>
									<td style="vertical-align: middle;">${solicitacao.rgSolicitante}</td>
									
									
									<td style="vertical-align: middle;"><c:url
											value="/a/solicitacao/${solicitacao.id}" var="visualizar" />
											<c:url
											value="/a/solicitacao/${solicitacao.id}/status" var="resposta" />
										<sec:authorize
											access="!hasRole('ROLE_SOLICITANTE') and !hasRole('ROLE_MONITOR')">
											<a class="btn btn-default" href="${visualizar}"><i
												class="glyphicon glyphicon-zoom-in"></i>&nbsp; Visualizar</a>
												<a class="btn btn-default" href="${visualizar}"><i
												class="glyphicon glyphicon-download-alt"></i>&nbsp; Resposta</a>
										</sec:authorize> <sec:authorize
											access="hasRole('ROLE_SOLICITANTE') or hasRole('ROLE_MONITOR')">
											<div class="btn-group">
												<button type="button"
													class="btn btn-sm btn-default dropdown-toggle"
													data-toggle="dropdown" aria-haspopup="true"
													aria-expanded="false">
													<i class="glyphicon glyphicon-cog"></i> Ação <span
														class="caret"></span>
												</button>
												<ul class="dropdown-menu dropdown-menu-right">
													<li><a href="${visualizar}"><i
															class="glyphicon glyphicon-zoom-in"></i>&nbsp; Visualizar</a></li>
													<li><a href="${resposta}"><i
															class="glyphicon glyphicon-download-alt"></i>&nbsp; Resposta</a></li>
												</ul>
											</div>
										</sec:authorize></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:when>
			<c:otherwise>
				<div class="panel-body">${mensagemVazia}</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>