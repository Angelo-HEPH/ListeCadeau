<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau, be.couderiannello.models.Cadeau, be.couderiannello.models.Personne" %>

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
    String error = (String) request.getAttribute("error");
%>

<h2 class="mb-3">Détail de la liste : <%= liste.getTitle() %></h2>

<% if (error != null && !error.isBlank()) { %>
    <div class="alert alert-danger" role="alert">
        <%= error %>
    </div>
<% } %>

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

<!-- ✅ Bloc Invitation -->
<h3 class="mb-3">Inviter une personne</h3>

<form method="post" action="<%= request.getContextPath() %>/liste/detail" class="mb-4">
    <!-- ✅ requis par ta servlet -->
    <input type="hidden" name="action" value="invite">
    <input type="hidden" name="listeId" value="<%= liste.getId() %>">

    <div class="mb-2">
        <label for="email" class="form-label"><b>Email</b></label>
        <input type="email" id="email" name="email" class="form-control" placeholder="ex: quelquun@mail.com" required>
    </div>

    <button type="submit" class="btn btn-warning">
        Inviter
    </button>
</form>

<!-- ✅ Affichage des invités -->
<h3 class="mb-3">Invités</h3>

<%
    List<Personne> invites = liste.getInvites();
    if (invites == null || invites.isEmpty()) {
%>
    <p class="text-muted">Aucun invité pour le moment.</p>
<%
    } else {
%>
    <ul class="list-group mb-4">
    <%
        for (Personne p : invites) {
    %>
        <li class="list-group-item d-flex justify-content-between align-items-center">
            <span>
                <b><%= p.getFirstName() %> <%= p.getName() %></b>
                <% if (p.getEmail() != null) { %>
                    — <%= p.getEmail() %>
                <% } %>
            </span>

            <!-- ✅ Bouton rouge Supprimer -->
            <form method="post" action="<%= request.getContextPath() %>/liste/detail" style="margin:0;">
                <input type="hidden" name="action" value="remove">
                <input type="hidden" name="listeId" value="<%= liste.getId() %>">
                <input type="hidden" name="personneId" value="<%= p.getId() %>">

                <button type="submit" class="btn btn-danger btn-sm"
                        onclick="return confirm('Supprimer cet invité ?');">
                    Supprimer
                </button>
            </form>
        </li>
    <%
        }
    %>
    </ul>
<%
    }
%>

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
