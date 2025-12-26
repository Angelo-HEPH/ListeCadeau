<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau, be.couderiannello.models.Cadeau, be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Consultation liste</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    ListeCadeau liste = (ListeCadeau) request.getAttribute("liste");
    Personne creator = liste.getCreator();
%>

<h2 class="mb-3">Liste : <%= liste.getTitle() %></h2>

<div class="mb-4">
    <p><b>Événement :</b> <%= liste.getEvenement() %></p>
	<p><b>Créée par :</b> ${creatorLabel}</p>
    <p><b>Date de création :</b> <%= liste.getCreationDate() %></p>
    <p><b>Expiration :</b> <%= liste.getExpirationDate() %></p>
</div>

<hr>

<h3 class="mb-3">Cadeaux</h3>

<%
    List<Cadeau> cadeaux = liste.getCadeaux();
    if (cadeaux == null || cadeaux.isEmpty()) {
%>
    <p class="text-muted">Aucun cadeau dans cette liste.</p>
<%
    } else {
%>
<div class="row">
<%
        for (Cadeau c : cadeaux) {
%>
    <div class="col-md-4 mb-4">
        <div class="card shadow-sm">
            <img src="<%= c.getPhoto() %>" class="card-img-top" alt="photo cadeau"
                 style="height:180px; object-fit:cover;">

            <div class="card-body">
                <h5 class="card-title"><%= c.getName() %></h5>

                <p class="card-text">
                    <b>Prix :</b> <%= c.getPrice() %> € <br>
                    <b>Priorité :</b> <%= c.getPriorite() %> <br>
                    <b>Description :</b> <%= c.getDescription() %>
                </p>

                <a href="<%= c.getLinkSite() %>" target="_blank"
                   class="btn btn-primary btn-sm">
                    Voir le produit
                </a>

            </div>
        </div>
    </div>
<%
        }
%>
</div>
<%
    }
%>

<div class="mt-4">
    <a href="<%= request.getContextPath() %>/liste/invitations" class="btn btn-secondary">
        Retour aux invitations
    </a>
</div>

</div>
</body>
</html>
