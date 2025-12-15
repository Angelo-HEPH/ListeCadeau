<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau, be.couderiannello.models.Cadeau" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Détail liste</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    ListeCadeau liste = (ListeCadeau) request.getAttribute("liste");
%>

<h2 class="mb-3">Détail de la liste : <%= liste.getTitle() %></h2>

<div class="mb-4">
    <p><b>Événement :</b> <%= liste.getEvenement() %></p>
    <p><b>Date de création :</b> <%= liste.getCreationDate() %></p>
    <p><b>Expiration :</b> <%= liste.getExpirationDate() %></p>
    <p><b>Lien de partage :</b>
        <a href="<%= liste.getShareLink() %>" target="_blank">
            <%= liste.getShareLink() %>
        </a>
    </p>
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
    <a href="<%= request.getContextPath() %>/cadeau/create?listeId=<%= liste.getId() %>"
       class="btn btn-success">
        Ajouter un cadeau
    </a>

    <a href="<%= request.getContextPath() %>/liste/all"
       class="btn btn-secondary ms-2">
        Retour aux listes
    </a>
</div>

</div>

</body>
</html>
