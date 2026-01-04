<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau, be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mes invitations</title>
</head>
<body class="bg-light">

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container my-5">

    <h2 class="mb-4 fw-bold">🎉 Listes auxquelles vous êtes invités :</h2>

<%
    String error = (String) request.getAttribute("error");
    if (error != null && !error.isBlank()) {
%>
    <div class="alert alert-danger text-center fw-bold">
        <%= error %>
    </div>
<%
    }
%>

<%
    List<ListeCadeau> listes = (List<ListeCadeau>) request.getAttribute("listesInvite");
    if (listes == null || listes.isEmpty()) {
%>
    <div class="alert alert-info text-center">
        Tu n’as aucune invitation pour le moment.
    </div>
<%
    } else {
%>

    <div class="row g-4">
    <%
        for (ListeCadeau l : listes) {
            Personne creator = l.getCreator();

            String creatorLabel = "Créateur inconnu";
            if (creator != null) {
                creatorLabel = creator.getFirstName() + " " + creator.getName();
            }

            boolean active = l.isStatut();
    %>
        <div class="col-md-6 col-lg-4">
            <div class="card shadow-sm rounded-4 h-100">

                <div class="card-body d-flex flex-column">

                    <h5 class="card-title fw-bold"><%= l.getTitle() %></h5>

                    <p class="card-text text-muted mb-4">
                        🎉 <b>Événement :</b> <%= l.getEvenement() %><br>
                        📅 <b>Expiration :</b> <%= l.getExpirationDate() %><br>
                        👤 <b>Créée par :</b> <%= creatorLabel %>
                    </p>

                    <div class="mt-auto">
                    <% if (active) { %>
                        <a class="btn btn-primary btn-lg fw-bold w-100"
                           href="<%= request.getContextPath() %>/liste/view?id=<%= l.getId() %>">
                            Ouvrir la liste
                        </a>
                    <% } else { %>
                        <button class="btn btn-secondary btn-lg w-100" type="button" disabled>
                            Pas encore disponible
                        </button>
                        <p class="text-muted mt-2 text-center small">
                            Cette liste n’est pas encore active.
                        </p>
                    <% } %>
                    </div>

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

    <div class="mt-4 text-center">
        <a class="btn btn-secondary btn-lg fw-bold" href="<%= request.getContextPath() %>/home">
            ← Retour à l’accueil
        </a>
    </div>

</div>

</body>
</html>
